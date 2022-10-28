package com.jiaruiblog.justforonce.service;

import lombok.val;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/28 18:39
 * @Version 1.0
 */
public class MyClassLoaderTest {

    @Test
    public void findClass() {

        val myClassLoader = new MyClassLoader();

        // Class<?> clazz = myClassLoader.findClass();

        // clazz.newInstance();

    }
}