package com.jiaruiblog.justforonce.service;

import com.jiaruiblog.justforonce.JustForOnceApplication;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author Jarrett Luo
 * @Date 2022/7/21 11:46
 * @Version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JustForOnceApplication.class)
public class AsyncTaskServiceTest {

    @Autowired
    AsyncTaskService asyncTaskService;

    // 评价：设置一段代码执行的超时时间呢？如果处理超时就忽略该错误继续向下执行。
    // 该种写法将程序进行阻塞，导致数据不能及时响应给客户。
    @Test
    public void runTest1() throws Exception {
        System.out.println("开始阻塞住了");
        Future<String> futureResult = asyncTaskService.run();
        try {
            String result = futureResult.get(5, TimeUnit.SECONDS);
            log.info(result);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("阻塞结束！");
    }
}
