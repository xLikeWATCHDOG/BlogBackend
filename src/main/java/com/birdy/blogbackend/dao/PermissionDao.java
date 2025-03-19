package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.Permission;
import com.birdy.blogbackend.mapper.PermissionMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class PermissionDao extends ServiceImpl<PermissionMapper, Permission> implements IService<Permission> {

}