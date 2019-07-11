package com.xq.douyin.application;

import com.alibaba.fastjson.JSONObject;
import com.xq.douyin.domain.DouYin;
import com.xq.douyin.domain.DouYinRepository;
import com.xq.douyin.infrastructure.util.DouYinUtil;
import com.xq.douyin.infrastructure.util.FFmpegUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DouYinService {

    @Autowired
    private DouYinRepository douYinRepository;
    @Value("${dy.root-path}")
    private String ROOTPATH;
    @Autowired
    private DouYinUtil douYinUtil;

    public DouYin save(String url){

        JSONObject video = douYinUtil.getVideoUrl(url);

        String videoName = video.getString("id")+".mp4";
        DouYin dy = douYinRepository.findByVideoName(videoName);
        if (dy!=null){
            return dy;
        }
        String videoDownUrl = douYinUtil.getVideoDownUrl(video.getString("playUrl"));
        douYinUtil.down(videoDownUrl,videoName);
        JSONObject videoInfo = FFmpegUtil.getVideoInfo(ROOTPATH + videoName);

        dy = new DouYin(url, video.getString("playUrl"), videoName, videoInfo.getString("resolution"), videoInfo.getLong("time"),video.getString("id")+"_catch.jpg",videoInfo.getString("fps"));
        douYinRepository.saveAndFlush(dy);

        FFmpegUtil.catchImage(ROOTPATH + videoName);
        return dy;
    }

}
