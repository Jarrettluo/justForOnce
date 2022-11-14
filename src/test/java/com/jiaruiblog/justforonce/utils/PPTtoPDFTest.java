package com.jiaruiblog.justforonce.utils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/11/14 14:43
 * @Version 1.0
 */
public class PPTtoPDFTest {

    @Test
    public void convertPPTToPDF() throws IOException {

        String path = "D:\\_my_del\\ppt\\";

        File file2 = new File(path + "ResultG/");
        if (!file2.exists())
            file2.mkdirs();

        File file = new File(path);
        for (File f : file.listFiles()) {
            if (!f.isFile())
                continue;
            file2 = new File(path + "ResultG/" + file.getName() + ".pdf");
            if (!file2.exists())
                file2.createNewFile();
            new PPTtoPDF().convertPPTToPDF(f, file2);
        }

    }

    // 参考教程：https://blog.csdn.net/qq_26599807/article/details/107657890
    // HSLFSlideShow 用于ppt文件，XMLSlideShow 用于pptx文件。
    @Test
    public void convertPPTToPDFTest1() throws IOException {

        // String path = "E:\\优盘\\电子科技大学\\WOS_电子科技大学_20130426.pptx";
        String path = "E:\\优盘\\电子科技大学\\Web of Science数据库在科研中的价值与应用20120423.ppt";
        // String path = "D:\\_my_del\\ppt\\";
        File f = new File(path);
        String pdfPath = "D:\\abc.pdf";
        File file2 = new File(pdfPath);
        new PPTtoPDF().convertPPTToPDF(f, file2);

    }

}