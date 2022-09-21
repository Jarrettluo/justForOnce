package com.jiaruiblog.justforonce.service.fileSplit;

import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/21 10:46
 * @Version 1.0
 */
public class Producer implements Runnable {

    private ArrayBlockingQueue<FileChunk> queue;

    private volatile boolean endFlag = false;

    public Producer(ArrayBlockingQueue<FileChunk> queue) {
        this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {

        int count = 0;
        Random random = new SecureRandom();
        while (!endFlag) {
            try {
                Thread.sleep(200);
                if(queue.size() == 10) {
                    System.out.println("================the queue is full,the producer thread is waiting..................");
                }
                FileChunk fileChunk = new FileChunk();
                fileChunk.setSn(count ++);
                long size = random.nextLong();
                fileChunk.setSize(size);
                queue.put(fileChunk);

                System.out.println("producer:" + Thread.currentThread().getName() + " produce:" + fileChunk+";the size of the queue:" + queue.size());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if ( count >= 50) {
                queue.put(FileSplitService.POISON_PILL);
                endFlag = true;
                break;
            }
        }
    }
}
