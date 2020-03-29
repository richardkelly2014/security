package com.demo;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by jiangfei on 2020/2/9.
 */
@Slf4j
public class URLTest {

    @Test
    public void test1() {
        TakingTaskModel model = TakingTaskModel.builder().build();
        StringBuilder url = new StringBuilder("111")
                .append("/taking/v1/tripOrderDispatch?")
                .append("tripId=").append(model.getTripId())
                .append("&orderNo=").append(model.getOrderNo());

        log.info("{}", url.toString());
    }

    @Test
    public void test2() {
        String aa = null;
        String b = "aaa" + aa;
        log.info("{}", b);
    }

    @Test
    public void test3() {
        ConcurrentMap<String, TakingTaskModel> map = new ConcurrentHashMap<>();
        map.put("1", TakingTaskModel.builder().build());
        map.put("2", TakingTaskModel.builder().build());

        log.info("{}", Lists.newArrayList(map.values()).size());
    }

    @Data
    @Builder
    private static class TakingTaskModel {
        private Integer tripId;
        private String orderNo;
    }
}
