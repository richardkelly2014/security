package com.netty;

import com.utils.SystemPropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class BaseTest {

    @Test
    public void test1() {
        boolean flag = SystemPropertyUtil.getBoolean("java.util.secureRandomSeed", false);
        log.info("{}", flag);
    }
}
