package com.jiaruiblog.justforonce.utils;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author jiarui.luo
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Transfer {

    String rowName();

    boolean isNecessary() default true;

    int fieldLength() default 64;

    String fieldRegex() default ".*";

    boolean isRepeat() default false;

    // 是否忽略正则检查
    boolean ignore() default false;


}
