package com.jiaruiblog.justforonce.utils;

import java.util.List;
import java.util.Set;

/**
 * @ClassName CSVToObjectTest
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/7/17 6:21 下午
 * @Version 1.0
 **/
public class CSVToObjectTest {

    public static void main(String[] args) {

        CSVToObject csvToObject = new CSVToObject().bind(
            TestCase.class, Set.class
        );

        try {
            List<TestCase> testCaseList = csvToObject.CSVExport("sfdfd");
            System.out.println(testCaseList);
        } catch (Exception e) {
            e.printStackTrace();
        }



        System.out.println(csvToObject);

    }
}
