package com.xq.douyin.infrastructure.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xq.douyin.domain.DouYin;
import com.xq.douyin.domain.DouYinRepository;
import com.xq.douyin.domain.DyMerge;
import com.xq.douyin.domain.DyMergeRepository;
import com.xq.douyin.infrastructure.util.DouYinUtil;
import com.xq.douyin.infrastructure.util.FFmpegUtil;
import com.xq.douyin.infrastructure.util.FileUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dymerge")
public class DyMergeController {

    @Autowired
    private DouYinRepository douYinRepository;

    @Value("${dy.root-path}")
    private String ROOTPATH;

    @Autowired
    private DouYinUtil douYinUtil;

    @Autowired
    private DyMergeRepository dyMergeRepository;


    @PostMapping()
    public ResponseEntity merge(@RequestBody List<DouYin> dys){
        System.out.println(JSON.toJSONString(dys));
        String name = System.currentTimeMillis()+"";
        String pathName = ROOTPATH + name;
        List<Long> ids = dys.stream().map(dy -> dy.getId()).collect(Collectors.toList());
        List<String> merges = dys.stream().map(dy -> ROOTPATH + dy.getVideoName()).collect(Collectors.toList());
        FileUtil.writeTxt(pathName,merges);
        FFmpegUtil.mergeAvis(pathName+".txt",pathName+".mp4");
        DyMerge dyMerge = dyMergeRepository.saveAndFlush(new DyMerge(name + ".mp4", ids.toString()));
        dys.stream().forEach(dy->{
            dy.setMergeUse("已合并");
            dy.setMergeName(name);
            douYinRepository.saveAndFlush(dy);
        });

        return ResponseEntity.ok(name + ".mp4");
    }

}
