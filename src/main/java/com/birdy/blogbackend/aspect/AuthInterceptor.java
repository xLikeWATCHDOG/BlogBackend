package com.birdy.blogbackend.aspect;

import com.birdy.blogbackend.annotation.AuthCheck;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.PermissionService;
import com.birdy.blogbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

import static com.birdy.blogbackend.constant.UserConstant.LOGIN_TOKEN;

/**
 * 权限校验 AOP
 *
 * @author birdy
 */
@Aspect
@Component
public class AuthInterceptor {
  @Resource
  private UserService userService;
  @Resource
  private PermissionService permissionService;

  /**
   * 执行拦截
   */
  @Around("@annotation(authCheck)")
  public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
    List<String> any = Arrays.stream(authCheck.any()).filter(StringUtils::isNotBlank).toList();
    List<String> must = Arrays.stream(authCheck.must()).filter(StringUtils::isNotBlank).toList();

    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

    // 当前登录用户
    // 从请求头获取token
    String token = request.getHeader(LOGIN_TOKEN);
    if (token == null) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "Token不存在", request);
    }
    // 通过token获取用户
    User user = userService.getUserByToken(token, request);
    long uid = user.getUid();

    // 拥有任意权限即通过
    if (CollectionUtils.isNotEmpty(any)) {
      if (any.stream().noneMatch(per -> permissionService.checkPermission(uid, per))) {
        throw new BusinessException(ReturnCode.FORBIDDEN_ERROR, "无权限", request);
      }
    }

    // 必须有所有权限才通过
    if (CollectionUtils.isNotEmpty(must)) {
      if (must.stream().anyMatch(per -> !permissionService.checkPermission(uid, per))) {
        throw new BusinessException(ReturnCode.FORBIDDEN_ERROR, "无权限", request);
      }
    }

    // 通过权限校验，放行
    return joinPoint.proceed();
  }
}
