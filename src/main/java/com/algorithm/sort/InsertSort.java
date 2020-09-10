package com.algorithm.sort;

public class InsertSort {


    public static <AnyType extends Comparable<? super AnyType>> void insertSort(AnyType[] a) {

        int j;
        for (int p = 1; p < a.length; p++) {

            AnyType tmp = a[p];

            for (j = p; j > 0 && tmp.compareTo(a[j - 1]) < 0; j--) {
                a[j] = a[j - 1];
            }

            a[j] = tmp;

        }

    }

}
