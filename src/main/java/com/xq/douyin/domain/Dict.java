package com.xq.douyin.domain;

import lombok.Data;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;

@Entity
@Data
public class Dict extends AbstractPersistable<Long> {

    private String name;
    private String val;

}
