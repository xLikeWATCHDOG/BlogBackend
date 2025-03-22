package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.Visitor;
import com.birdy.blogbackend.mapper.VisitorMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class VisitorDao extends ServiceImpl<VisitorMapper, Visitor> implements IService<Visitor> {

}