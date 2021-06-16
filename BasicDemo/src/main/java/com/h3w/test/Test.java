package com.h3w.test;

import com.h3w.utils.HttpUtil;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {



    public static void main(String[] agrs) {
        long start = System.currentTimeMillis();

        for(int i=0;i<10;i++){

            ExecutorService cachedThreadPool = Executors.newFixedThreadPool(3);
            ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpUtil.httpGet("http://localhost:8082/user/testTh");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        System.out.println("用时:" + String.valueOf(System.currentTimeMillis()-start));
    }


}
