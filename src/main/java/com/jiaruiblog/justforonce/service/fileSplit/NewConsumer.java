package com.jiaruiblog.justforonce.service.fileSplit;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/23 9:50
 * @Version 1.0
 */
public class NewConsumer implements Callable<List<FileChunk>> {


    private ArrayBlockingQueue<FileChunk> queue;


    public NewConsumer(ArrayBlockingQueue<FileChunk> queue) {
        this.queue = queue;
    }

    private String peerUrl = "http://81.69.247.172:8080/group1/";


    @Override
    public List<FileChunk> call() throws Exception {
        List<FileChunk> fileChunks = new ArrayList<>(0);
        while (true){
            try {
                FileChunk item = queue.take();

                if(queue.size() == 0) {
                    System.out.println("=============the queue is empty,the consumer thread is waiting................");
                }
                //poison pill processing
                if (item == FileSplitService.POISON_PILL) {
                    //put back to kill others
                    queue.put(item);
                    System.out.println(" finished");
                    break;
                }

                FileInputStream fileInputStream = new FileInputStream(item.getPath());
                String fileName = item.getName();
                GoFastDfsUploadResult result = FileUploadUtil.upload(fileInputStream,
                        fileName, peerUrl + DfsConstant.API_UPLOAD, peerUrl, FileCoordinator.CHUNK_FOLDER);

                if ( result != null) {
                    item.setUrl(result.getUrl());
                    item.setMd5(result.getMd5());
                    item.setPath(result.getPath());
                    fileChunks.add(item);
                }

                Files.delete(Paths.get(item.getPath()));


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return fileChunks;
    }
}
