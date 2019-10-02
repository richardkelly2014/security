package com.disruptor;

import com.demo.JwtTokenUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    private JwtTokenUtil jwtTokenUtil;

    @Before
    public void before() {

        jwtTokenUtil = new JwtTokenUtil();
    }

    @Test
    public void test1() {

        Map<String, Object> map = new HashMap<>();
        map.put("hello", "word");
        map.put("wwr", 100);

        String token = jwtTokenUtil.doGenerateToken(map, "123456");

        jwtTokenUtil.getUsernameFromToken(token);

    }


}
