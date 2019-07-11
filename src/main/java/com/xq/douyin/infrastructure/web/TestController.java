package com.xq.douyin.infrastructure.web;

import com.xq.douyin.application.DouYinService;
import com.xq.douyin.application.TestService;
import com.xq.douyin.domain.DouYinRepository;
import com.xq.douyin.infrastructure.util.DouYinUtil;
import com.xq.douyin.infrastructure.util.FFmpegUtil;
import com.xq.douyin.infrastructure.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private DouYinRepository douYinRepository;

    @Value("${dy.root-path}")
    private String ROOTPATH;

    @Autowired
    private DouYinUtil douYinUtil;
    @Autowired
    private DouYinService douYinService;

    @Autowired
    private TestService testService;

//793   894  896
    @GetMapping
    public ResponseEntity get(){
//        List<String> list = new ArrayList<>();
//        for (int i=1000;i<1132;i++){
//            list.add(i+"");
//        }
//        String url = "https://135zyv6.xw0371.com/2019/02/07/cTJrO8R0uALqvMCD/out";
//        list.stream().forEach(a->{
//            douYinUtil.down(url+a+".ts",a+".NueXini");
//        });

        testService.DownVedio();

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/merge")
    public ResponseEntity merge(){
        List<String> merges = new ArrayList<>();
        for (int i=18;i<1332;i++){
            merges.add(ROOTPATH+i);
        }
//        FileUtil.writeTxt(ROOTPATH+"merge",merges);
        FFmpegUtil.mergeAvis(ROOTPATH+"merge",ROOTPATH+"疯狂外星人.mp4");
        return ResponseEntity.ok("ok");
    }


    @GetMapping("/check")
    public ResponseEntity check(){
        for (int i=1;i<1332;i++){
            File file=new File(ROOTPATH+i);
            if(!file.exists()){
                System.out.println("---------------------------->文件不存在："+"      "+i);
            }
        }
        return ResponseEntity.ok("ok");
    }

}
