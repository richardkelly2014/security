package com.list;

import java.util.LinkedList;
import java.util.List;

public class SeparateChainingHashTable<AnyType> {

    private static final int DEFAULT_TABLE_SIZE = 101;
    private List<AnyType>[] theList;
    private int currentSize;

    public SeparateChainingHashTable() {
        this(DEFAULT_TABLE_SIZE);
    }

    public SeparateChainingHashTable(int size) {
        theList = new LinkedList[nextPrime(size)];
        for (int i = 0; i < theList.length; i++) {
            theList[i] = new LinkedList<AnyType>();
        }

    }

    public void insert(AnyType x) {
        List<AnyType> whichList = theList[myhash(x)];
        if (!whichList.contains(x)) {
            whichList.add(x);
            if (++currentSize > theList.length) {
                rehash();
            }
        }
    }

    public void remove(AnyType x) {
        List<AnyType> whichList = theList[myhash(x)];
        if (whichList.contains(x)) {
            whichList.remove(x);
            currentSize--;
        }
    }

    public boolean contains(AnyType x) {
        List<AnyType> whichList = theList[myhash(x)];
        return whichList.contains(x);
    }

    public void makeEmpty() {
        for (int i = 0; i < theList.length; i++) {
            theList[i] = new LinkedList<AnyType>();
        }

    }

    private void rehash() {
    }

    private int myhash(AnyType x) {
        int hashVal = x.hashCode();
        hashVal %= theList.length;
        if (hashVal < 0) {
            hashVal += theList.length;
        }
        return hashVal;
    }

    private static int nextPrime(int n) {
        return n;
    }

    private static boolean isPrime(int n) {
        return true;
    }

}
