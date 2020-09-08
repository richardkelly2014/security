package com.list;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayList<AnyType> implements Iterable<AnyType> {

    private static final int DEFAULT_CAPACITY = 10;
    private int theSize;
    private AnyType[] theItems;

    public MyArrayList() {
        clear();
        ensureCapacity(DEFAULT_CAPACITY);
    }

    public void clear() {
        theSize = 0;
    }

    public int size() {
        return theSize;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void ensureCapacity(int newCapacity) {
        if (newCapacity < theSize) {
            return;
        }

        AnyType[] old = theItems;
        theItems = (AnyType[]) new Object[newCapacity];
        for (int i = 0, size = size(); i < size; i++) {
            theItems[i] = old[i];
        }
    }

    public boolean add(AnyType x) {
        add(size(), x);
        return true;
    }

    public void add(int idx, AnyType x) {
        if (theItems.length == size()) {
            ensureCapacity(size() * 2 + 1);
        }

        for (int i = theSize; i > idx; i--) {
            theItems[i] = theItems[i - 1];
        }

        theItems[idx] = x;
        theSize++;
    }

    public AnyType remove(int idx) {
        AnyType removeItem = theItems[idx];
        for (int i = idx; i < size() - 1; i++) {
            theItems[i] = theItems[i + 1];
        }
        theSize--;
        return removeItem;
    }

    public AnyType get(int idx) {
        if (idx >= 0 && idx < size()) {
            return theItems[idx];
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public AnyType set(int idx, AnyType newVal) {
        if (idx >= 0 && idx < size()) {
            AnyType old = theItems[idx];
            theItems[idx] = newVal;
            return old;
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }


    @Override
    public Iterator<AnyType> iterator() {
        return null;
    }

    private class ArrayListIterator implements Iterator<AnyType> {

        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < size();
        }

        @Override
        public AnyType next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return theItems[current++];
        }

        public void remove() {
            MyArrayList.this.remove(--current);
        }
    }
}
