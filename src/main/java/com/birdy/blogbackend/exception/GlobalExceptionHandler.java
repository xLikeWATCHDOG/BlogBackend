package com.birdy.blogbackend.exception;

import com.birdy.blogbackend.domain.ResultUtil;
import com.birdy.blogbackend.domain.entity.Log;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.service.LogService;
import com.birdy.blogbackend.util.gson.GsonProvider;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @Resource
  private LogService logService;

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<BaseResponse<Object>> businessExceptionHandler(BusinessException e) {
    Log l = new Log();
    l.setHttpCode(e.getStatus());
    String requestId = UUID.randomUUID().toString();
    l.setRequestId(requestId);
    l.setMethod(e.getMethod());
    l.setParams(e.getParams());
    l.setUrl(e.getUrl());
    l.setHeaders(GsonProvider.normal().toJson(e.getHeaders()));
    l.setIp(e.getIp());
    l.setResult(e.getMessage());
    logService.addLog(l, e.getRequest());
    log.error("businessException, id: {}, message: {}", requestId, e.getMessage(), e);
    ResponseEntity<BaseResponse<Object>> ret = ResultUtil.failed(e.getCode(), e.getData(), e.getMessage(), e.getStatus());
    BaseResponse<Object> body = ret.getBody();
    BaseResponse.RequestInfo requestInfo = new BaseResponse.RequestInfo();
    requestInfo.setRequestId(requestId);
    requestInfo.setCost(-1);
    assert body != null;
    body.setRequestInfo(requestInfo);
    return ret;
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<BaseResponse<Object>> runtimeExceptionHandler(RuntimeException e) {
    Log l = new Log();
    l.setHttpCode(500);
    String requestId = UUID.randomUUID().toString();
    l.setRequestId(requestId);
    l.setResult(e.getMessage());
    logService.addLog(l, null);
    log.error("runtimeException, id: {}, message: {}", requestId, e.getMessage(), e);
    ResponseEntity<BaseResponse<Object>> ret = ResultUtil.failed(ReturnCode.SYSTEM_ERROR, e.getMessage(), 500);
    BaseResponse<Object> body = ret.getBody();
    BaseResponse.RequestInfo requestInfo = new BaseResponse.RequestInfo();
    requestInfo.setRequestId(requestId);
    requestInfo.setCost(-1);
    assert body != null;
    body.setRequestInfo(requestInfo);
    return ret;
  }
}
