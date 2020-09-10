package com.algorithm.array;

public class DynamicArray<AnyType> extends StaticArray<AnyType> {

    public DynamicArray() {
        super();
    }

    public DynamicArray(int capacity) {
        super(capacity);
    }

    public AnyType getLast() {
        return get(size - 1);
    }

    public AnyType getFirst() {
        return get(0);
    }

    public void add(int index, AnyType e) {

        if (index < 0 || index > size) {
            throw new IllegalArgumentException("add failed. Require index >=0 and index < size.");
        }
        if (size == data.length) {
            resize(2 * data.length);
        }


        for (int i = size - 1; i >= index; i--) {
            data[i + 1] = data[i];
        }
        data[index] = e;
        size++;
    }

    private void resize(int newCapacity) {
        AnyType[] newData = (AnyType[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }
}
