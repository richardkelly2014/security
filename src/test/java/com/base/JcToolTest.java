package com.base;

import com.map.Pow2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static com.map.UnsafeAccess.UNSAFE;

/**
 * Created by jiangfei on 2019/10/12.
 */
@Slf4j
public class JcToolTest {

    @Test
    public void test1() {

        int CPUs = Runtime.getRuntime().availableProcessors();

        System.out.println(CPUs);

        int value = Integer.numberOfLeadingZeros(CPUs);

        log.info("{},{}", CPUs, value);
    }

    @Test
    public void test2() {

        int value = Pow2.roundToPowerOfTwo(5);
        log.info("{}", value);
    }

    @Test
    public void test3() {
        int base = UNSAFE.arrayBaseOffset(long[].class);
        int scale = UNSAFE.arrayIndexScale(long[].class);

        log.info("{},{}", base, scale);
    }

}
