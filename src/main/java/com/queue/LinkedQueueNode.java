package com.queue;

import static com.map.UnsafeAccess.UNSAFE;
import static com.map.UnsafeAccess.fieldOffset;

public final class LinkedQueueNode<E> {

    private final static long NEXT_OFFSET = fieldOffset(LinkedQueueNode.class, "next");

    private E value;
    private volatile LinkedQueueNode<E> next;

    LinkedQueueNode() {
        this(null);
    }

    LinkedQueueNode(E val) {

        spValue(val);
    }

    public E getAndNullValue() {
        E temp = lpValue();
        spValue(null);
        return temp;
    }

    /**
     * load
     *
     * @return
     */
    public E lpValue() {

        return value;
    }

    /**
     * store put
     *
     * @param newValue
     */
    public void spValue(E newValue) {

        value = newValue;
    }

    /**
     * store offset
     *
     * @param n
     */
    public void soNext(LinkedQueueNode<E> n) {

        UNSAFE.putOrderedObject(this, NEXT_OFFSET, n);
    }

    /**
     * load
     *
     * @return
     */
    public LinkedQueueNode<E> lvNext() {

        return next;
    }
}
