package com.jiaruiblog.justforonce.service.fileSplit;

import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;


// 参考代码：https://codeantenna.com/a/638m3PZeGY
// 放入毒丸停止消费者
// 标准生产者、消费者模型：https://zhuanlan.zhihu.com/p/73442055
/**
 * @Author Jarrett Luo
 * @Date 2022/9/21 10:46
 * @Version 1.0
 */
public class Consumer implements Runnable{

    //special object to kill consumers
    public static final Object POISON_PILL = new Object();

    private int productCount = 3;

    private int consumerCount = 5;

    private ArrayBlockingQueue<FileChunk> queue;

    private volatile boolean endFlag = false;

    public Consumer(ArrayBlockingQueue<FileChunk> queue, boolean endFlag) {
        this.queue = queue;
        this.endFlag = endFlag;
    }

    @Override
    public void run() {

        while (true){
            try {
                Thread.sleep(100);
                if(queue.size() == 0) System.out.println("=============the queue is empty,the consumer thread is waiting................");
                FileChunk item = queue.take();
                System.out.println("consumer:" + Thread.currentThread().getName() + " consume:" + item+";the size of the queue:" + queue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if ( endFlag && queue.isEmpty()) {
                break;
            }
        }

    }
}
