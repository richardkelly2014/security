package com.resource;

public interface ResourceLeakTracker<T> {

    void record();

    void record(Object hint);

    boolean close(T trackedObject);
}
