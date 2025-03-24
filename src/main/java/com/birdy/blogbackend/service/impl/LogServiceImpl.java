package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.LogDao;
import com.birdy.blogbackend.domain.entity.Log;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.LogService;
import com.birdy.blogbackend.service.UserService;
import com.birdy.blogbackend.util.crypto.AESUtil;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author birdy
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService {
  @Autowired
  private LogDao logDao;
  @Autowired
  private UserService userService;

  @Override
  public @Nullable Log getLog(@Nullable String requestId, @Nullable HttpServletRequest request) {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("requestId", requestId);
    Log l = this.getOne(queryWrapper);
    if (l == null) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "requestId not found", request);
    }
    // 解密敏感数据
    String params = l.getParams();
    String result = l.getResult();
    try {
      params = AESUtil.decrypt(params);
      l.setParams(params);
      result = AESUtil.decrypt(result);
      l.setResult(result);
    } catch (Exception ignored) {
    }
    return l;
  }

  @Override
  public void addLog(@Nullable Log log, @Nullable HttpServletRequest request) {
    Log l = new Log();
    BeanUtils.copyProperties(log, l);
    if (l.getHttpCode() == null) {
      l.setHttpCode(200);
    }
    if (l.getCost() == null) {
      l.setCost(-1L);
    }
    if (l.getUid() == null) {
      User user = null;
      if (request != null) {
        user = userService.getLoginUserIgnoreError(request);
      }
      if (user != null) {
        l.setUid(user.getUid());
      }
    }
    // 加密敏感数据
    String params = l.getParams();
    String url = l.getUrl();
    String result = l.getResult();
    if (url.startsWith("/admin/log")) {
      result = null;
    }
    try {
      params = AESUtil.encrypt(params);
      l.setParams(params);
      if (result != null) {
        result = AESUtil.encrypt(result);
      }
      l.setResult(result);
    } catch (Exception ignored) {
    }
    this.save(l);
  }

  @Override
  public BaseMapper<Log> getMapper() {
    return logDao.getMapper();
  }
}
