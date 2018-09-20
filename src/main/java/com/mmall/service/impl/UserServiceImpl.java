package com.mmall.service.impl;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public ServiceResponse<User> login(String username, String password) {
        return null;
    }
}
