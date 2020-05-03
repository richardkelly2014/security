package com.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.map.PortableJvmInfo;
import com.map.Pow2;
import com.map.RangeUtil;
import com.queue.IndexedQueueSizeUtil.*;

import static com.map.UnsafeAccess.UNSAFE;
import static com.map.UnsafeAccess.fieldOffset;
import static com.map.UnsafeRefArrayAccess.*;
import static com.queue.LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset;


import static com.queue.LinkedArrayQueueUtil.*;

/**
 * Created by jiangfei on 2020/5/2.
 */

abstract class BaseMpscLinkedArrayQueuePad1<E> extends AbstractQueue<E> implements IndexedQueue {
    long p01, p02, p03, p04, p05, p06, p07;
    long p10, p11, p12, p13, p14, p15, p16, p17;
}

/**
 * 生产者
 *
 * @param <E>
 */
abstract class BaseMpscLinkedArrayQueueProducerFields<E> extends BaseMpscLinkedArrayQueuePad1<E> {
    //生产者 index 偏移量
    private final static long P_INDEX_OFFSET = fieldOffset(BaseMpscLinkedArrayQueueProducerFields.class, "producerIndex");

    private volatile long producerIndex;

    /**
     * get
     *
     * @return
     */
    @Override
    public final long lvProducerIndex() {
        return producerIndex;
    }

    /**
     * set
     *
     * @param newValue
     */
    final void soProducerIndex(long newValue) {
        UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, newValue);
    }

    /**
     * cas
     *
     * @param expect
     * @param newValue
     * @return
     */
    final boolean casProducerIndex(long expect, long newValue) {
        return UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, expect, newValue);
    }
}

abstract class BaseMpscLinkedArrayQueuePad2<E> extends BaseMpscLinkedArrayQueueProducerFields<E> {
    long p01, p02, p03, p04, p05, p06, p07;
    long p10, p11, p12, p13, p14, p15, p16, p17;
}

/**
 * 消费
 *
 * @param <E>
 */
abstract class BaseMpscLinkedArrayQueueConsumerFields<E> extends BaseMpscLinkedArrayQueuePad2<E> {
    private final static long C_INDEX_OFFSET = fieldOffset(BaseMpscLinkedArrayQueueConsumerFields.class, "consumerIndex");
    //消费者索引
    private volatile long consumerIndex;
    //标记 可能是数组长度-1
    protected long consumerMask;
    //消费数组
    protected E[] consumerBuffer;

    /**
     * get 消费者索引
     *
     * @return
     */
    @Override
    public final long lvConsumerIndex() {
        return consumerIndex;
    }

    final long lpConsumerIndex() {
        return UNSAFE.getLong(this, C_INDEX_OFFSET);
    }

    final void soConsumerIndex(long newValue) {
        UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, newValue);
    }
}

abstract class BaseMpscLinkedArrayQueuePad3<E> extends BaseMpscLinkedArrayQueueConsumerFields<E> {
    long p0, p1, p2, p3, p4, p5, p6, p7;
    long p10, p11, p12, p13, p14, p15, p16, p17;
}

/**
 * 生产者
 *
 * @param <E>
 */
abstract class BaseMpscLinkedArrayQueueColdProducerFields<E> extends BaseMpscLinkedArrayQueuePad3<E> {
    private final static long P_LIMIT_OFFSET = fieldOffset(BaseMpscLinkedArrayQueueColdProducerFields.class, "producerLimit");
    //生产者 limit
    private volatile long producerLimit;
    //mask
    protected long producerMask;
    // 生产者数组
    protected E[] producerBuffer;

    final long lvProducerLimit() {
        return producerLimit;
    }

    final boolean casProducerLimit(long expect, long newValue) {
        return UNSAFE.compareAndSwapLong(this, P_LIMIT_OFFSET, expect, newValue);
    }

    final void soProducerLimit(long newValue) {
        UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, newValue);
    }
}

/**
 * 多生产者，单消费者
 *
 * @param <E>
 */
