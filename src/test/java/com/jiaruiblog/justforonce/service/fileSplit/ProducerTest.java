package com.jiaruiblog.justforonce.service.fileSplit;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/21 10:54
 * @Version 1.0
 */
public class ProducerTest {

    @Test
    public void producerTest1() {

        boolean endFlag = false;
        MetaFile metaFile = new MetaFile();
        metaFile.setName("testdataa");

        List<FileChunk> fileChunkList = Lists.newArrayList();
        metaFile.setFileChunks(fileChunkList);

        ArrayBlockingQueue<FileChunk> queue = new ArrayBlockingQueue<FileChunk>(10);
        Thread producer1 = new Thread(new Producer(queue, endFlag));
        producer1.start();

        Thread consumer1 = new Thread(new Consumer(queue, endFlag));
        Thread consumer2 = new Thread(new Consumer(queue, endFlag));
        consumer1.start();
        consumer2.start();
        try {
            producer1.join();
            consumer1.join();
            consumer2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

