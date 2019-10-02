package com.disruptor;

import com.hystrix.MyCommand;
import com.hystrix.MyService;
import com.netflix.hystrix.HystrixCommand;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class HystrixTest {
    @Test
    public void test() throws Exception {
        MyService service = new MyService();
        List<Future<String>> fl = new ArrayList<>(10);

        for (int i = 0; i < 15; i++) {
            Thread.sleep(1000);
            MyCommand command = new MyCommand("TestGroup", "fishing" + (i * i));
            command.setService(service);

            Observable<String> observable = command.toObservable();
            observable.subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    System.out.println("执行command发生错误！");
                    e.printStackTrace();
                }

                @Override
                public void onNext(String s) {
                    System.out.println(s);
                }
            });
        }

        Thread.sleep(15000);

    }

    @Test
    public void test1() {
//        HystrixCommand command = new HystrixCommand() {
//            @Override
//            protected Object run() throws Exception {
//
//                return null;
//            }
//        };
//
//        command.execute();
    }
}
