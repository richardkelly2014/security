package com.queue;


import java.util.AbstractQueue;
import java.util.Iterator;

import static com.map.UnsafeAccess.UNSAFE;
import static com.map.UnsafeAccess.fieldOffset;

abstract class BaseLinkedQueuePad0<E> extends AbstractQueue<E> implements MessagePassingQueue<E> {
    long p00, p01, p02, p03, p04, p05, p06, p07;
    long p10, p11, p12, p13, p14, p15, p16;
}

//ordered-fields 生产
abstract class BaseLinkedQueueProducerNodeRef<E> extends BaseLinkedQueuePad0<E> {
    // 变量 偏移量
    final static long P_NODE_OFFSET = fieldOffset(BaseLinkedQueueProducerNodeRef.class, "producerNode");

    private LinkedQueueNode<E> producerNode;

    final void spProducerNode(LinkedQueueNode<E> newValue) {
        producerNode = newValue;
    }

    @SuppressWarnings("unchecked")
    final LinkedQueueNode<E> lvProducerNode() {
        return (LinkedQueueNode<E>) UNSAFE.getObjectVolatile(this, P_NODE_OFFSET);
    }

    @SuppressWarnings("unchecked")
    final boolean casProducerNode(LinkedQueueNode<E> expect, LinkedQueueNode<E> newValue) {
        return UNSAFE.compareAndSwapObject(this, P_NODE_OFFSET, expect, newValue);
    }

    final LinkedQueueNode<E> lpProducerNode() {
        return producerNode;
    }
}

abstract class BaseLinkedQueuePad1<E> extends BaseLinkedQueueProducerNodeRef<E> {
    long p01, p02, p03, p04, p05, p06, p07;
    long p10, p11, p12, p13, p14, p15, p16, p17;
}

//ordered-fields 消费
abstract class BaseLinkedQueueConsumerNodeRef<E> extends BaseLinkedQueuePad1<E> {
    private final static long C_NODE_OFFSET = fieldOffset(BaseLinkedQueueConsumerNodeRef.class, "consumerNode");

    private LinkedQueueNode<E> consumerNode;

    final void spConsumerNode(LinkedQueueNode<E> newValue) {
        consumerNode = newValue;
    }

    @SuppressWarnings("unchecked")
    final LinkedQueueNode<E> lvConsumerNode() {
        return (LinkedQueueNode<E>) UNSAFE.getObjectVolatile(this, C_NODE_OFFSET);
    }

    final LinkedQueueNode<E> lpConsumerNode() {
        return consumerNode;
    }
}

abstract class BaseLinkedQueuePad2<E> extends BaseLinkedQueueConsumerNodeRef<E> {
    long p01, p02, p03, p04, p05, p06, p07;
    long p10, p11, p12, p13, p14, p15, p16, p17;
}

/**
 * base linked queue
 *
 * @param <E>
 */
abstract class BaseLinkedQueue<E> extends BaseLinkedQueuePad2<E> {

    @Override
    public final Iterator<E> iterator() {

        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {

        return this.getClass().getName();
    }


    protected final LinkedQueueNode<E> newNode() {
        return new LinkedQueueNode<E>();
    }

    protected final LinkedQueueNode<E> newNode(E e) {
        return new LinkedQueueNode<E>(e);
    }


    @Override
    public final int size() {
        // Read consumer first, this is important because if the producer is node is 'older' than the consumer
        // the consumer may overtake it (consume past it) invalidating the 'snapshot' notion of size.
        // 从消费者，找到生产者
        LinkedQueueNode<E> chaserNode = lvConsumerNode();
        LinkedQueueNode<E> producerNode = lvProducerNode();
        int size = 0;
        // must chase the nodes all the way to the producer node, but there's no need to count beyond expected head.
        while (chaserNode != producerNode && // don't go passed producer node
                chaserNode != null && // stop at last node
                size < Integer.MAX_VALUE) // stop at max int
        {
            LinkedQueueNode<E> next;
            next = chaserNode.lvNext();
            // check if this node has been consumed, if so return what we have
            if (next == chaserNode) {
                return size;
            }
            chaserNode = next;
            size++;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return lvConsumerNode() == lvProducerNode();
    }


    protected E getSingleConsumerNodeValue(LinkedQueueNode<E> currConsumerNode, LinkedQueueNode<E> nextNode) {
        // we have to null out the value because we are going to hang on to the node
        final E nextValue = nextNode.getAndNullValue();

        // Fix up the next ref of currConsumerNode to prevent promoted nodes from keeping new ones alive.
        // We use a reference to self instead of null because null is already a meaningful value (the next of
        // producer node is null).
        currConsumerNode.soNext(currConsumerNode);
        spConsumerNode(nextNode);
        // currConsumerNode is now no longer referenced and can be collected
        return nextValue;
    }

    @Override
    public E relaxedPoll() {
        final LinkedQueueNode<E> currConsumerNode = lpConsumerNode();
        final LinkedQueueNode<E> nextNode = currConsumerNode.lvNext();
        if (nextNode != null) {
            return getSingleConsumerNodeValue(currConsumerNode, nextNode);
        }
        return null;
    }

    @Override
    public E relaxedPeek() {
        final LinkedQueueNode<E> nextNode = lpConsumerNode().lvNext();
        if (nextNode != null) {
            return nextNode.lpValue();
        }
        return null;
    }

    @Override
    public boolean relaxedOffer(E e) {
        return offer(e);
    }

    @Override
    public int drain(Consumer<E> c, int limit) {
        if (null == c)
            throw new IllegalArgumentException("c is null");
        if (limit < 0)
            throw new IllegalArgumentException("limit is negative: " + limit);
        if (limit == 0)
            return 0;

        LinkedQueueNode<E> chaserNode = this.lpConsumerNode();
        for (int i = 0; i < limit; i++) {
            final LinkedQueueNode<E> nextNode = chaserNode.lvNext();

            if (nextNode == null) {
                return i;
            }
            // we have to null out the value because we are going to hang on to the node
            final E nextValue = getSingleConsumerNodeValue(chaserNode, nextNode);
            chaserNode = nextNode;
            c.accept(nextValue);
        }
        return limit;
    }

    @Override
    public int drain(Consumer<E> c) {
        return MessagePassingQueueUtil.drain(this, c);
    }

    @Override
    public void drain(Consumer<E> c, WaitStrategy wait, ExitCondition exit) {
        MessagePassingQueueUtil.drain(this, c, wait, exit);
    }

    @Override
    public int capacity() {
        return UNBOUNDED_CAPACITY;
    }
}
