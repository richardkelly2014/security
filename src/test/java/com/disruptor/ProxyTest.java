package com.disruptor;

import com.proxy.*;
import net.sf.cglib.proxy.Enhancer;
import org.junit.Test;

import java.lang.reflect.Proxy;

public class ProxyTest {

    @Test
    public void test1() {

        //ElectricCar car = new ElectricCar();

        Vehicle vehicle = (Vehicle) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                new Class[]{Vehicle.class}, new InvocationHandlerImpl());

        vehicle.drive();

    }

    @Test
    public void test2() {

        //Programmer progammer = new Programmer();

        Hacker hacker = new Hacker();
        //cglib 中加强器，用来创建动态代理
        Enhancer enhancer = new Enhancer();

        //设置要创建动态代理的类
        enhancer.setSuperclass(Programmer.class);

        // 设置回调，这里相当于是对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实行intercept()方法进行拦截
        enhancer.setCallback(hacker);

        Programmer proxy = (Programmer) enhancer.create();

        proxy.code();

    }
}
