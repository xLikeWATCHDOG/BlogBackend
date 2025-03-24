package com.birdy.blogbackend.controller;

import com.birdy.blogbackend.config.ConfigProperties;
import com.birdy.blogbackend.domain.ResultUtil;
import com.birdy.blogbackend.domain.dto.UserVO;
import com.birdy.blogbackend.domain.entity.Permission;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.enums.StatusCode;
import com.birdy.blogbackend.domain.vo.request.EmailSendRequest;
import com.birdy.blogbackend.domain.vo.request.forget.CheckForgetPasswordRequest;
import com.birdy.blogbackend.domain.vo.request.forget.UserForgetPasswordRequest;
import com.birdy.blogbackend.domain.vo.request.forget.UserForgetRequest;
import com.birdy.blogbackend.domain.vo.request.phone.PhoneCodeSendRequest;
import com.birdy.blogbackend.domain.vo.request.phone.PhoneLoginRequest;
import com.birdy.blogbackend.domain.vo.request.user.*;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.domain.vo.response.TencentCaptchaResponse;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.MailService;
import com.birdy.blogbackend.service.PermissionService;
import com.birdy.blogbackend.service.PhotoService;
import com.birdy.blogbackend.service.UserService;
import com.birdy.blogbackend.util.CaffeineFactory;
import com.birdy.blogbackend.util.NumberUtil;
import com.birdy.blogbackend.util.PasswordUtil;
import com.birdy.blogbackend.util.aliyun.AliyunSmsUtil;
import com.birdy.blogbackend.util.gson.GsonProvider;
import com.birdy.blogbackend.util.tencent.TencentCaptchaUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.birdy.blogbackend.constant.CommonConstant.CAPTCHA_HEADER;
import static com.birdy.blogbackend.constant.UserConstant.FORGET_TOKEN;
import static com.birdy.blogbackend.constant.UserConstant.LOGIN_TOKEN;

