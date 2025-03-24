package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.VisitorDao;
import com.birdy.blogbackend.domain.entity.Visitor;
import com.birdy.blogbackend.service.VisitorService;
import com.mybatisflex.core.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
public class VisitorServiceImpl implements VisitorService {
  @Autowired
  private VisitorDao visitorDao;

  @Override
  public void addOne() {
    // 设置日期，时分秒毫秒都设为0
    LocalDate localDate = LocalDate.now();
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    Visitor visitor = visitorDao.getById(date);

    if (visitor == null) {
      // 如果今天没有记录，创建新记录
      visitor = new Visitor();
      visitor.setDate(date);
      visitor.setCount(1L);
      visitorDao.save(visitor);
    } else {
      // 如果有记录，更新计数
      visitor.setCount(visitor.getCount() + 1);
      visitorDao.updateById(visitor);
    }
  }

  @Override
  public BaseMapper<Visitor> getMapper() {
    return visitorDao.getMapper();
  }

  @Override
  public long getToday() {
    LocalDate localDate = LocalDate.now();
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Visitor visitor = visitorDao.getById(date);
    return visitor.getCount();
  }
}
