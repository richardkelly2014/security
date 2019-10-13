package com.map;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static com.map.UnsafeAccess.UNSAFE;

/**
 * Created by jiangfei on 2019/10/13.
 */
@Slf4j
public class ConcurrentAutoTable implements Serializable {

    public void add(long x) {
        add_if(x);
    }

    public void decrement() {
        add_if(-1L);
    }

    public void increment() {
        add_if(1L);
    }

    public void set(long x) {
        CAT newcat = new CAT(null, 4, x);
        // Spin until CAS works
        while (!CAS_cat(_cat, newcat)) {/*empty*/}
    }

    public long get() {
        return _cat.sum();
    }

    private long add_if(long x) {
        return _cat.add_if(x, hash(), this);
    }


    private volatile CAT _cat = new CAT(null, 16/*Start Small, Think Big!*/, 0L);

    private static AtomicReferenceFieldUpdater<ConcurrentAutoTable, CAT> _catUpdater =
            AtomicReferenceFieldUpdater.newUpdater(ConcurrentAutoTable.class, CAT.class, "_cat");

    private boolean CAS_cat(CAT oldcat, CAT newcat) {

        return _catUpdater.compareAndSet(this, oldcat, newcat);
    }

    // Hash spreader
    private static int hash() {
        //int h = (int)Thread.currentThread().getId();
        int h = System.identityHashCode(Thread.currentThread());
        int value = h << 3;                // Pad out cache lines.  The goal is to avoid cache-line contention
        return value;
    }

    private static class CAT implements Serializable {
        //array star
        private static final int _Lbase = UNSAFE.arrayBaseOffset(long[].class);
        private static final int _Lscale = UNSAFE.arrayIndexScale(long[].class);

        private static long rawIndex(long[] ary, int i) {
            assert i >= 0 && i < ary.length;
            return _Lbase + i * _Lscale;
        }

        private static boolean CAS(long[] A, int idx, long old, long nnn) {
            return UNSAFE.compareAndSwapLong(A, rawIndex(A, idx), old, nnn);
        }

        private final CAT _next;
        private volatile long _fuzzy_sum_cache;
        private volatile long _fuzzy_time;
        private static final int MAX_SPIN = 1;
        private final long[] _t;     // Power-of-2 array of longs

        CAT(CAT next, int sz, long init) {
            _next = next;
            _t = new long[sz];
            _t[0] = init;
        }

        public long add_if(long x, int hash, ConcurrentAutoTable master) {
            final long[] t = _t;
            final int idx = hash & (t.length - 1);
            log.info("idx : {} & {} = {}", hash, (t.length - 1), idx);
            // Peel loop; try once fast
            long old = t[idx];
            final boolean ok = CAS(t, idx, old, old + x);
            if (ok) return old;      // Got it
            // Try harder
            int cnt = 0;
            while (true) {
                old = t[idx];
                if (CAS(t, idx, old, old + x)) break; // Got it!
                cnt++;
            }
            if (cnt < MAX_SPIN) return old; // Allowable spin loop count
            if (t.length >= 1024 * 1024) return old; // too big already

            // Too much contention; double array size in an effort to reduce contention
            //long r = _resizers;
            //final int newbytes = (t.length<<1)<<3/*word to bytes*/;
            //while( !_resizerUpdater.compareAndSet(this,r,r+newbytes) )
            //  r = _resizers;
            //r += newbytes;
            if (master._cat != this) return old; // Already doubled, don't bother
            //if( (r>>17) != 0 ) {      // Already too much allocation attempts?
            //  // We could use a wait with timeout, so we'll wakeup as soon as the new
            //  // table is ready, or after the timeout in any case.  Annoyingly, this
            //  // breaks the non-blocking property - so for now we just briefly sleep.
            //  //synchronized( this ) { wait(8*megs); }         // Timeout - we always wakeup
            //  try { Thread.sleep(r>>17); } catch( InterruptedException e ) { }
            //  if( master._cat != this ) return old;
            //}

            CAT newcat = new CAT(this, t.length * 2, 0);
            // Take 1 stab at updating the CAT with the new larger size.  If this
            // fails, we assume some other thread already expanded the CAT - so we
            // do not need to retry until it succeeds.
            while (master._cat == this && !master.CAS_cat(this, newcat)) {/*empty*/}
            return old;
        }

        public long sum() {
            long sum = _next == null ? 0 : _next.sum(); // Recursively get cached sum
            final long[] t = _t;
            for (long cnt : t) {
                sum += cnt;
            }
            return sum;
        }

    }

}
