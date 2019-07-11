package com.xq.douyin.infrastructure.thread;

import com.xq.douyin.infrastructure.util.DouYinUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

/**
 * 下载视频多线程类
 * 
 * @author 田小强
 *
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class DownVedioThread extends Thread {

    private String url;
    private String name;

    private DownVedioThread() {
    }

    public DownVedioThread(String u,String name) {
        this.url=u;
        this.name=name;
    }

    @Override
    public void run() {
        try {

            File file=new File(name);
            if(file.exists()){
                System.out.println("---------------------------->文件已存在："+"      "+name);
                return;
            }
            System.out.println(Thread.currentThread().getName()+"++++++++++++++++++>下载："+url+"      "+name);
            // 构造URL
            URL u = new URL(url);
            // 打开连接
            HttpURLConnection con = (HttpURLConnection )u.openConnection();
            con.setConnectTimeout(100000);
            con.setReadTimeout(100000);
            con.connect();
//            int contentLength = con.getContentLength();
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            OutputStream os = new FileOutputStream(name);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            os.close();
            is.close();
            System.out.println("++++++++++++++++++>完成："+name);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("线程" + Thread.currentThread().getName() + "====================异常：" + e.getMessage());
        }

    }

}
