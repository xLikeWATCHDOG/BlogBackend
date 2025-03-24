package com.birdy.blogbackend.aspect;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.birdy.blogbackend.domain.entity.Log;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.service.LogService;
import com.birdy.blogbackend.service.VisitorService;
import com.birdy.blogbackend.util.gson.GsonProvider;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 日志拦截器
 *
 * @author birdy
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
  @Autowired
  private LogService logService;
  @Autowired
  private VisitorService visitorService;

  /**
   * 执行拦截
   */
  @Around("execution(* com.birdy.blogbackend.controller.*.*(..))")
  public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
    Gson gson = GsonProvider.normal();
    // 计时
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    // 获取请求路径
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
    // 获取请求方法
    String method = request.getMethod();
    // 获取ip
    String ip = JakartaServletUtil.getClientIP(request);
    // 生成请求唯一 id
    String requestId = IdUtil.randomUUID();
    String url = request.getRequestURI();
    // 获取请求参数
    var paramMap = JakartaServletUtil.getParamMap(request);
    String reqParam = gson.toJson(paramMap);
    // 开始记录请求日志
    log.info("发起请求：[ID：{},方法：{},路径：{},IP：{},参数：{}]", requestId, method, url, ip, reqParam);

    // 执行请求
    Object result = point.proceed();
    stopWatch.stop();

    // 记录请求结束日志
    long totalTimeMillis = stopWatch.getTotalTimeMillis();
    log.info("请求完成：[ID：{},持续时间：{} ms]", requestId, totalTimeMillis);

    if (result instanceof BaseResponse) {
      var baseResponse = (BaseResponse<Object>) result;
      BaseResponse.RequestInfo requestInfo = BaseResponse.RequestInfo.builder()
        .requestId(requestId)
        .cost(totalTimeMillis).build();
      baseResponse.setRequestInfo(requestInfo);

    }
    String resultStr = null;
    try {
      resultStr = gson.toJson(result);
    } catch (Throwable e) {
      log.error("序列化返回结果失败", e);
    }
    Log l = Log.builder()
      .requestId(requestId)
      .url(url)
      .method(method)
      .ip(ip)
      .params(reqParam)
      .result(resultStr)
      .cost(totalTimeMillis)
      .build();
    logService.save(l);
    visitorService.addOne();
    return result;
  }
}
