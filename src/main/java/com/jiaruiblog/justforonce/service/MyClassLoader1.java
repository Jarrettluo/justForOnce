package com.jiaruiblog.justforonce.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/29 10:08
 * @Version 1.0
 */
public class MyClassLoader1 extends ClassLoader {

    private static final String MESSAGE_CLASS_PATH = "D:" + File.separator + "Apple.class";

    public Class<?> loadData(String className) throws Exception {
        byte [] data = loadClassData();
        if(data != null) {
            return super.defineClass(className, data, 0, data.length);
        }
        return null;
    }

    private byte [] loadClassData() throws Exception {
        InputStream inputStream = null;
        ByteArrayOutputStream bos = null;
        byte [] data = null;
        try {
            inputStream = new FileInputStream(new File(MESSAGE_CLASS_PATH));
            bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[8];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            if (bos != null) {
                data = bos.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
            if(bos != null) {
                bos.close();
            }
        }
        return data;
    }
}
