package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.Modpack;
import com.birdy.blogbackend.mapper.ModpackMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class ModpackDao extends ServiceImpl<ModpackMapper, Modpack> implements IService<Modpack> {

}