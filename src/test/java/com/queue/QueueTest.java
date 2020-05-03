package com.queue;

import com.map.Pow2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by jiangfei on 2020/5/2.
 */
@Slf4j
public class QueueTest {

    @Test
    public void test0() {
        log.info("{}", Pow2.roundToPowerOfTwo(7));
    }

    @Test
    public void test1() {

        MpscUnboundedArrayQueue<Integer> queue = new MpscUnboundedArrayQueue<>(8);

        for (int i = 0; i < 20; i++) {
            queue.offer(i);
        }

        log.info("{}", queue);

    }

}
