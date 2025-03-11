package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.mapper.UserMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class UserDao extends ServiceImpl<UserMapper, User> implements IService<User> {
}
