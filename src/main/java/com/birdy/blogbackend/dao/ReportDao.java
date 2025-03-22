package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.Report;
import com.birdy.blogbackend.mapper.ReportMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class ReportDao extends ServiceImpl<ReportMapper, Report> implements IService<Report> {

}
