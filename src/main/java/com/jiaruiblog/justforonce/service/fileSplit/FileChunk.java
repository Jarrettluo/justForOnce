package com.jiaruiblog.justforonce.service.fileSplit;

import lombok.Data;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/21 10:47
 * @Version 1.0
 */
@Data
public class FileChunk {

    private int sn;

    private String name;

    private String path;

    private String url;

    private String md5;

    private long size;

}
