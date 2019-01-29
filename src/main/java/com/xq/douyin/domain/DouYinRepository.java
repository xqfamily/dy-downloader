package com.xq.douyin.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DouYinRepository extends JpaRepository<DouYin,Long>,JpaSpecificationExecutor<DouYin> {
    DouYin findByVideoName(String videoName);


    @Query("select dy.fps from DouYin dy group by dy.fps")
    List<String> getFps();

    @Query("select dy.resolution from DouYin dy group by dy.resolution")
    List<String> getResolution();

}