public abstract class BaseMpscLinkedArrayQueue<E> extends BaseMpscLinkedArrayQueueColdProducerFields<E>
        implements MessagePassingQueue<E>, QueueProgressIndicators {

    private static final Object JUMP = new Object();
    private static final Object BUFFER_CONSUMED = new Object(); //已经消费 标示
    private static final int CONTINUE_TO_P_INDEX_CAS = 0;
    private static final int RETRY = 1;
    private static final int QUEUE_FULL = 2;
    private static final int QUEUE_RESIZE = 3;

    public BaseMpscLinkedArrayQueue(final int initialCapacity) {
        //2的N次方
        RangeUtil.checkGreaterThanOrEqual(initialCapacity, 2, "initialCapacity");

        int p2capacity = Pow2.roundToPowerOfTwo(initialCapacity);
        // leave lower bit of mask clear
        long mask = (p2capacity - 1) << 1;
        // need extra element to point at next array
        E[] buffer = allocateRefArray(p2capacity + 1);
        producerBuffer = buffer;
        producerMask = mask;
        consumerBuffer = buffer;
        consumerMask = mask;
        soProducerLimit(mask); // we know it's all empty to start with
    }

    @Override
    public int size() {
        // NOTE: because indices are on even numbers we cannot use the size util.

        /*
         * It is possible for a thread to be interrupted or reschedule between the read of the producer and
         * consumer indices, therefore protection is required to ensure size is within valid range. In the
         * event of concurrent polls/offers to this method the size is OVER estimated as we read consumer
         * index BEFORE the producer index.
         */
        //当前消费索引
        long after = lvConsumerIndex();
        long size;
        while (true) {
            //当前消费者
            final long before = after;
            //当前生产者索引
            final long currentProducerIndex = lvProducerIndex();
            // 当前消费者
            after = lvConsumerIndex();
            //消费者位置
            if (before == after) {
                size = ((currentProducerIndex - after) >> 1);
                break;
            }
        }
        // Long overflow is impossible, so size is always positive. Integer overflow is possible for the unbounded
        // indexed queues.
        if (size > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) size;
        }
    }

    /**
     * 空
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        // Order matters!
        // Loading consumer before producer allows for producer increments after consumer index is read.
        // This ensures this method is conservative in it's estimate. Note that as this is an MPMC there is
        // nothing we can do to make this an exact method.
        return (this.lvConsumerIndex() == this.lvProducerIndex());
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    /**
     * 添加
     *
     * @param e
     * @return
     */
    @Override
    public boolean offer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }

        long mask;
        E[] buffer;
        long pIndex;

        while (true) {
            //生产者 limit
            long producerLimit = lvProducerLimit();
            //当前生产者索引
            pIndex = lvProducerIndex();
            // lower bit is indicative of resize, if we see it we spin until it's cleared
            if ((pIndex & 1) == 1) {
                //需要扩展大小，让其他线程等待，等待扩展完毕在继续
                continue;
            }
            // pIndex is even (lower bit is 0) -> actual index is (pIndex >> 1)

            // mask/buffer may get changed by resizing -> only use for array access after successful CAS.
            mask = this.producerMask;           //当前buffer mask
            buffer = this.producerBuffer;
            // a successful CAS ties the ordering, lv(pIndex) - [mask/buffer] -> cas(pIndex)

            // assumption behind this optimization is that queue is almost always empty or near empty

            //判断索引与 limit
            if (producerLimit <= pIndex) {
                int result = offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case CONTINUE_TO_P_INDEX_CAS:
                        break;
                    case RETRY:
                        continue;
                    case QUEUE_FULL:
                        return false;
                    case QUEUE_RESIZE:
                        resize(mask, buffer, pIndex, e, null);
                        return true;
                }
            }

            if (casProducerIndex(pIndex, pIndex + 2)) {
                break;
            }
        }
        // INDEX visible before ELEMENT
        // 根据index 换算offset (pIndex + 2 & mask) << (2-1)
        final long offset = modifiedCalcCircularRefElementOffset(pIndex, mask);

        // unsafe 操作
        soRefElement(buffer, offset, e); // release element e
        return true;
    }

    /**
     * 取数据
     *
     * @return
     */
    @Override
    public E poll() {
        // 当前 消费 buffer
        final E[] buffer = consumerBuffer;
        // 消费索引
        final long index = lpConsumerIndex();
        // 消费mask
        final long mask = consumerMask;

        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        // 取出数组元素
        Object e = lvRefElement(buffer, offset);
        if (e == null) {
            if (index != lvProducerIndex()) {
                // poll() == null iff queue is empty, null element is not strong enough indicator, so we must
                // check the producer index. If the queue is indeed not empty we spin until element is
                // visible.
                do {
                    // 如果 索引和 生产者不一致，直到取到数据位置
                    e = lvRefElement(buffer, offset);
                }
                while (e == null);
            } else {
                return null;
            }
        }

        if (e == JUMP) {
            // 取到 跳转 标记
            final E[] nextBuffer = nextBuffer(buffer, mask);
            return newBufferPoll(nextBuffer, index);
        }
        // 数组 相应位置元素设置为null，释放对象
        soRefElement(buffer, offset, null); // release element null
        // 消费索引 + 2
        soConsumerIndex(index + 2); // release cIndex
        return (E) e;
    }

    @Override
    public E peek() {
        final E[] buffer = consumerBuffer;
        final long index = lpConsumerIndex();
        final long mask = consumerMask;

        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        Object e = lvRefElement(buffer, offset);
        if (e == null && index != lvProducerIndex()) {
            // peek() == null iff queue is empty, null element is not strong enough indicator, so we must
            // check the producer index. If the queue is indeed not empty we spin until element is visible.
            do {
                e = lvRefElement(buffer, offset);
            }
            while (e == null);
        }
        if (e == JUMP) {
            return newBufferPeek(nextBuffer(buffer, mask), index);
        }
        return (E) e;
    }

    /**
     * We do not inline resize into this method because we do not resize on fill
     *
     * @param mask
     * @param pIndex
     * @param producerLimit
     * @return
     */
    private int offerSlowPath(long mask, long pIndex, long producerLimit) {

        //当前消费者 索引
        final long cIndex = lvConsumerIndex();
        //当前buffer 数组容量
        long bufferCapacity = getCurrentBufferCapacity(mask);

        // 消费者 + buffer >
        if (cIndex + bufferCapacity > pIndex) {
            // 设置 生产者 limit
            if (!casProducerLimit(producerLimit, cIndex + bufferCapacity)) {
                // retry from top
                return RETRY;
            } else {
                // continue to pIndex CAS
                return CONTINUE_TO_P_INDEX_CAS;
            }
        }
        // full and cannot grow
        else if (availableInQueue(pIndex, cIndex) <= 0) {
            // offer should return false;
            return QUEUE_FULL;
        }
        // grab index for resize -> set lower bit
        //抓取索引以调整大小->设置低位
        else if (casProducerIndex(pIndex, pIndex + 1)) {
            // trigger a resize
            return QUEUE_RESIZE;
        } else {
            // failed resize attempt, retry from top
            return RETRY;
        }
    }

    /**
     * 可使用的
     *
     * @param pIndex
     * @param cIndex
     * @return
     */
    protected abstract long availableInQueue(long pIndex, long cIndex);

    /**
     * 下一个 buffer
     *
     * @param buffer
     * @param mask
     * @return
     */
    private E[] nextBuffer(final E[] buffer, final long mask) {
        final long offset = nextArrayOffset(mask);
        // next buffer
        final E[] nextBuffer = (E[]) lvRefElement(buffer, offset);
        consumerBuffer = nextBuffer;
        consumerMask = (length(nextBuffer) - 2) << 1;
        //buffer 最后一个元素 设置已经消费标示
        soRefElement(buffer, offset, BUFFER_CONSUMED);
        return nextBuffer;
    }

    /**
     * 数组位置
     *
     * @param mask
     * @return
     */
    private static long nextArrayOffset(long mask) {
        return modifiedCalcCircularRefElementOffset(mask + 2, Long.MAX_VALUE);
    }

    /**
     * new  buffer poll
     *
     * @param nextBuffer
     * @param index
     * @return
     */
    private E newBufferPoll(E[] nextBuffer, long index) {
        final long offset = modifiedCalcCircularRefElementOffset(index, consumerMask);
        final E n = lvRefElement(nextBuffer, offset);
        if (n == null) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        // 取完设置null ，释放回收
        soRefElement(nextBuffer, offset, null);
        // 消费index + 2
        soConsumerIndex(index + 2);
        return n;
    }

    private E newBufferPeek(E[] nextBuffer, long index) {
        final long offset = modifiedCalcCircularRefElementOffset(index, consumerMask);
        final E n = lvRefElement(nextBuffer, offset);
        if (null == n) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        return n;
    }

    @Override
    public long currentProducerIndex() {
        return lvProducerIndex() / 2;
    }

    @Override
    public long currentConsumerIndex() {
        return lvConsumerIndex() / 2;
    }

    @Override
    public abstract int capacity();

    @Override
    public boolean relaxedOffer(E e) {
        return offer(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E relaxedPoll() {
        final E[] buffer = consumerBuffer;
        final long index = lpConsumerIndex();
        final long mask = consumerMask;

        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        Object e = lvRefElement(buffer, offset);
        if (e == null) {
            return null;
        }
        if (e == JUMP) {
            final E[] nextBuffer = nextBuffer(buffer, mask);
            return newBufferPoll(nextBuffer, index);
        }
        soRefElement(buffer, offset, null);
        soConsumerIndex(index + 2);
        return (E) e;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E relaxedPeek() {
        final E[] buffer = consumerBuffer;
        final long index = lpConsumerIndex();
        final long mask = consumerMask;

        final long offset = modifiedCalcCircularRefElementOffset(index, mask);
        Object e = lvRefElement(buffer, offset);
        if (e == JUMP) {
            return newBufferPeek(nextBuffer(buffer, mask), index);
        }
        return (E) e;
    }

    @Override
    public int fill(Supplier<E> s) {
        long result = 0;// result is a long because we want to have a safepoint check at regular intervals
        final int capacity = capacity();
        do {
            final int filled = fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH);
            if (filled == 0) {
                return (int) result;
            }
            result += filled;
        }
        while (result <= capacity);
        return (int) result;
    }

    @Override
    public int fill(Supplier<E> s, int limit) {
        if (null == s)
            throw new IllegalArgumentException("supplier is null");
        if (limit < 0)
            throw new IllegalArgumentException("limit is negative:" + limit);
        if (limit == 0)
            return 0;

        long mask;
        E[] buffer;
        long pIndex;
        int claimedSlots;
        while (true) {
            long producerLimit = lvProducerLimit();
            pIndex = lvProducerIndex();
            // lower bit is indicative of resize, if we see it we spin until it's cleared
            if ((pIndex & 1) == 1) {
                continue;
            }
            // pIndex is even (lower bit is 0) -> actual index is (pIndex >> 1)

            // NOTE: mask/buffer may get changed by resizing -> only use for array access after successful CAS.
            // Only by virtue offloading them between the lvProducerIndex and a successful casProducerIndex are they
            // safe to use.
            mask = this.producerMask;
            buffer = this.producerBuffer;
            // a successful CAS ties the ordering, lv(pIndex) -> [mask/buffer] -> cas(pIndex)

            // we want 'limit' slots, but will settle for whatever is visible to 'producerLimit'
            long batchIndex = Math.min(producerLimit, pIndex + 2l * limit); //  -> producerLimit >= batchIndex

            if (pIndex >= producerLimit) {
                int result = offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case CONTINUE_TO_P_INDEX_CAS:
                        // offer slow path verifies only one slot ahead, we cannot rely on indication here
                    case RETRY:
                        continue;
                    case QUEUE_FULL:
                        return 0;
                    case QUEUE_RESIZE:
                        resize(mask, buffer, pIndex, null, s);
                        return 1;
                }
            }

            // claim limit slots at once
            if (casProducerIndex(pIndex, batchIndex)) {
                claimedSlots = (int) ((batchIndex - pIndex) / 2);
                break;
            }
        }

        for (int i = 0; i < claimedSlots; i++) {
            final long offset = modifiedCalcCircularRefElementOffset(pIndex + 2l * i, mask);
            soRefElement(buffer, offset, s.get());
        }
        return claimedSlots;
    }

    @Override
    public void fill(Supplier<E> s, WaitStrategy wait, ExitCondition exit) {
        MessagePassingQueueUtil.fill(this, s, wait, exit);
    }

    @Override
    public int drain(Consumer<E> c) {
        return drain(c, capacity());
    }

    @Override
    public int drain(Consumer<E> c, int limit) {
        return MessagePassingQueueUtil.drain(this, c, limit);
    }

    @Override
    public void drain(Consumer<E> c, WaitStrategy wait, ExitCondition exit) {
        MessagePassingQueueUtil.drain(this, c, wait, exit);
    }

    /**
     * Get an iterator for this queue. This method is thread safe.
     * <p>
     * The iterator provides a best-effort snapshot of the elements in the queue.
     * The returned iterator is not guaranteed to return elements in queue order,
     * and races with the consumer thread may cause gaps in the sequence of returned elements.
     * Like {link #relaxedPoll}, the iterator may not immediately return newly inserted elements.
     *
     * @return The iterator.
     */
    @Override
    public Iterator<E> iterator() {
        return new WeakIterator(consumerBuffer, lvConsumerIndex(), lvProducerIndex());
    }

    private static class WeakIterator<E> implements Iterator<E> {
        private final long pIndex;
        private long nextIndex;
        private E nextElement;
        private E[] currentBuffer;
        private int mask;

        WeakIterator(E[] currentBuffer, long cIndex, long pIndex) {
            this.pIndex = pIndex >> 1;
            this.nextIndex = cIndex >> 1;
            setBuffer(currentBuffer);
            nextElement = getNext();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public boolean hasNext() {
            return nextElement != null;
        }

        @Override
        public E next() {
            final E e = nextElement;
            if (e == null) {
                throw new NoSuchElementException();
            }
            nextElement = getNext();
            return e;
        }

        private void setBuffer(E[] buffer) {
            this.currentBuffer = buffer;
            this.mask = length(buffer) - 2;
        }

        private E getNext() {
            while (nextIndex < pIndex) {
                long index = nextIndex++;
                E e = lvRefElement(currentBuffer, calcCircularRefElementOffset(index, mask));
                // skip removed/not yet visible elements
                if (e == null) {
                    continue;
                }

                // not null && not JUMP -> found next element
                if (e != JUMP) {
                    return e;
                }

                // need to jump to the next buffer
                int nextBufferIndex = mask + 1;
                Object nextBuffer = lvRefElement(currentBuffer,
                        calcRefElementOffset(nextBufferIndex));

                if (nextBuffer == BUFFER_CONSUMED || nextBuffer == null) {
                    // Consumer may have passed us, or the next buffer is not visible yet: drop out early
                    return null;
                }

                setBuffer((E[]) nextBuffer);
                // now with the new array retry the load, it can't be a JUMP, but we need to repeat same index
                e = lvRefElement(currentBuffer, calcCircularRefElementOffset(index, mask));
                // skip removed/not yet visible elements
                if (e == null) {
                    continue;
                } else {
                    return e;
                }

            }
            return null;
        }
    }

    /**
     * 重新变大小
     *
     * @param oldMask
     * @param oldBuffer
     * @param pIndex
     * @param e
     * @param s
     */
    private void resize(long oldMask, E[] oldBuffer, long pIndex, E e, Supplier<E> s) {

        assert (e != null && s == null) || (e == null || s != null);
        // 获取 old 数组 大小
        int newBufferLength = getNextBufferSize(oldBuffer);

        final E[] newBuffer;
        try {
            // 从新分配
            newBuffer = allocateRefArray(newBufferLength);
        } catch (OutOfMemoryError oom) {
            assert lvProducerIndex() == pIndex + 1;
            soProducerIndex(pIndex);
            throw oom;
        }

        producerBuffer = newBuffer;
        final int newMask = (newBufferLength - 2) << 1;
        producerMask = newMask;
        // old offset
        final long offsetInOld = modifiedCalcCircularRefElementOffset(pIndex, oldMask);
        // new offset
        final long offsetInNew = modifiedCalcCircularRefElementOffset(pIndex, newMask);
        // set new offset
        soRefElement(newBuffer, offsetInNew, e == null ? s.get() : e);// element in new array
        // 老数组 oldMask+2位置放入新 数组引用,最后一个位置
        soRefElement(oldBuffer, nextArrayOffset(oldMask), newBuffer);// buffer linked

        // ASSERT code
        final long cIndex = lvConsumerIndex();
        final long availableInQueue = availableInQueue(pIndex, cIndex);
        RangeUtil.checkPositive(availableInQueue, "availableInQueue");

        // Invalidate racing CASs
        // We never set the limit beyond the bounds of a buffer
        soProducerLimit(pIndex + Math.min(newMask, availableInQueue));

        // make resize visible to the other producers
        soProducerIndex(pIndex + 2);

        // INDEX visible before ELEMENT, consistent with consumer expectation

        // make resize visible to consumer
        // old offset 设置 jump 数据
        soRefElement(oldBuffer, offsetInOld, JUMP);
    }


    protected abstract int getNextBufferSize(E[] buffer);

    protected abstract long getCurrentBufferCapacity(long mask);
}
