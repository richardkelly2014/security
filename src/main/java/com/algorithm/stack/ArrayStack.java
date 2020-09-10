package com.algorithm.stack;


import com.algorithm.array.DynamicArray;

public class ArrayStack<AnyType> implements Stack<AnyType> {

    private DynamicArray<AnyType> array;

    public ArrayStack(int capacity) {
        array = new DynamicArray<>(capacity);
    }

    public ArrayStack() {
        array = new DynamicArray<>();
    }

    @Override
    public int getSize() {
        return array.getSize();
    }

    @Override
    public boolean isEmpty() {
        return array.isEmpty();
    }

    public int getCapacity() {
        return array.getCapacity();
    }

    @Override
    public void push(AnyType e) {
        array.addLast(e);
    }

    @Override
    public AnyType pop() {
        return array.removeLast();
    }

    @Override
    public AnyType peek() {
        return array.getLast();
    }
}
