package com.jiaruiblog.justforonce.utils.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/21 10:13
 * @Version 1.0
 */
public class FileUtil {

    /**
     * @Author：
     * @Description：获取某个目录下所有直接下级文件，不包括目录下的子目录的下的文件，所以不用递归获取
     * @Date：
     */
    public static List<String> getFiles(String path) {
        List<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                //files.add(tempList[i].toString());
                //文件名，不包含路径
                String fileName = tempList[i].getName();
                files.add(fileName);
            }
            if (tempList[i].isDirectory()) {
                //这里就不递归了，
            }
        }
        return files;
    }

    /**
     * 获取文件长度
     */
    public static long getFileLength(File file) {
        FileReader fr =  null;
        BufferedReader br = null;
        //文件大小
        long fileSize = 0;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = br.readLine();
            //按行读取文件
            while(line != null){
                //计算文件大小
                fileSize += line.length();
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //关闭输入流
            try {
                if(br != null){
                    br.close();
                }
                if(fr != null){
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //返回文件大小
        return fileSize;
    }

    /**
     * 获取文件名 （去除后缀）
     * @param filename
     * @return
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }



}
