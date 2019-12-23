package com.base;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@Slf4j
public class AtomicBooleanTest {

    @Test
    public void test1() {

        AtomicBoolean atomicBoolean = new AtomicBoolean();
        System.out.println(atomicBoolean.get());

        System.out.println(atomicBoolean.compareAndSet(false, true));
        System.out.println(atomicBoolean.get());

        System.out.println(atomicBoolean.getAndSet(true));
        System.out.println(atomicBoolean.get());
    }

    @Test
    public void test2() {
        A a1 = A.builder().value(1).build();
        AtomicReference<A> atomicReference = new AtomicReference<A>(a1);

        System.out.println(atomicReference.get());

        atomicReference.compareAndSet(A.builder().value(2).build(), A.builder().value(3).build());

        System.out.println(atomicReference.get());

    }

    @Test
    public void test3() {
        AtomicReferenceFieldUpdater<A, Integer> aIntegerAtomicReferenceFieldUpdater =
                AtomicReferenceFieldUpdater.newUpdater(A.class, Integer.class, "code");

        A a1 = A.builder().code(100).build();

        System.out.println(aIntegerAtomicReferenceFieldUpdater.compareAndSet(a1, 100, 200));

        System.out.println(a1);
    }

    @Test
    public void test4() {
        int[] SIZE_TABLE;

        List<Integer> sizeTable = new ArrayList<Integer>();
        for (int i = 16; i < 512; i += 16) {
            sizeTable.add(i);
        }

        for (int i = 512; i > 0; i <<= 1) {
            sizeTable.add(i);
        }

        SIZE_TABLE = new int[sizeTable.size()];
        for (int i = 0; i < SIZE_TABLE.length; i++) {
            SIZE_TABLE[i] = sizeTable.get(i);
        }

        log.info("{}", SIZE_TABLE);

    }

    @Data
    @Builder
    private static class A {
        private Integer value;
        private String message;

        public volatile Integer code;
    }
}