/**
 * @author birdy
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
  public static final Cache<String, User> FORGET_PASSWORD_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
  private static final Cache<String, String> MAIL_CODE_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
  private static final Cache<String, Integer> MAIL_FAIL_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).build();
  private static final Cache<String, Integer> PHONE_FAIL_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
  public static TencentCaptchaUtil TENCENT_CAPTCHA_UTIL = null;
  @Autowired
  private UserService userService;
  @Autowired
  private ConfigProperties configProperties;
  @Autowired
  private MailService mailService;
  @Autowired
  private PhotoService photoService;
  @Autowired
  private PermissionService permissionService;
  @Autowired
  private AliyunSmsUtil aliyunSmsUtil;

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
    //checkCaptcha(request);
    User user = userService.userLogin(userLoginRequest, request);
    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);
    // 获取Token
    String token = request.getSession().getAttribute(LOGIN_TOKEN).toString();
    userVO.setToken(token);
    return ResultUtil.ok(userVO);
  }

  @PostMapping("/phone/code")
  public ResponseEntity<BaseResponse<Boolean>> phoneCode(@RequestBody PhoneCodeSendRequest phoneCodeSendRequest, HttpServletRequest request) {
    checkCaptcha(request);
    // 生成6位随机验证码
    String code = NumberUtil.getRandomCode(6);
    // 存储验证码
    PHONE_FAIL_CACHE.put(phoneCodeSendRequest.getPhone(), 0);
    Map<String, String> params = Map.of("code", code);
    boolean success = aliyunSmsUtil.send(params, phoneCodeSendRequest.getPhone());
    if (!success) {
      log.info("手机验证码发送成功，手机号：{}", phoneCodeSendRequest.getPhone());
    } else {
      log.error("手机验证码发送失败，手机号：{}", phoneCodeSendRequest.getPhone());
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "手机验证码发送失败，请稍后重试", request);
    }

    return ResultUtil.ok(true);
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
    return ResultUtil.ok(userVO);
  }

  @PostMapping("/forget")
  public ResponseEntity<BaseResponse<Boolean>> forget(@RequestBody UserForgetRequest userForgetRequest, HttpServletRequest request) {
    checkCaptcha(request);
    String email = userForgetRequest.getEmail();
    User user = userService.getByEmail(email, request);
    String token = UUID.randomUUID().toString();
    mailService.forgetPassword(email, token, request);
    FORGET_PASSWORD_CACHE.put(token, user);

    return ResultUtil.ok(true);
  }

  @PostMapping("forget/password")
  public ResponseEntity<BaseResponse<Boolean>> forgetPassword(@RequestBody UserForgetPasswordRequest userForgetPasswordRequest, HttpServletRequest request) {
    checkCaptcha(request);
    String password = userForgetPasswordRequest.getPassword();
    String token = request.getHeader(FORGET_TOKEN);
    if (StringUtils.isAllBlank(token, password)) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "参数错误", request);
    }
    User user = FORGET_PASSWORD_CACHE.getIfPresent(token);
    if (user == null) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "token无效", token, request);
    }
    userService.updatePassword(user, password, request);
    FORGET_PASSWORD_CACHE.invalidate(token);
    return ResultUtil.ok(true);
  }

  @PostMapping("check/forget")
  public ResponseEntity<BaseResponse<String>> checkForgetPasswordToken(@RequestBody CheckForgetPasswordRequest checkForgetPasswordRequest, HttpServletRequest request) {
    String token = checkForgetPasswordRequest.getToken();
    User user = FORGET_PASSWORD_CACHE.getIfPresent(token);
    if (user == null) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "token无效", token, request);
    }
    return ResultUtil.ok(user.getEmail().toLowerCase());
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
      MAIL_CODE_CACHE.put(email, code);
      log.info("邮件发送成功，邮箱：{}", email);
    } catch (Exception e) {
      // 发送失败处理
      int attempts = failCount == null ? 1 : failCount + 1;
      // 更新失败次数
      MAIL_FAIL_CACHE.put(email, attempts);
      log.error("邮件发送失败，邮箱：{}，失败次数：{}", email, attempts);
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "邮件发送失败，请稍后重试", request);
    }
    return ResultUtil.ok(true);
  }

  @PostMapping("/register")
  public ResponseEntity<BaseResponse<UserVO>> register(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
    checkCaptcha(request);
    // 验证邮箱验证码
    String email = userRegisterRequest.getEmail();
    String code = MAIL_CODE_CACHE.getIfPresent(email);
    log.warn(userRegisterRequest.getCode());
    if (code == null || !code.equalsIgnoreCase(userRegisterRequest.getCode())) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "验证码错误", request);
    }
    User user = userService.register(userRegisterRequest, request);
    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);
    // 获取Token
    String token = request.getSession().getAttribute(LOGIN_TOKEN).toString();
    userVO.setToken(token);
    return ResultUtil.ok(userVO);
  }

  @PostMapping("/token")
  public ResponseEntity<BaseResponse<UserVO>> refreshToken(HttpServletRequest request) {
    // 从请求头获取token
    String token = request.getHeader(LOGIN_TOKEN);
    if (token == null) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "Token不存在", request);
    }
    // 通过token获取用户
    User user = userService.getUserByToken(token, request);
    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);
    long uid = user.getUid();
    Permission ret = permissionService.getMaxPriorityGroupP(uid);
    userVO.setGroup(ret);
    // 刷新token
    token = userService.refreshToken(user, request);
    userVO.setToken(token);
    return ResultUtil.ok(userVO);
  }

  @PostMapping("logout")
  public ResponseEntity<BaseResponse<Boolean>> logout(HttpServletRequest request) {
    userService.logout(request);
    return ResultUtil.ok(true);
  }

  /**
   * 获取头像
   */
  @GetMapping("/avatar/{uid}")
  public ResponseEntity<InputStreamResource> getAvatar(@PathVariable("uid") Long uid, HttpServletRequest request) {
    User user = userService.getById(uid);
    Path path;
    try {
      if (user == null) {
        path = photoService.getPhotoPathByMd5(String.valueOf(uid), request);
      } else {
        var avatar = user.getAvatar();
        if (avatar == null) {
          userService.generateDefaultAvatar(user, request);
        }
        path = photoService.getPhotoPathByMd5(avatar, request);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "头像文件不存在", uid, request);
    }
    File file = new File(path.toString());
    if (!file.exists()) {
      if (user == null) {
        userService.generateDefaultAvatar(uid, request);
      } else {
        userService.generateDefaultAvatar(user, request);
      }
      throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "头像文件不存在", uid, request);
    }
    try {
      InputStream is = new FileInputStream(file);
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file.toPath()));
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + file.getName());
      InputStreamResource inputStreamResource = new InputStreamResource(is);
      return new ResponseEntity<>(inputStreamResource, headers, StatusCode.OK);
    } catch (Throwable e) {
      userService.generateDefaultAvatar(user, request);
      throw new BusinessException(ReturnCode.SYSTEM_ERROR, "预览系统异常", request);
    }
  }

  @PostMapping("/avatar")
  public ResponseEntity<BaseResponse<Boolean>> uploadAvatar(@RequestBody MultipartFile file, HttpServletRequest request) {
    // 从请求头获取token
    String token = request.getHeader(LOGIN_TOKEN);
    if (token == null) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "Token不存在", request);
    }
    // 通过token获取用户
    User user = userService.getUserByToken(token, request);
    userService.setupAvatar(user, file, request);
    return ResultUtil.ok(true);
  }

  @GetMapping("/profile/{uid}")
  public ResponseEntity<BaseResponse<UserVO>> getProfile(@PathVariable("uid") Long uid, HttpServletRequest request) {
    User user = userService.getById(uid);
    if (user == null) {
      throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "用户不存在", uid, request);
    }
    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);
    return ResultUtil.ok(userVO);
  }

  @PostMapping("/update")
  public ResponseEntity<BaseResponse<Boolean>> update(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
    // 从请求头获取token
    String token = request.getHeader(LOGIN_TOKEN);
    if (token == null) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "Token不存在", request);
    }
    // 通过token获取用户
    User loginUser = userService.getUserByToken(token, request);
    loginUser.setUsername(userUpdateRequest.getUsername());
    loginUser.setGender(userUpdateRequest.getGender());
    userService.updateById(loginUser);
    return ResultUtil.ok(true);
  }

  @PostMapping("/password")
  public ResponseEntity<BaseResponse<Boolean>> updatePassword(@RequestBody UserPasswordRequest userPasswordRequest, HttpServletRequest request) {
    // 从请求头获取token
    String token = request.getHeader(LOGIN_TOKEN);
    if (token == null) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "Token不存在", request);
    }
    // 通过token获取用户
    User loginUser = userService.getUserByToken(token, request);
    String password = userPasswordRequest.getOldPassword();
    if (!PasswordUtil.checkPassword(password, loginUser.getPassword())) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "原密码错误", request);
    }
    userService.updatePassword(loginUser, userPasswordRequest.getNewPassword(), request);
    return ResultUtil.ok(true);
  }

  @PostMapping("/email")
  public ResponseEntity<BaseResponse<Boolean>> updateEmail(@RequestBody UserEmailRequest userEmailRequest, HttpServletRequest request) {
    // 从请求头获取token
    String token = request.getHeader(LOGIN_TOKEN);
    if (token == null) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "Token不存在", request);
    }
    // 通过token获取用户
    User loginUser = userService.getUserByToken(token, request);
    String password = userEmailRequest.getPassword();
    if (!PasswordUtil.checkPassword(password, loginUser.getPassword())) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "原密码错误", request);
    }
    loginUser.setEmail(userEmailRequest.getNewEmail());
    userService.updateById(loginUser);
    return ResultUtil.ok(true);
  }
}
