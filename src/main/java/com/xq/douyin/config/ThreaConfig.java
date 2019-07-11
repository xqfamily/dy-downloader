package com.xq.douyin.config;

import org.springframework.context.annotation.Configuration;

/**
 * 参数配置工具类
 * 
 * @author 田小强
 *
 */
@Configuration
public class ThreaConfig {

    /**
     * 获取可用代理ip多线程开启数量
     * 
     * @return
     */
    public int agencyIpThreadPoolNum = 30;

    /**
     * 获取id转视频下载地址多线程开启数量
     * 
     * @return
     */
    public int idUpVedioUrlThreadPoolNum =30;

    /**
     * 获取视频下载多线程多线程开启数量
     * 
     * @return
     */
    public int downVedioThreadPoolNum = 3;


}
