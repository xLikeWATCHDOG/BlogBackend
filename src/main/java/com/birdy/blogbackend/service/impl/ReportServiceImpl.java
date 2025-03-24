package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.ReportDao;
import com.birdy.blogbackend.domain.entity.Report;
import com.birdy.blogbackend.service.ReportService;
import com.mybatisflex.core.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author birdy
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
  @Autowired
  private ReportDao reportDao;

  @Override
  public BaseMapper<Report> getMapper() {
    return reportDao.getMapper();
  }
}
