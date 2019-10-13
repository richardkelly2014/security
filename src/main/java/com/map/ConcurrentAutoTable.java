package com.map;

import java.io.Serializable;

import static com.map.UnsafeAccess.UNSAFE;

/**
 * Created by jiangfei on 2019/10/13.
 */
public class ConcurrentAutoTable implements Serializable {



    private static class CAT implements Serializable {
        //array star
        private static final int _Lbase  = UNSAFE.arrayBaseOffset(long[].class);
        private static final int _Lscale = UNSAFE.arrayIndexScale(long[].class);
    }

}
