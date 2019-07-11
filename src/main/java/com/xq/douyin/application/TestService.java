package com.xq.douyin.application;

import com.xq.douyin.config.ThreaConfig;
import com.xq.douyin.infrastructure.thread.DownVedioThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TestService {

    @Autowired
    private ThreaConfig threaConfig;
    @Value("${dy.root-path}")
    private String ROOTPATH;

    public void DownVedio() {

        String url = "https://135zyv6.xw0371.com/2019/02/07/cTJrO8R0uALqvMCD/out";
        // 开启下载多线程
        ExecutorService es = Executors.newFixedThreadPool(threaConfig.downVedioThreadPoolNum);// 创建一个固定大小为30的线程池

        for (int i = 1; i < 1332; i++) {// 为所有待下载视频创建线程
            es.submit(new DownVedioThread(url+i+".ts",ROOTPATH+i));
        }
        es.shutdown();
        while (true) {// 查看视频线程是否下载完毕
            if (es.isTerminated()) {
                // 如果下载线程运行完毕结束下载程序运行
                System.out.println("=========================全部下载完成===============================");
                break;// 终止es循环查询
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}
