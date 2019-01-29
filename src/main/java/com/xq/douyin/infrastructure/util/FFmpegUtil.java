package com.xq.douyin.infrastructure.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import net.sf.json.JSONObject;

//import com.boyuyun.basesystem.constant.Constant;

/**
 * 对视频、音频文件进行处理的工具类
 *
 * @author lx
 * @since 2016年7月25日 13:41:14
 * @version 1.0
 * @description 主要是处理视频、音频的处理；
 *  1.
 *
 */
public class FFmpegUtil {

    private static final Logger log = LoggerFactory.getLogger(FFmpegUtil.class);

    /**
     * 获取视频某个时间点的图片
     * timeStr 为秒 第几秒
     */
    public static void catchImage(String filePath){
        String tempDir = filePath.substring(0,filePath.lastIndexOf("."));
        String output = tempDir + "_catch.jpg";
        try {
            List<String> command = new ArrayList<String>();
//            command.add("cmd.exe");
//            command.add("/c");
            command.add("ffmpeg");
            command.add("-ss");
            command.add("00:00:01");
            command.add("-i");
            command.add(filePath);
            command.add("-f");
            command.add("image2");
            command.add("-y");
            command.add(output);
            //开始执行，并不等待返回
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null){
                sb.append(line);
                continue;
            }
            int exitVal = p.waitFor();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void convertToflvHD(String filePath,String dest){
        try {
            List<String> command = new ArrayList<String>();
//            command.add("cmd.exe");
//            command.add("/C");
            command.add("ffmpeg");
            command.add("-i");              //输入文件路径
            command.add(filePath);
            command.add("-y");              //是否覆盖
            command.add("-ab");             //音频数据流量，32 64 96 128
            command.add("32");
            command.add("-ar");             //声音采样频率 22050
            command.add("22050");
            command.add("-acodec");         //音频编码AAC
            command.add("aac");
            command.add("-s");              //指定分辨率，标清，高清；
//          command.add("640x480");         //标清：480p
            command.add("1280x720");        //高清：720p
            command.add("-qscale");         //以数值质量为基础的VBR，范围：0.01-255，越小质量越好
            command.add("10");
            command.add("-r");              //帧速率，数值： 15 29.97
            command.add("15");
            command.add(dest);              //目标存储路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static JSONObject getVideoInfo(String filePath){
        JSONObject result = new JSONObject();
        try {
            List<String> command = new ArrayList<String>();
            command.add("ffmpeg");
            command.add("-i");              //输入文件路径
            command.add(filePath);
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null){
                sb.append(line);
                continue;
            }
            result.put("resolution",getVideoResolution(sb));
            result.put("time",getVideoTime(sb));
            result.put("fps",getFps(sb));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得视频原有的分辨率
     * @return
     */
    public static String getVideoResolution(StringBuffer sb ){
        Pattern pat = Pattern.compile("([0-9]{2,4}x[0-9]{2,4})");
        Matcher matcher = pat.matcher(sb.toString());
        boolean find = matcher.find();
        if(find){
            String res = matcher.group(1);
            return res;
        }
        return "";
    }

    /**
     * 获取fps
     * @param sb
     * @return
     */
    public static String getFps(StringBuffer sb ){
        Pattern pat = Pattern.compile("([0-9]{2} fps|[0-9]{2}.[0-9]{2} fps)");
        Matcher matcher = pat.matcher(sb.toString());
        boolean find = matcher.find();
        if(find){
            String res = matcher.group(1);
            return res;
        }
        return "";
    }

    /**
     * 返回视频的长度：毫秒
     * @return
     */
    public static long getVideoTime(StringBuffer sb){
        Pattern pat = Pattern.compile("Duration: ([0-9:.]*),");
        Matcher matcher = pat.matcher(sb.toString());
        boolean find = matcher.find();
        if(find){
            String res = matcher.group(1);
            //处理
            String[] arr = res.split("[.]");
            String t = "";
            String l = "0";
            if(arr.length > 0){
                t = arr[0];
                if(arr.length > 1)l = arr[1];
            }
            try {
                long tempL = Long.parseLong(l);
                tempL = tempL * (l.length() ==1 ? 100 : (l.length() == 2 ? 10 : 1));
                String[] tempT = t.split(":");
                long tempH = Long.parseLong(tempT[0]);
                long tempM = Long.parseLong(tempT[1]);
                long tempS = Long.parseLong(tempT[2]);
                tempH = tempH * 60 * 60 * 1000;
                tempM = tempM * 60 * 1000;
                tempS = tempS * 1000;

                return (tempH + tempM + tempS + tempL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0L;
    }
    /**
     * 转化视频文件为Flv格式文件；
     * @param filePath
     * @return
     */
    public static JSONObject convertToFlv(String filePath){
        JSONObject result = new JSONObject();
        String tempFolder = filePath.substring(0,filePath.lastIndexOf("/"));
        String fileName = filePath.substring(filePath.lastIndexOf("/"),filePath.lastIndexOf("."));
        File folder = new File(tempFolder);
        if(!folder.isDirectory()){
            folder.mkdirs();
        }
        try {
            List<String> command = new ArrayList<String>();
            String dest = tempFolder+fileName+".flv";
            String destHD = tempFolder+fileName+"-HD.flv";
            System.out.println(filePath+"\r\n"+dest);
//            command.add("cmd.exe");
//            command.add("/C");
            command.add("ffmpeg");
            command.add("-i");              //输入文件路径
            command.add(filePath);
            command.add("-y");              //是否覆盖
//          command.add("-ab");             //音频数据流量，32 64 96 128
//          command.add("32K");
//          command.add("-ar");             //声音采样频率 22050
//          command.add("22050");
//          command.add("-acodec");         //音频编码AAC
//          command.add("aac");
            command.add("-s");              //指定分辨率，标清，高清；
            command.add("640x480");         //标清：480p
//          command.add("1280x720");        //高清：720p
//          command.add("-qscale");         //以数值质量为基础的VBR，范围：0.01-255，越小质量越好
//          command.add("10");
            command.add("-r");              //帧速率，数值： 15 29.97
            command.add("15");
            command.add(dest);              //目标存储路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null){
                sb.append(line);
//                System.out.println(line);
                continue;
            }
            int exitVal = p.waitFor();
            p.destroy();
            //获得时长/速率/其他meta信息
            convertToflvHD(filePath,destHD);
            long time = getVideoTime(sb);
            String solution = getVideoResolution(sb);
            System.out.println(time+"   ::  "+solution);
            //截取第一秒的图片做封面
//            catchImage(dest, fileName, solution);
            result.put("destPath",dest);    //普情FLV存放地址
            result.put("duration",time);    //时长
            result.put("destPathHD",destHD);//高清FLV存放地址
            result.put("solution",solution);//分辨率
            result.put("imagePath",tempFolder+fileName+"-1-"+solution+".jpg");
            //转化完成后进行增加索引
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    //根据当前视频的分辨率获得高清分辨率信息
    public static String getHDSolution(String solution){
        String rs = "1280x720";
        try {
            if(solution.indexOf("x") > -1){
                String[] ars = solution.split("x");
                String width = ars[1];
                int iwidth = Integer.valueOf(width);
                if(iwidth < 720){
                    rs = solution;
                }
            }
        } catch (Exception e) {}
        return rs;
    }


    /**
     * 合并多个Avi文件
     * @param txtPath 待合并的文件每行一个保存的txt文件里
     * @param outPath 合并后的avi保存路径
     */
    public static void mergeAvis(String txtPath,String outPath){
        System.out.println("merge:"+txtPath+"   "+outPath);
        List<String> commend = new ArrayList<>();
        commend.add("ffmpeg");
        commend.add("-f");//合成音视频
        commend.add("concat");
        commend.add("-safe");
        commend.add("0");
        commend.add("-i");
        commend.add(txtPath);
        commend.add("-c");
        commend.add("copy");
        commend.add(outPath);

        try {
            ProcessBuilder builder = new ProcessBuilder(commend);
            builder.command(commend);
            Process p = builder.start();
            doWaitFor(p);
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int doWaitFor(Process p){
        InputStream in = null;
        InputStream err = null;
        int exitValue = -1; // returned to caller when p is finished
        try {
            System.out.println("comeing");
            in = p.getInputStream();
            err = p.getErrorStream();
            boolean finished = false; // Set to true when p is finished

            while (!finished) {
                try {
                    while (in.available() > 0) {
                        // Print the output of our system call
                        BufferedInputStream bi = new BufferedInputStream(in);
                        Character c = new Character((char) bi.read());
                        System.out.print(c);
                    }
                    while (err.available() > 0) {
                        // Print the output of our system call
                        BufferedInputStream bi = new BufferedInputStream(err);
                        Character c = new Character((char) bi.read());
                        System.out.print(c);
                    }

                    exitValue = p.exitValue();
                    finished = true;

                } catch (IllegalThreadStateException e) {
                    // Process is not finished yet;
                    // Sleep a little to save on CPU cycles
                    Thread.currentThread().sleep(500);
                }
            }
        } catch (Exception e) {
            // unexpected exception! print it out for debugging...
            System.err.println("doWaitFor();: unexpected exception - "
                    + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            if (err != null) {
                try {
                    err.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        // return completion status to caller
        return exitValue;
    }


    /**
     * 新版-视频转化逻辑流程
     * 1. 至转化高清
     * @param filePath
     * @return
     */
/*    public static JSONObject callFFmpegNew(String filePath){
        log.info("开始转化视频");
        Date d1 = new Date();
        long d1l = d1.getTime();
        filePath = filePath.replaceAll("\\\\", "/");
        JSONObject jo = new JSONObject();
        String folder = filePath.substring(0,filePath.lastIndexOf("/"));//文件的相对文件夹路径,例如：/attachment/video/
        String fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."));//文件的文件名,例如：sss.mp4
        String hdDestFolder = folder+"/"+fileName+"_HD";//高清存放路径
        String hdDestM3u8Path = hdDestFolder+"/"+fileName+".m3u8";
        String hdResPath = folder+"/"+fileName+"_HD"+"/"+fileName+".m3u8";

        File destFolderFile = new File(hdDestFolder);
        if(!destFolderFile.isDirectory()){
            destFolderFile.mkdirs();
        }

        BufferedReader br = null;
        String solution = "1280x720";
        Long duration = 0L;
        try {
            String[] command = new String[18];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath+"/ffmpeg";
            command[3] = "-i";              //输入文件路径
            command[4] = filePath;
            command[5] = "-c:v";            //
            command[6] = "libx264";
            command[7] = "-c:a";
            command[8] = "aac";
            command[9] = "-strict";
            command[10] = "-2";
            command[11] = "-hls_time";
            command[12] = Constant.VIDEO_HLS_TIME+"";
            command[13] = "-hls_list_size";
            command[14] = "0";
            command[15] = "-f";
            command[16] = "hls";
            command[17] = hdDestM3u8Path;
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null){
                sb.append(line);
                continue;
            }
            //获得视频的分辨率，默认分辨率1280x720
            solution = getVideoResolution(sb);
            duration = getVideoTime(sb);
            int exitVal = p.waitFor();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        }
        //通过输出流获得需要的信息
        //截取第一秒的图片做封面
        String name = catchImage(filePath, fileName, solution);
        String postPath = folder+"/"+fileName+"_postpath.jpg";
        if(!new File(postPath).exists()){
            postPath = folder + "/" +name;
        }
        jo.put("solution",solution);
        jo.put("postpath",postPath);
        jo.put("success",true);
        jo.put("duration",duration);
        jo.put("hdPath",hdResPath);
        jo.put("sdPath","");

        Date d2 = new Date();
        long d2l = d2.getTime();
        long diff = d2l - d1l;
        log.debug("\r\n 视频转化转化结束，转化结果为：\r\n 文件路径:"+filePath+" \r\n 耗时: "+(diff/1000)+"秒 \r\n 返回信息:"+jo.toString());
        return jo;
    }*/

    /**
     * 对文件进行分片,默认处理方法：在目标文件目录下建立一个同名文件夹，然后将对应的分片数据放入文件夹内
     * @param filePath
     * @return
     */
    /*public static JSONObject callFFmpeg(String filePath){
        *//***
         * 目标：两种分辨率格式，最终都为m3u8,时长固定,切换源，名字一样？
         * 1.将源文件转化为两个格式的flv文件，分别存储，
         * ID_HD-->id.flv-->id.m3u8
         * ID_SD-->id.flv-->id.m3u8
         * 2.将两个分辨率的flv转化为m3u8格式
         * 3.将两个m3u8格式的地址回传回去
         *//*
        log.info("开始转化视频");
        filePath = filePath.replaceAll("\\\\", "/");
        JSONObject jo = new JSONObject();
//      String basePath = "";//FileUtil.getConfigRealPath();//tomcat 路径,例如：i:/tomcat1/resource/
        String folder = filePath.substring(0,filePath.lastIndexOf("/"));//文件的相对文件夹路径,例如：/attachment/video/
        String fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."));//文件的文件名,例如：sss.mp4
//      String sourcePath = basePath+filePath;//文件的绝对路径，源文件
        String hdDestFolder = folder+"/"+fileName+"_HD";//高清存放路径
        String hdDestFlvPath = hdDestFolder+"/"+fileName+".flv";
        String hdDestM3u8Path = hdDestFolder+"/"+fileName+".m3u8";
        String hdResPath = folder+"/"+fileName+"_HD"+"/"+fileName+".m3u8";
        String sdDestFolder = folder+"/"+fileName+"_SD";//普清存放路径
        String sdDestFlvPath = sdDestFolder+"/"+fileName+".flv";
        String sdDestM3u8Path = sdDestFolder+"/"+fileName+".m3u8";
        String sdResPath = folder+"/"+fileName+"_SD"+"/"+fileName+".m3u8";



        File destFolderFile = new File(hdDestFolder);
        if(!destFolderFile.isDirectory()){
            destFolderFile.mkdirs();
        }
        File destFolderFile2 = new File(sdDestFolder);
        if(!destFolderFile2.isDirectory()){
            destFolderFile2.mkdirs();
        }
        //1. 转化源文件为普清FLV格式

        BufferedReader br = null;
        String solution = "";
        Long duration = 0L;
        try {
            String[] command = new String[13];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath+"/ffmpeg";
            command[3] = "-i";              //输入文件路径
            command[4] = filePath;
            command[5] = "-y";              //是否覆盖
//          command[6] = "-ab";             //音频数据流量，32 64 96 128
//          command[7] = "32K";
            command[6] = "-ar";             //声音采样频率 22050
            command[7] = "22050";
//          command[10] = "-acodec";            //音频编码AAC
//          command[11] = "aac";
            command[8] = "-s";              //指定分辨率，标清，高清；
            command[9] = "640x480";         //标清：480p
//          command[14] = "1280x720";       //高清：720p
//          command[14] = "-qscale";            //以数值质量为基础的VBR，范围：0.01-255，越小质量越好
//          command[15] = "10";
            command[10] = "-r";             //帧速率，数值： 15 29.97
            command[11] = "15";
            command[12] = sdDestFlvPath;//目标存储路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null){
                sb.append(line);
                continue;
            }
            System.out.println(sb.toString());
            int exitVal = p.waitFor();
            solution = getVideoResolution(sb);
            duration = getVideoTime(sb);
            br.close();
            br = null;
            isr.close();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2. 转化源文件为高清FLV格式
        //根据solution获得HD Solution,edit by lixun ,鉴于视频转化低分辨率后失真严重，高清不在转化其他分辨率。
//      String hdSolution = getHDSolution(solution);
        String hdSolution = solution;
        try {
            String[] command = new String[13];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath+"/ffmpeg";
            command[3] = "-i";              //输入文件路径
            command[4] = filePath;
            command[5] = "-y";              //是否覆盖
//          command[6] = "-ab";             //音频数据流量，32 64 96 128
//          command[7] = "32K";
            command[6] = "-ar";             //声音采样频率 22050
            command[7] = "22050";
//          command[10] = "-acodec";            //音频编码AAC
//          command[11] = "aac";
            command[8] = "-s";              //指定分辨率，标清，高清；
//          command[13] = "640x480";            //标清：480p
//          command[7] = "1280x720";        //高清：720p
            command[9] = hdSolution;
//          command[15] = "-qscale";            //以数值质量为基础的VBR，范围：0.01-255，越小质量越好
//          command[16] = "10";
            command[10] = "-r";             //帧速率，数值： 15 29.97
            command[11] = "15";
            command[12] = hdDestFlvPath;                //目标存储路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null){
                sb.append(line);
                continue;
            }
            System.out.println(sb.toString());
            int exitVal = p.waitFor();
            br.close();
            br = null;
            isr.close();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3. 转化普清FLV为m3u8格式
        try {
            String[] command = new String[18];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath+"/ffmpeg";
            command[3] = "-i";              //输入文件路径
            command[4] = sdDestFlvPath;
            command[5] = "-c:v";            //
            command[6] = "libx264";
            command[7] = "-c:a";
            command[8] = "aac";
            command[9] = "-strict";
            command[10] = "-2";
            command[11] = "-hls_time";
            command[12] = Constant.VIDEO_HLS_TIME+"";
            command[13] = "-hls_list_size";
            command[14] = "0";
            command[15] = "-f";
            command[16] = "hls";
            command[17] = sdDestM3u8Path;

            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null){
                sb.append(line);
                continue;
            }
            System.out.println(sb.toString());
            int exitVal = p.waitFor();
            p.destroy();
            isr.close();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //4. 转化高清FLV为m3u8格式
        try {
            String[] command = new String[18];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath+"/ffmpeg";
            command[3] = "-i";              //输入文件路径
            command[4] = hdDestFlvPath;
            command[5] = "-c:v";            //
            command[6] = "libx264";
            command[7] = "-c:a";
            command[8] = "aac";
            command[9] = "-strict";
            command[10] = "-2";
            command[11] = "-hls_time";
            command[12] = Constant.VIDEO_HLS_TIME+"";
            command[13] = "-hls_list_size";
            command[14] = "0";
            command[15] = "-f";
            command[16] = "hls";
            command[17] = hdDestM3u8Path;
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null){
                sb.append(line);
                continue;
            }
            System.out.println(sb.toString());
            int exitVal = p.waitFor();
            p.destroy();
            isr.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            jo.put("success",false);
        }

        //通过输出流获得需要的信息
        //截取第一秒的图片做封面
        String name = catchImage(filePath, fileName, solution);
        String postPath = folder+"/"+fileName+"_postpath.jpg";
        if(!new File(postPath).exists()){
            postPath = folder + "/" +name;
        }
        jo.put("solution",solution);
        jo.put("postpath",postPath);
        jo.put("success",true);
        jo.put("duration",duration);
//        jo.element("destpath",resPath);
        jo.put("hdPath",hdResPath);
        jo.put("sdPath",sdResPath);
        System.out.println("视频转化转化结束，转化结果为：\r\n"+jo.toString());
        return jo;
    }*/

   /* public static void main(String[] args) {
//        rootPath="I:/workspace/ffmpeg/src/main/resources/ffmpeg";
//        String file = "d:/abc/avi/4.mp4";
//        callFFmpegNew(file);
//      convertToFlv(file);
//        System.out.println(rootPath);
        try {
//        File initialFile = new File("/home/icodebug/视频/抖音3.mp4");

            System.out.println(getVideoInfo("/home/icodebug/视频/抖音3.mp4").toJSONString());

        *//*InputStream stderr = new FileInputStream(initialFile);

//        InputStream stderr = p.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null){
            sb.append(line);
//            System.out.println(line);
            continue;
        }

            long time = getVideoTime(sb);
            String solution = getVideoResolution(sb);
            System.out.println(time+"  :::  "+solution);*//*

        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/
}

