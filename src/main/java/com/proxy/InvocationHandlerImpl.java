package com.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvocationHandlerImpl implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method paramMethod, Object[] args) throws Throwable {
        System.out.println("You are going to invoke " + paramMethod.getName() + " ...");
        //paramMethod.invoke(car, null);
        System.out.println(paramMethod.getName() + " invocation Has Been finished...");
        return null;
    }
}
