package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.OAuth;
import com.birdy.blogbackend.mapper.OAuthMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class OAuthDao extends ServiceImpl<OAuthMapper, OAuth> implements IService<OAuth> {

}
