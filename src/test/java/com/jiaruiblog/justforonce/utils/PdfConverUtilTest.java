package com.jiaruiblog.justforonce.utils;

import org.junit.Test;

import com.jiaruiblog.justforonce.utils.PdfConverUtil;

import java.io.File;

/**
 * @Author Jarrett Luo
 * @Date 2022/11/14 11:51
 * @Version 1.0
 */
public class PdfConverUtilTest {

    @Test
    public void pptToPdf() {

        String pptPath = "E:\\优盘\\电子科技大学\\Web of Science数据库在科研中的价值与应用20120423.ppt";

        boolean successful = false;
        // ppt to pdf
        successful = PdfConverUtil.pptToPdf(pptPath, "D:\\");

        // pptx to pdf
        //	 successful = PdfConvertUtil.pptxToPdf("D:\\360_js\\测321pt.pptx", "D:\\360_js");

        System.out.println("转换" + (successful ? "成功" : "失败"));

    }

    @Test
    public void pptxToPdf() {

        String pptPath = "E:\\优盘\\电子科技大学\\WOS_电子科技大学_20130426.pptx";

        boolean successful = false;
        // ppt to pdf
        successful = PdfConverUtil.pptxToPdf(pptPath, "D:\\");

        // pptx to pdf
        //	 successful = PdfConvertUtil.pptxToPdf("D:\\360_js\\测321pt.pptx", "D:\\360_js");

        System.out.println("转换" + (successful ? "成功" : "失败"));
    }

    @Test
    public void makeTablePpt() {

        try {
            PdfConverUtil.makeTablePpt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createAPpt() {
        try {
            PdfConverUtil.createAPpt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pptToImageTest1() {
        // String path = "E:\\优盘\\电子科技大学\\WOS_电子科技大学_20130426.pptx";
        String path = "D:\\测试专用.pptx";
        String picPath = "D:\\";
        int times = 1;
        PdfConverUtil.pptToImage(new File(path), new File(picPath), times);
    }
}