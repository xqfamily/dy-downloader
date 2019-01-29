package com.xq.douyin.infrastructure.web;

import com.alibaba.fastjson.JSONObject;
import com.xq.douyin.application.DouYinService;
import com.xq.douyin.domain.DouYin;
import com.xq.douyin.domain.DouYinRepository;
import com.xq.douyin.domain.DouYinSpecification;
import com.xq.douyin.infrastructure.util.DouYinUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dy")
public class DouYinController {

    @Autowired
    private DouYinRepository douYinRepository;

    @Value("${dy.root-path}")
    private String ROOTPATH;

    @Autowired
    private DouYinUtil douYinUtil;
    @Autowired
    private DouYinService douYinService;

    /**
     * 获取fps和分辨率
     * @param url
     * @return
     */
    @GetMapping("/fps/res")
    public ResponseEntity getFpsAndRes(String url){
        Map<String,Object> result = new HashMap<>();
        List<JSONObject> fps = new ArrayList<>();
        douYinRepository.getFps().stream().forEach(f->{
            JSONObject obj = new JSONObject();
            obj.put("val",f);
            fps.add(obj);
        });

        List<JSONObject> resolution = new ArrayList<>();
        douYinRepository.getResolution().stream().forEach(f->{
            JSONObject obj = new JSONObject();
            obj.put("val",f);
            resolution.add(obj);
        });

        result.put("fps",fps);
        result.put("resolution",resolution);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/convert")
    public ResponseEntity convert(String url){
        String videoDownUrl = douYinUtil.getVideoDownUrl(douYinUtil.getVideoUrl(url));
        return ResponseEntity.ok(videoDownUrl);
    }

    @PostMapping
    public ResponseEntity save(String url){
        return ResponseEntity.ok(douYinService.save(url));
    }

    @GetMapping
    public ResponseEntity get(String resolution, String fps, String mergeUse,Pageable pageRequest){
        Specification<DouYin> douYinSpecification = DouYinSpecification.toSpecification(resolution, fps, mergeUse);
        return ResponseEntity.ok(douYinRepository.findAll(douYinSpecification,pageRequest));
    }

    @GetMapping("/images/{img}")
    public void getImg(@PathVariable("img")String img,HttpServletResponse response) throws IOException {
        File file = new File(ROOTPATH+img);
        InputStream in = new FileInputStream(file);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @GetMapping("/file/{fileName}")
    public void getFile(@PathVariable("fileName")String fileName,HttpServletResponse response) throws IOException {
        File file = new File(ROOTPATH+fileName);
        InputStream in = new FileInputStream(file);
        IOUtils.copy(in, response.getOutputStream());
        response.flushBuffer();
    }


}
