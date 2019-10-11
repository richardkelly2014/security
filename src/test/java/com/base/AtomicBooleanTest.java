package com.base;

import lombok.Builder;
import lombok.Data;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

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

    @Data
    @Builder
    private static class A {
        private Integer value;
        private String message;

        public volatile Integer code;
    }
}
