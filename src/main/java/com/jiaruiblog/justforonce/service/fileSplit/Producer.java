package com.jiaruiblog.justforonce.service.fileSplit;

import io.netty.handler.stream.ChunkedFile;
import lombok.SneakyThrows;

import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
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

    private MetaFile metaFile;

    public Producer(ArrayBlockingQueue<FileChunk> queue, MetaFile metaFile) {
        this.queue = queue;
        this.metaFile = metaFile;
    }

    @SneakyThrows
    @Override
    public void run() {
        int count = 0;

        File splitFile = new File(metaFile.getName());
        String splitSmallFilesDir = FileCoordinator.FILE_DIR;
        long splitSmallFileSize = FileCoordinator.CHUNK_SIZE;

        //被分割文件总大小
        long splitFileSize=splitFile.length();
        //每个小文件分割的起始位置
        long splitSmallFileBeginPos=0;
        //实际分割的小文件大小
        long splitSmallFileActualSize=splitSmallFileSize>splitFileSize?splitFileSize:splitSmallFileSize;
        //被分割的小文件个数
        int size=(int)Math.ceil(splitFileSize*1.0/splitSmallFileSize);

        //被分割的小文件路径list
        List<String> splitSmallFileList=new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            splitSmallFileList.add(splitSmallFilesDir+ File.separator +i+"_"+splitFile.getName());
        }

        //切割文件
        for (int i = 0; i < size; i++) {
            if(queue.size() == 0) {
                System.out.println("=============the queue is empty,the producer thread is waiting................");
            }
            //切割起始位置
            splitSmallFileBeginPos=i*splitSmallFileSize;
            //切割到最后一个小文件
            if (i==size-1) {
                //切割的实际大小
                splitSmallFileActualSize=splitFileSize;
            }else {//否则
                //切割的实际大小
                splitSmallFileActualSize=splitSmallFileSize;
                //源文件减小
                splitFileSize-=splitSmallFileActualSize;
            }

            //具体的切割
            try {

                //源文件
                RandomAccessFile splitRandomAccessFile=new RandomAccessFile(splitFile, "r");
                //被分割的小文件
                RandomAccessFile splitSmallRandomAccessFile=new RandomAccessFile(splitSmallFileList.get(i), "rw");

                //从源文件的哪个位置读取
                splitRandomAccessFile.seek(splitSmallFileBeginPos);
                //分段读取
                //10字节的缓存, 默认的bufferInputStream 的缓存大小是8K
                // byte[] cache=new byte[1024*10];
                byte[] cache=new byte[8192];
                int len=-1;
                while((len=splitRandomAccessFile.read(cache))!=-1) {
                    //小文件实际分割大小>len
                    if (splitSmallFileActualSize>len) {
                        splitSmallRandomAccessFile.write(cache,0,len);
                        splitSmallFileActualSize-=len;
                    }else {//小文件实际分割大小<len，写完数据后跳出循环
                        splitSmallRandomAccessFile.write(cache,0,(int)splitSmallFileActualSize);
                        break;
                    }
                }

                splitRandomAccessFile.close();
                splitSmallRandomAccessFile.close();


                FileChunk fileChunk = new FileChunk();
                fileChunk.setSize(splitSmallFileActualSize);
                fileChunk.setPath(splitSmallFileList.get(i));
                fileChunk.setSn(++count);
                fileChunk.setName(i+"_"+splitFile.getName());
                fileChunk.setMd5("");
                fileChunk.setUrl("");
                queue.put(fileChunk);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("文件未找到",e);
            } catch (IOException e) {
                throw new RuntimeException("文件传输异常",e);
            }
        }
        queue.put(FileSplitService.POISON_PILL);
        endFlag = true;


    }


}
