package com.anno.dao;

import org.springframework.stereotype.Repository;

@Repository
public class BookDao {

    private Integer label = 1;

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "BookDao{" +
                "label=" + label +
                '}';
    }
}
