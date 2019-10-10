package com.resource;

public interface ResourceLeak {

    void record();

    void record(Object hint);

    boolean close();

}
