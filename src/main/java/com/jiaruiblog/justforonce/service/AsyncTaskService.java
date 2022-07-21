package com.jiaruiblog.justforonce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.Future;

/**
 * @Author Jarrett Luo
 * @Date 2022/7/21 11:44
 * @Version 1.0
 */

@Slf4j
@Component
public class AsyncTaskService {

    public static Random random = new Random();

    @Async("taskExecutor")
    public Future<String> run() throws Exception {
        long sleep = random.nextInt(10000);
        sleep += 5000;
        log.info("开始任务，需耗时：" + sleep + "毫秒");
        Thread.sleep(sleep);
        log.info("完成任务");
        return new AsyncResult<>("test");
    }

}
