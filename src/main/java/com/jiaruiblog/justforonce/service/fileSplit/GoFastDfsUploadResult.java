package com.jiaruiblog.justforonce.service.fileSplit;

import lombok.Data;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/23 14:26
 * @Version 1.0
 */
@Data
public class GoFastDfsUploadResult {

    private String url;
    private String md5;
    private String path;
    private String domain;
    private String scene;
    private String scenes;
    private String retmsg;
    private int retcode;
    private String src;

}
