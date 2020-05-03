package com.queue;

import static com.map.UnsafeRefArrayAccess.REF_ARRAY_BASE;
import static com.map.UnsafeRefArrayAccess.REF_ELEMENT_SHIFT;

/**
 * Created by jiangfei on 2020/5/2.
 */
public final class LinkedArrayQueueUtil {

    public static int length(Object[] buf) {
        return buf.length;
    }

    /**
     * This method assumes index is actually (index << 1) because lower bit is
     * used for resize. This is compensated for by reducing the element shift.
     * The computation is constant folded, so there's no cost.
     */
    static long modifiedCalcCircularRefElementOffset(long index, long mask) {
        return REF_ARRAY_BASE + ((index & mask) << (REF_ELEMENT_SHIFT - 1));
    }

    static long nextArrayOffset(Object[] curr) {
        return REF_ARRAY_BASE + ((long) (length(curr) - 1) << REF_ELEMENT_SHIFT);
    }
}

