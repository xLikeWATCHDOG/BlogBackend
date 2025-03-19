package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.ModpackDao;
import com.birdy.blogbackend.domain.entity.Modpack;
import com.birdy.blogbackend.service.ModpackService;
import com.mybatisflex.core.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author birdy
 */
@Service
@Slf4j
public class ModpackServiceImpl implements ModpackService {
    @Autowired
    private ModpackDao modpackDao;

    @Override
    public BaseMapper<Modpack> getMapper() {
        return modpackDao.getMapper();
    }
}
