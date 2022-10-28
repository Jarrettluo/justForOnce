package com.jiaruiblog.justforonce.service.fileSplit;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @Author Jarrett Luo
 * @Date 2022/9/23 10:13
 * @Version 1.0
 */
public class FileCoordinatorTest {

    @Test
    public void coordinatorTest1() throws IOException {

        long startTime = System.currentTimeMillis();

        FileCoordinator fileCoordinator = new FileCoordinator("D:" + File.separator + "scenebuilder-kit-18.0.0.jar");
        fileCoordinator.start();

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

    }

    @Test
    public void coordinatorTest2() throws IOException {

        long startTime = System.currentTimeMillis();

        FileCoordinator fileCoordinator = new FileCoordinator("D:" + File.separator + "flutter_windows_v1.0.0-stable.zip");
        fileCoordinator.start();

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

    }

}
