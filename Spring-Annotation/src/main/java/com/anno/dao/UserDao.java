package com.anno.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @title: UserDao
 * @Author Wen
 * @Date: 2021/1/28 11:41
 * @Version 1.0
 */
@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void insert() {
        String sql = "INSERT INTO `tbl_user`(username,age) VALUES(?,?)";
        String username = UUID.randomUUID().toString().substring(0, 5);
        jdbcTemplate.update(sql, username, 19);
    }
}