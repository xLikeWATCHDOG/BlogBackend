package com.birdy.blogbackend.controller;

import com.birdy.blogbackend.config.ConfigProperties;
import com.birdy.blogbackend.domain.dto.UserVO;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.vo.request.UserLoginRequest;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.domain.vo.response.TencentCaptchaResponse;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.UserService;
import com.birdy.blogbackend.util.captcha.TencentCaptchaUtil;
import com.birdy.blogbackend.util.gson.GsonProvider;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.birdy.blogbackend.constant.CommonConstant.CAPTCHA_HEADER;
import static com.birdy.blogbackend.constant.UserConstant.LOGIN_TOKEN;

/**
 * @author birdy
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    public static TencentCaptchaUtil TENCENT_CAPTCHA_UTIL = null;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigProperties configProperties;


    public void checkCaptcha(HttpServletRequest request) {
        if (!configProperties.captcha.isEnable()) {
            return;
        }
        if (TENCENT_CAPTCHA_UTIL == null) {
            // init
            TENCENT_CAPTCHA_UTIL = new TencentCaptchaUtil(configProperties.tencent.getSecretId(), configProperties.tencent.getSecretKey(), configProperties.captcha.getAppSecretKey());
        }
        // 获取Header里的captcha
        String captcha = request.getHeader(CAPTCHA_HEADER);
        // 将captcha转换为CaptchaResult
        TencentCaptchaResponse captchaResult = GsonProvider.normal().fromJson(captcha, TencentCaptchaResponse.class);
        if (captchaResult == null) {
            throw new BusinessException(ReturnCode.VALIDATION_FAILED, "请进行人机验证", request);
        }
        // 验证captcha
        try {
            TENCENT_CAPTCHA_UTIL.isCaptchaValid(captchaResult, Long.parseLong(configProperties.captcha.getCaptchaAppId()), request);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            throw new BusinessException(ReturnCode.SYSTEM_ERROR, e.getMessage(), request);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<UserVO>> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        checkCaptcha(request);
        User user = userService.userLogin(userLoginRequest, request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 获取Token
        String token = request.getSession().getAttribute(LOGIN_TOKEN).toString();
        userVO.setToken(token);
        return ResponseEntity.ok(BaseResponse.success(userVO));
    }
}
