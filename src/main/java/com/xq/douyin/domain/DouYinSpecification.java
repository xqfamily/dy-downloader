package com.xq.douyin.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class DouYinSpecification {

    public static Specification<DouYin> toSpecification(String resolution, String fps, String mergeUse){

        return (root,cq,cb)->{
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(resolution)){
                predicates.add(cb.equal(root.get("resolution"),resolution));
            }
            if (StringUtils.isNotBlank(fps)){
                predicates.add(cb.equal(root.get("fps"),fps));
            }
            if (StringUtils.isNotBlank(mergeUse)){
                predicates.add(cb.equal(root.get("mergeUse"),mergeUse));
            }
            return cq.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        };

    }

}
