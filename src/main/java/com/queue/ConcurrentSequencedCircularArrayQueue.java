package com.queue;

import static com.map.UnsafeLongArrayAccess.allocateLongArray;
import static com.map.UnsafeLongArrayAccess.calcCircularLongElementOffset;
import static com.map.UnsafeLongArrayAccess.soLongElement;

/**
 * Created by jiangfei on 2020/5/3.
 */
public abstract class ConcurrentSequencedCircularArrayQueue<E> extends ConcurrentCircularArrayQueue<E> {

    //序列
    protected final long[] sequenceBuffer;

    public ConcurrentSequencedCircularArrayQueue(int capacity) {
        super(capacity);
        int actualCapacity = (int) (this.mask + 1);
        // pad data on either end with some empty slots. Note that actualCapacity is <= MAX_POW2_INT
        sequenceBuffer = allocateLongArray(actualCapacity);
        for (long i = 0; i < actualCapacity; i++) {
            soLongElement(sequenceBuffer, calcCircularLongElementOffset(i, mask), i);
        }
    }
}
