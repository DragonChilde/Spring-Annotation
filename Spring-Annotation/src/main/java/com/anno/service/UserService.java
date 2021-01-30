package com.anno.service;

import com.anno.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @title: UserService
 * @Author Wen
 * @Date: 2021/1/28 11:44
 * @Version 1.0
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    @Transactional()
    public void insertUser() {
        userDao.insert();
        System.out.println("插入完成...");

        int i = 10 / 0;
    }
}