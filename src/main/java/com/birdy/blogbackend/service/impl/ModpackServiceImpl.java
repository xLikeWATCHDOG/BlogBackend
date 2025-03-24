package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.ModpackDao;
import com.birdy.blogbackend.domain.entity.Modpack;
import com.birdy.blogbackend.service.ModpackService;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
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

  @Override
  public long countToday() {
    // 记录今日的数量
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("to_char(create_time, 'YYYY-MM-DD')", "to_char(CURRENT_DATE, 'YYYY-MM-DD')");
    return modpackDao.getMapper().selectCountByQuery(queryWrapper);
  }

  @Override
  public long countPending() {
    // 记录待审核的数量
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("status", 0);
    return modpackDao.getMapper().selectCountByQuery(queryWrapper);
  }
}
