package com.algorithm.array;

public class StaticArray<AnyType> {

    protected int size;
    protected AnyType[] data;

    public StaticArray() {
        this(10);
    }

    public StaticArray(int capacity) {
        data = (AnyType[]) new Object[capacity];
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return data.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void addLast(AnyType e) {
        add(getSize(), e);
    }

    public void addFirst(AnyType e) {
        add(0, e);
    }

    public void add(int index, AnyType e) {

        if (size == data.length) {
            throw new IllegalArgumentException("add failed. Array is full.");
        }

        if (index < 0 || index > size) {
            throw new IllegalArgumentException("add failed. Array is full.");
        }

        for (int i = size - 1; i >= index; i--) {
            data[i + 1] = data[i];
        }
        data[index] = e;
        size++;
    }

    public AnyType get(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Get failed. Index is illegal.");
        }
        return data[index];
    }

    public void set(int index, AnyType e) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Set failed. Index is illegal.");
        }
        data[index] = e;
    }

    public boolean contains(AnyType e) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(e)) {
                return true;
            }
        }
        return false;
    }

    public int find(AnyType e) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(e)) {
                return i;
            }
        }
        return -1;
    }

    public AnyType remove(int index) {

        if (index < 0 || index > size) {
            throw new IllegalArgumentException("remove failed. Index is illegal.");
        }
        AnyType ret = data[index];
        for (int i = index + 1; i < size; i++) {
            data[i - 1] = data[i];
        }
        size--;

        // 该行可选
        data[size] = null; // loitering objects != memory leak

        return ret;
    }


    // 从数组中删除第一个元素，返回删除的元素
    public AnyType removeFirst() {

        return remove(0);
    }

    // 从数组中删除最后元素，返回删除的元素
    public AnyType removeLast() {

        return remove(size - 1);
    }

    // 从数组中删除元素e
    public void removeElement(AnyType e) {
        int index = find(e);
        if (index != -1) {
            remove(index);
        }
    }

}
