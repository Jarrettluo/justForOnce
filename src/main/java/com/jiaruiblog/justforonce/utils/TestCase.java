package com.jiaruiblog.justforonce.utils;

import lombok.Data;


/**
 * @ClassName TestCase
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/7/17 6:21 下午
 * @Version 1.0
 **/
@Data
public class TestCase {

    private Long id;

    @Transfer(rowName = "信号名称", fieldLength = 32, fieldRegex = ".*")
    private String name;

    @Transfer(rowName = "信号类型")
    private String signalType;

    @Transfer(rowName = "单位")
    private String unit;

    @Transfer(rowName = "一级属性")
    private String attribute1;

    @Transfer(rowName = "二级属性")
    private String attribute2;

    @Transfer(rowName = "映射集名称")
    private String mappingName;


}
