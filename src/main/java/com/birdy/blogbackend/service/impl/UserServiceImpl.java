package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.UserDao;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.service.UserService;
import com.mybatisflex.core.BaseMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;


    @Override
    public BaseMapper<User> getMapper() {
        return userDao.getMapper();
    }

    @Override
    public @Nullable User getLoginUserIgnoreError(@NotNull HttpServletRequest request) {
        return null;
    }
}
