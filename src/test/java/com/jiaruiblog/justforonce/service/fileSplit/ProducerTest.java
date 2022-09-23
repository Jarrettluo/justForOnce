package com.jiaruiblog.justforonce.service.fileSplit;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/21 10:54
 * @Version 1.0
 */
public class ProducerTest {

    /**
     * Consumer 采用Runnable 无法获取到返回值
     * 使用join 可以加入到主线程中，阻塞住主线程？
     */
    @Test
    public void producerTest1() {

        MetaFile metaFile = new MetaFile();
        metaFile.setName("testdataa");

        List<FileChunk> fileChunkList = Lists.newArrayList();
        metaFile.setFileChunks(fileChunkList);

        ArrayBlockingQueue<FileChunk> queue = new ArrayBlockingQueue<FileChunk>(10);
        Thread producer1 = new Thread(new Producer(queue, metaFile));
        producer1.start();

        Thread consumer1 = new Thread(new Consumer(queue));
        Thread consumer2 = new Thread(new Consumer(queue));
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

    /**
     * consumer 采用Callable 可以返回线程的值
     * Callable 必须搭配着线程池来实现
     */
    @Test
    public void producerTest2() {
        MetaFile metaFile = new MetaFile();
        metaFile.setName("testdataa");

        List<FileChunk> fileChunkList = Lists.newArrayList();
        metaFile.setFileChunks(fileChunkList);

        ArrayBlockingQueue<FileChunk> queue = new ArrayBlockingQueue<FileChunk>(10);

        // 采用newCached 线程池
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
    }


}

