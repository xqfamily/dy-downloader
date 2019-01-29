package com.xq.douyin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Data
public class DouYin extends AbstractPersistable<Long> {

    private String shareUrl;//分享的链接
    private String videoUrl;//视频地址
    private String videoName;//视频名称
    private String resolution;//分辨率
    private Long videoTime;//视频时长
    private String videoImg;//视频封面
    private String fps;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;//创建时间

    private String mergeUse = "未合并";
    private String mergeName;

    public DouYin() {
    }

    public DouYin(String shareUrl, String videoUrl, String videoName, String resolution, Long videoTime,String videoImg,String fps) {
        this.shareUrl = shareUrl;
        this.videoUrl = videoUrl;
        this.videoName = videoName;
        this.resolution = resolution;
        this.videoTime = videoTime;
        this.createTime = new Date();
        this.videoImg = videoImg;
        this.fps = fps;
    }
}
