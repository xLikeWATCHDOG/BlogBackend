package com.birdy.blogbackend.controller;

import com.birdy.blogbackend.config.ConfigProperties;
import com.birdy.blogbackend.domain.dto.UserVO;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.vo.request.EmailSendRequest;
import com.birdy.blogbackend.domain.vo.request.PhoneLoginRequest;
import com.birdy.blogbackend.domain.vo.request.UserLoginRequest;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.domain.vo.response.TencentCaptchaResponse;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.MailService;
import com.birdy.blogbackend.service.UserService;
import com.birdy.blogbackend.util.CaffeineFactory;
import com.birdy.blogbackend.util.NumberUtil;
import com.birdy.blogbackend.util.gson.GsonProvider;
import com.birdy.blogbackend.util.tencent.TencentCaptchaUtil;
import com.github.benmanes.caffeine.cache.Cache;
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

import java.util.concurrent.TimeUnit;

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
    private static final Cache<String, Integer> MAIL_CODE_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
    @Autowired
    private ConfigProperties configProperties;
    private static final Cache<String, Integer> MAIL_FAIL_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).build();
    @Autowired
    private MailService mailService;

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

    @PostMapping("/phone")
    public ResponseEntity<BaseResponse<UserVO>> phoneLogin(@RequestBody PhoneLoginRequest phoneLoginRequest, HttpServletRequest request) {
        checkCaptcha(request);
        User user = userService.phoneLogin(phoneLoginRequest, request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 获取Token
        String token = request.getSession().getAttribute(LOGIN_TOKEN).toString();
        userVO.setToken(token);
        return ResponseEntity.ok(BaseResponse.success(userVO));
    }

    @PostMapping("/mail")
    public ResponseEntity<BaseResponse<Boolean>> sendEmail(@RequestBody EmailSendRequest emailSendRequest, HttpServletRequest request) {
        checkCaptcha(request);
        // 验证失败次数是否超过阈值
        String email = emailSendRequest.getEmail();
        Integer failCount = MAIL_FAIL_CACHE.getIfPresent(email);
        if (failCount != null && failCount >= 3) {
            throw new BusinessException(ReturnCode.VALIDATION_FAILED, "请勿频繁发送邮件", request);
        }

        try {
            // 生成6位随机验证码
            String code = NumberUtil.getRandomCode(6);
            // 发送邮件
            mailService.getEmailCode(email, code, request);

            // 成功发送后操作
            // 清除失败计数
            MAIL_FAIL_CACHE.invalidate(email);
            // 存储验证码
            MAIL_CODE_CACHE.put(email, Integer.parseInt(code));
            log.info("邮件发送成功，邮箱：{}", email);
        } catch (Exception e) {
            // 发送失败处理
            int attempts = failCount == null ? 1 : failCount + 1;
            // 更新失败次数
            MAIL_FAIL_CACHE.put(email, attempts);
            log.error("邮件发送失败，邮箱：{}，失败次数：{}", email, attempts);
            throw new BusinessException(ReturnCode.VALIDATION_FAILED, "邮件发送失败，请稍后重试", request);
        }
        return ResponseEntity.ok(BaseResponse.success(true));
    }
}
