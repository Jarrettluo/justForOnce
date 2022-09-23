package com.jiaruiblog.justforonce.service.fileSplit;

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


    @Override
    public List<FileChunk> call() throws Exception {
        List<FileChunk> fileChunks = new ArrayList<>(0);
        while (true){
            try {
                Thread.sleep(100);
                if(queue.size() == 0) {
                    System.out.println("=============the queue is empty,the consumer thread is waiting................");
                }
                FileChunk item = queue.take();

                //poison pill processing
                if (item == FileSplitService.POISON_PILL) {
                    //put back to kill others
                    queue.put(item);
                    System.out.println(" finished");
                    break;
                }

                System.out.println("consumer:" + Thread.currentThread().getName() + " consume:" + item+";the size of the queue:" + queue.size());
                fileChunks.add(item);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return fileChunks;
    }
}
