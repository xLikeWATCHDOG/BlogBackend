package com.birdy.blogbackend.controller;

import com.birdy.blogbackend.config.ConfigProperties;
import com.birdy.blogbackend.domain.ResultUtil;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.OAuthPlatform;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author birdy
 */
@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {
  @Autowired
  private ConfigProperties configProperties;
  @Value("${website}")
  private String websiteUrl;
  @Autowired
  private UserService userService;


  @GetMapping("/github")
  public ResponseEntity<BaseResponse<String>> github(HttpServletRequest request, HttpServletResponse response) {
    AuthGithubRequest authGithubRequest = getAuthGithubRequest();
    String url = authGithubRequest.authorize(AuthStateUtils.createState());
    // 直接重定向到url
    try {
      if (configProperties.getGithub().isEnable()) {
        log.info(url);
        response.sendRedirect(url);
      } else {
        response.sendRedirect(websiteUrl);
      }
    } catch (IOException e) {
      throw new BusinessException(ReturnCode.OPERATION_ERROR, "Failed to redirect to Github", request);
    }
    return ResultUtil.ok(url);
  }

  @RequestMapping("/github/callback")
  public ResponseEntity<BaseResponse<String>> githubCallback(AuthCallback callback, HttpServletRequest request, HttpServletResponse response) {
    try {
      AuthGithubRequest authGithubRequest = getAuthGithubRequest();
      AuthResponse<AuthUser> authResponse = authGithubRequest.login(callback);
      AuthUser authUser = authResponse.getData();
      User user = userService.oAuthLogin(authUser, OAuthPlatform.GITHUB, request);
    } catch (Throwable ignored) {
    }
    // 重定向到前端页面
    try {
      response.sendRedirect(websiteUrl);
    } catch (IOException e) {
      throw new BusinessException(ReturnCode.OPERATION_ERROR, "Failed to redirect to website", request);
    }
    return ResultUtil.ok("Request is being processed");
  }

  public AuthGithubRequest getAuthGithubRequest() {
    return new AuthGithubRequest(AuthConfig.builder()
      .clientId(configProperties.getGithub().getClientId())
      .clientSecret(configProperties.getGithub().getClientSecret())
      .redirectUri(configProperties.getGithub().getRedirectUri())
      .build());
  }

  @RequestMapping("/list")
  public ResponseEntity<BaseResponse<List<String>>> list(HttpServletRequest request) {
    // 判断是否启用
    List<String> str = new ArrayList<>();
    if (configProperties.getGithub().isEnable()) {
      str.add("github");
    }
    if (configProperties.getQq().isEnable()) {
      str.add("qq");
    }
    return ResultUtil.ok(str);
  }
}
