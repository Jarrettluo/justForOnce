package com.jiaruiblog.justforonce.service.fileSplit;

import lombok.Data;

import java.util.List;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/21 10:49
 * @Version 1.0
 */
@Data
public class MetaFile {

    private String name;

    private long size;

    private long chunkSize;

    private Integer chunkNum;

    private String compressType;

    private boolean isCompress;

    private List<FileChunk> fileChunks;

}
