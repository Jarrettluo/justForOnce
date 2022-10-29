package com.jiaruiblog.justforonce.service;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/29 10:10
 * @Version 1.0
 */
public class MyClassLoader1Test {

    @Test
    public void loadData() throws Exception {

        MyClassLoader1 classLoader = new MyClassLoader1();

        Class<?> clazz = classLoader.loadData("com.jiaruiblog.Apple");

        Object obj = clazz.getDeclaredConstructor().newInstance();

        Method setMethod = clazz.getDeclaredMethod("setName", String.class);
        setMethod.invoke(obj, "ljr");

        Method getMethod = clazz.getDeclaredMethod("getName");
        final Object invoke = getMethod.invoke(obj);
        System.out.println(invoke);
        Method method = clazz.getDeclaredMethod("toString");
        String result = (String) method.invoke(obj);
        System.out.println(result);

    }
}