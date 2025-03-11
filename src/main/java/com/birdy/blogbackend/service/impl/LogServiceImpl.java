package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.LogDao;
import com.birdy.blogbackend.domain.entity.Log;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.service.LogService;
import com.mybatisflex.core.BaseMapper;
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

    @Override
    public @Nullable Log getLog(@Nullable String requestId, @Nullable HttpServletRequest request) {
        return null;
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
            User user = userService.getLoginUserIgnoreError(request);
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
        LogAddEvent event = new LogAddEvent(this, l, request);
        eventPublisher.publishEvent(event);
        if (event.isCancelled()) {
            throw new BusinessException(ReturnCode.CANCELLED, "日志记录被取消", request);
        }
        this.save(l);
    }

    @Override
    public BaseMapper<Log> getMapper() {
        return logDao.getMapper();
    }
}
