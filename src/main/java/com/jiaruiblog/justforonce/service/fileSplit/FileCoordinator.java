package com.jiaruiblog.justforonce.service.fileSplit;

import ch.qos.logback.core.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/23 10:04
 * @Version 1.0
 */
public class FileCoordinator {

    private int productCount = 3;

    private int consumerCount = 5;

    /**
     * 分片的大小，可以自由定制, 这里是bit； 100M
     */
    public static final long CHUNK_SIZE = 1024*1024L*10;

    /**
     * 分片是否进行压缩
     */
    private static final boolean IS_COMPRESS = false;

    public static final String META_FOLDER = "originData";

    public static final String CHUNK_FOLDER = "chunkData";

    /**
     * 原始文件的路径
     */
    private String filePath;

    /**
     * 大文件的分片个数
     */
    private int chunkNum = 0;

    /**
     * 存放碎片文件的
     */
    public static final String FILE_DIR = "D:" + File.separator + UUID.randomUUID().toString();

    public FileCoordinator(String filePath) throws FileNotFoundException {

        if (new File(filePath).exists()) {
            this.filePath = filePath;
        } else {
            throw new FileNotFoundException();
        }
    }

    public void start() throws IOException {
        MetaFile metaFile = getMetaData();
        if ( metaFile == null || metaFile == new MetaFile()) {
            throw new RuntimeException(" file is error !");
        }
        // 创建文件路径
        Path directoryPath = Paths.get(FILE_DIR);
        Files.createDirectory(directoryPath);

        ArrayBlockingQueue<FileChunk> queue = new ArrayBlockingQueue<FileChunk>(10);
        // 采用newCached 线程池
        // 读写数据库、请求网络接口采用缓存线程池
        ExecutorService executor = Executors.newCachedThreadPool();
        // 生产者加入消费者中
        Producer producer = new Producer(queue, metaFile);
        executor.submit(producer);
        // 2个消费者加入到生产者中
        NewConsumer task = new NewConsumer(queue);
        Future<List<FileChunk>> result = executor.submit(task);
        Future<List<FileChunk>> result2 = executor.submit(task);
        executor.shutdown();


        try {
            // 取得消费者的结果
            System.out.println("==" + result.get());
            System.out.println("**" + result2.get());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // 结束以后删除文件夹, 遍历文件夹进行删除
        Files.delete(Paths.get(FILE_DIR));

    }

    /**
     * 获取文件的元数据对象
     *
     * @return 返回元数据对象
     */
    private MetaFile getMetaData() {
        MetaFile metaFile = new MetaFile();
        metaFile.setName(this.filePath);

        try {
            // long fileSize = FileUtil.getFileLength(new File(this.filePath));
            long fileSize = new File(this.filePath).length();
            chunkNum =(int) Math.ceil(fileSize*1.0 / CHUNK_SIZE) ;
            metaFile.setSize(fileSize);
            metaFile.setChunkNum(chunkNum);
        } catch (Exception e) {
            return metaFile;
        }
        List<FileChunk> fileChunkList = new ArrayList<>();
        metaFile.setFileChunks(fileChunkList);
        return metaFile;
    }

    /**
     * 检查结果是否符合要求
     * @return
     */
    private boolean checkResult() {

        return false;
    }

    private void rollBack() {

    }

}
