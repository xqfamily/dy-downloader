package com.xq.douyin.domain;

import lombok.Data;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;

@Data
@Entity
public class DyMerge extends AbstractPersistable<Long> {

    private String name;
    private String dys;

    public DyMerge() {
    }

    public DyMerge(String name, String dys) {
        this.name = name;
        this.dys = dys;
    }
}
