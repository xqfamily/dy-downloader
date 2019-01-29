package com.xq.douyin.infrastructure.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Component
public class DouYinUtil {

    @Value("${dy.root-path}")
    private String ROOTPATH;

    private static final String BASE_URL = "https://aweme.snssdk.com/aweme/v1/play/?video_id=%s";
/*    public static void main(String[] args) {
//        String shareUrl = "http://v.douyin.com/Nw6mJV/";
//        String shareUrl = "http://v.douyin.com/NESph6/";
//        String douyin = douyin(shareUrl);
//        down(douyin);
//        System.out.println(douyin);

        mergeAvis("ffmpeg","/home/icodebug/视频/hb.txt","/home/icodebug/视频/hb.mp4");

    }*/

    /**
     * 获取视频的url
     * @param url
     * @return
     */
    public String getVideoUrl(String url){
        try {
            Document document = Jsoup.connect(url).get();
            String html = document.body().html();
            int len = html.indexOf("video_id");
            String end = html.substring(len);
            int indexOf = end.indexOf("\",");
            String videoId = end.substring(9, indexOf);
            videoId = videoId.replace("\\u0026", "&");
            return String.format(BASE_URL, videoId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取抖音视频下载地址
     * @param url
     * @return
     */
    public String getVideoDownUrl(String url){
        try {
            Document d = Jsoup.connect(url)
                    .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Upgrade-Insecure-Requests","1")
                    .header("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")
                    .ignoreContentType(true)
                    .get();
            return d.location();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 下载视频文件
     * @param u
     * @param name
     */
    public String down(String u,String name){
        try {
            // 构造URL
            URL url = new URL(u);
            // 打开连接
            HttpURLConnection con = (HttpURLConnection )url.openConnection();
            con.setConnectTimeout(100000);
            con.setReadTimeout(100000);
            con.connect();
            int contentLength = con.getContentLength();
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            MessageDigest md = MessageDigest.getInstance("MD5");
            OutputStream os = new FileOutputStream(ROOTPATH+name);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
                md.update(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
            BigInteger bigInt = new BigInteger(1, md.digest());
            System.out.println("文件md5值：" + bigInt.toString(16));
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
