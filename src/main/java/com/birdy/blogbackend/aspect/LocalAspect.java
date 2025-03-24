package com.birdy.blogbackend.aspect;

import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.birdy.blogbackend.annotation.Local;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author birdy
 */
@Aspect
@Component
public class LocalAspect {
  @Around("@annotation(local)")
  public Object doInterceptor(ProceedingJoinPoint joinPoint, Local local) throws Throwable {
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

    String ip = JakartaServletUtil.getClientIP(request);
    boolean isInternal = NetUtil.isInnerIP(ip);

    if (!isInternal) {
      throw new BusinessException(ReturnCode.FORBIDDEN_ERROR,
        "非法请求",
        request
      );
    }

    return joinPoint.proceed();
  }
}
