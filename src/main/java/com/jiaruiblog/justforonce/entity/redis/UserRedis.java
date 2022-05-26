package com.jiaruiblog.justforonce.entity.redis;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Jarrett Luo
 * @Date 2022/5/26 18:35
 * @Version 1.0
 */

@Data
public class UserRedis implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String sex;
    private String age;
    private String city;

    //get、set自行添加

    @Override
    public String toString() {
        return "{" + "name='" + name + '\'' + ", sex='" + sex + '\'' + ", age='" + age + '\'' + ", city='" + city + '\'' + '}';
    }
}