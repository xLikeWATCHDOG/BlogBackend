package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.Photo;
import com.birdy.blogbackend.mapper.PhotoMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class PhotoDao extends ServiceImpl<PhotoMapper, Photo> implements IService<Photo> {
}
