package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.Log;
import com.birdy.blogbackend.mapper.LogMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class LogDao extends ServiceImpl<LogMapper, Log> implements IService<Log> {

}
