package com.birdy.blogbackend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.birdy.blogbackend.dao.OAuthDao;
import com.birdy.blogbackend.dao.PermissionDao;
import com.birdy.blogbackend.dao.UserDao;
import com.birdy.blogbackend.domain.entity.Modpack;
import com.birdy.blogbackend.domain.entity.OAuth;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.OAuthPlatform;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.enums.UserGender;
import com.birdy.blogbackend.domain.enums.UserStatus;
import com.birdy.blogbackend.domain.vo.request.phone.PhoneLoginRequest;
import com.birdy.blogbackend.domain.vo.request.user.UserLoginRequest;
import com.birdy.blogbackend.domain.vo.request.user.UserRegisterRequest;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.MailService;
import com.birdy.blogbackend.service.PermissionService;
import com.birdy.blogbackend.service.PhotoService;
import com.birdy.blogbackend.service.UserService;
import com.birdy.blogbackend.util.*;
import com.github.benmanes.caffeine.cache.Cache;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import net.coobird.thumbnailator.Thumbnails;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.birdy.blogbackend.constant.UserConstant.LOGIN_TOKEN;
import static com.birdy.blogbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author birdy
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
  public static final Cache<String, User> FORGET_PASSWORD_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
  public static final Cache<String, User> TOKEN_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(7, TimeUnit.DAYS).build();
  private static final Cache<String, Integer> FAIL_LOGIN_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
  private static final Cache<String, User> tokenCache = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(7, TimeUnit.DAYS).build();
  @Autowired
  private UserDao userDao;
  @Autowired
  private PhotoService photoService;
  @Autowired
  private OAuthDao oAuthDao;
  @Autowired
  private PermissionDao permissionDao;
  @Autowired
  private PermissionService permissionService;
  @Autowired
  private MailService mailService;

  private void validateUserCredentials(String userName, String userPassword, HttpServletRequest request) {
    if (StringUtils.isAnyBlank(userName, userPassword)) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "参数为空", request);
    }
    if (checkDuplicates(userName, request)) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "账号重复", userName, request);
    }
    // userName只能存在英文、数字、下划线、横杠、点，并且长度小于16
    if (!userName.matches("^[a-zA-Z0-9_-]{1,16}$")) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "账号格式错误", userName, request);
    }
    // 检查密码不过分简单。密码必须包含大小写字母、数字、特殊符号中的三种，且长度为8-30位
    if (!userPassword.matches("^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{8,30}$")) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "密码格式错误", userPassword, request);
    }
  }


  @Override
  public boolean checkDuplicates(@NotNull String email, @NotNull HttpServletRequest request) {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("email", email);
    long count = this.count(queryWrapper);
    if (count > 0) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "账号重复", email, request);
    }
    return false;
  }

  @Override
  public boolean checkDuplicatesIgnoreError(@NotNull String userName, @NotNull HttpServletRequest request) {
    try {
      return checkDuplicates(userName, request);
    } catch (Exception e) {
      return true;
    }
  }

  @Override
  public @NotNull String generateUserName(@NotNull String login, @NotNull String prefix, @NotNull HttpServletRequest request) {
    String username;
    if (checkDuplicatesIgnoreError(login, request)) {
      do {
        // 随机生成用户名
        username = login + "_" + NumberUtil.getRandomCode(5);
        // 判断随机后的用户名是否符合规范
        if (!username.matches("^[a-zA-Z0-9_-]{1,16}$")) {
          // 随机生成新的用户名,取UUID的前6位
          UUID un = UUID.randomUUID();
          username = prefix + "_" + un.toString().substring(0, 6);
        }
        // 判断是否重复
      } while (checkDuplicatesIgnoreError(username, request));
    } else {
      username = login;
    }
    return username;
  }

  @Override
  public boolean checkStatus(@NotNull User user, @NotNull HttpServletRequest request) {
    if (user.getStatus() == null) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "参数为空", request);
    }
    int status = user.getStatus();
    UserStatus userStatus = UserStatus.valueOf(status);
    if (userStatus == UserStatus.DELETED) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "账户已删除", status, request);
    }
    if (userStatus == UserStatus.BANNED) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "账户已禁用", status, request);
    }

    return true;
  }

  public void setLoginState(User user, HttpServletRequest request) {
    if (user == null) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "用户对象为空", request);
    }

    checkStatus(user, request);

    // 清除之前的 Token
    String oldToken = (String) request.getSession().getAttribute(LOGIN_TOKEN);
    if (StringUtils.isNotBlank(oldToken)) {
      TOKEN_CACHE.invalidate(oldToken);
    }

    // 生成新的 Token
    String token = UUID.randomUUID().toString();
    TOKEN_CACHE.put(token, user);

    // 存入会话
    request.getSession().setAttribute(USER_LOGIN_STATE, user);
    request.getSession().setAttribute(LOGIN_TOKEN, token);
  }


  // 检查用户的UID和IP地址是否在缓存中
  public boolean checkFailLogin(String account, HttpServletRequest request) {
    String ip = NetUtil.getIpAddress(request);
    Integer accountFailCount = FAIL_LOGIN_CACHE.getIfPresent(account);
    Integer ipFailCount = FAIL_LOGIN_CACHE.getIfPresent(ip);
    return (accountFailCount != null && accountFailCount >= 5) || (ipFailCount != null && ipFailCount >= 5);
  }

  // 将用户的UID和IP地址添加到缓存中
  public void addFailLogin(String account, HttpServletRequest request) {
    String ip = NetUtil.getIpAddress(request);
    Integer accountFailCount = FAIL_LOGIN_CACHE.getIfPresent(account);
    FAIL_LOGIN_CACHE.put(account, accountFailCount == null ? 1 : accountFailCount + 1);
    Integer ipFailCount = FAIL_LOGIN_CACHE.getIfPresent(ip);
    FAIL_LOGIN_CACHE.put(ip, ipFailCount == null ? 1 : ipFailCount + 1);
  }

  @Override
  public BaseMapper<User> getMapper() {
    return userDao.getMapper();
  }

  /**
   * 获取当前登录用户
   */
  @Override
  public @NotNull User getLoginUser(@NotNull HttpServletRequest request) {
    // 先检查会话中的登录态
    Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
    if (userObj instanceof User currentUser && currentUser.getUid() != null) {
      return currentUser;
    }

    // 检查Token缓存
    String token = (String) request.getSession().getAttribute(LOGIN_TOKEN);
    if (StringUtils.isNotBlank(token)) {
      User cachedUser = TOKEN_CACHE.getIfPresent(token);
      if (cachedUser != null) {
        request.getSession().setAttribute(USER_LOGIN_STATE, cachedUser);
        return cachedUser;
      }
    }

    throw new BusinessException(ReturnCode.NOT_LOGIN_ERROR, "未登录", request);
  }


  @Override
  public @Nullable User getLoginUserIgnoreError(@NotNull HttpServletRequest request) {
    try {
      return getLoginUser(request);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public @NotNull User userLogin(@NotNull UserLoginRequest userLoginRequest, @NotNull HttpServletRequest request) {
    User user = getLoginUserIgnoreError(request);
    if (user != null) {
      return user;
    }
    String email = userLoginRequest.getEmail();
    String password = userLoginRequest.getPassword();
    // 判断是否是邮箱登录
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("email", email);
    user = this.getOne(queryWrapper);
    if (user == null) {
      throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "账户信息不存在", request);
    }
    long uid = user.getUid();
    if (checkFailLogin(email, request)) {
      throw new BusinessException(ReturnCode.TOO_MANY_REQUESTS_ERROR, "登录失败次数过多，请稍后再试", request);
    }
    // 检查密码
    if (!PasswordUtil.checkPassword(password, user.getPassword())) {
      addFailLogin(email, request);
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "密码错误", request);
    }
    setLoginState(user, request);
    return user;
  }

  @Override
  public @NotNull User phoneLogin(@NotNull PhoneLoginRequest phoneLoginRequest, @NotNull HttpServletRequest request) {
    // 检查是否有目标手机号的用户存在
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("phone", phoneLoginRequest.getPhone());
    User user = this.getOne(queryWrapper);
    if (user == null) {
      // 不存在则创建新用户
      user = new User();
      user.setUsername(phoneLoginRequest.getPhone());
      user.setPassword("UNKNOWN");
      user.setGender(3);
      user.setPhone(phoneLoginRequest.getPhone());
      user.setStatus(0);
      this.save(user);
    }
    setLoginState(user, request);
    return user;
  }

  @Override
  public @NotNull User register(@NotNull UserRegisterRequest userRegisterRequest, @NotNull HttpServletRequest request) {
    String email = userRegisterRequest.getEmail();
    String password = userRegisterRequest.getPassword();
    // 检查邮箱是否已注册
    if (checkDuplicates(email, request)) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "账号重复", email, request);
    }
    // 检查密码
    if (!password.matches("^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{8,30}$")) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "密码格式错误", password, request);
    }
    // 创建用户
    User user = new User();
    user.setEmail(email.toLowerCase());
    user.setPassword(PasswordUtil.encodePassword(password));
    user.setUsername(generateUserName(email, "user", request));
    user.setStatus(0);
    this.save(user);
    setLoginState(user, request);
    return user;
  }

  @Override
  public @NotNull User getByEmail(@NotNull String email, @NotNull HttpServletRequest request) {
    if (StringUtils.isBlank(email)) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "邮箱为空", request);
    }
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("email", email);
    return this.getOne(queryWrapper);
  }

  @Async
  @Override
  public void generateDefaultAvatar(long uid, @NotNull HttpServletRequest request) {
    String character = String.valueOf(uid);

    int width = 460;
    int height = 460;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();

    // 白色背景
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);

    // 黑色字体
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.BOLD, 200));
    FontMetrics fm = g.getFontMetrics();
    int x = (width - fm.stringWidth(character)) / 2;
    int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
    g.drawString(character, x, y);

    g.dispose();

    try {
      String ext = "png";
      String fileName = character + "." + ext;
      photoService.savePhotoByMd5(character, ext, 4300, request);
      Path path = Paths.get("photos", fileName);
      Files.createDirectories(path.getParent());
      ImageIO.write(image, "png", path.toFile());
    } catch (IOException e) {
      throw new BusinessException(ReturnCode.SYSTEM_ERROR, "Failed to generate avatar", request);
    }
  }

  private void clearAvatar(User user, HttpServletRequest request) {
    if (user == null) {
      return;
    }
    String avatar = user.getAvatar();
    if (StringUtils.isBlank(avatar)) {
      return;
    }
    user.setAvatar(null);
    this.updateById(user);
  }

  @Async
  @Override
  public void generateDefaultAvatar(@NotNull User user, @NotNull HttpServletRequest request) {
    clearAvatar(user, request);
    String username = user.getUsername();
    Long uid = user.getUid();
    String character = username.chars().mapToObj(c -> (char) c).filter(Character::isLetterOrDigit).findFirst().map(String::valueOf).orElse(String.valueOf(uid % 10));

    int width = 460;
    int height = 460;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();

    Random random = new Random();
    Color backgroundColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    g.setColor(backgroundColor);
    g.fillRect(0, 0, width, height);

    Color textColor = new Color(255 - backgroundColor.getRed(), 255 - backgroundColor.getGreen(), 255 - backgroundColor.getBlue());
    g.setColor(textColor);
    g.setFont(new Font("Arial", Font.BOLD, 200));
    FontMetrics fm = g.getFontMetrics();
    int x = (width - fm.stringWidth(character)) / 2;
    int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
    g.drawString(character, x, y);

    g.dispose();

    try {
      String md5 = DigestUtils.md5DigestAsHex((character + textColor.getBlue() + textColor.getGreen() + textColor.getRed()).getBytes());
      String ext = "png";
      String fileName = md5 + "." + ext;
      photoService.savePhotoByMd5(md5, ext, 4300, request);
      Path path = Paths.get("photos", fileName);
      Files.createDirectories(path.getParent());
      ImageIO.write(image, "png", path.toFile());
      user.setAvatar(md5);
      this.updateById(user);
    } catch (IOException e) {
      throw new BusinessException(ReturnCode.SYSTEM_ERROR, "Failed to generate avatar", request);
    }
  }

  @Override
  public @NotNull String refreshToken(@NotNull User user, @NotNull HttpServletRequest request) {
    UUID uuid = UUID.randomUUID();
    String token = uuid.toString();
    TOKEN_CACHE.put(token, user);
    return token;
  }

  @Override
  public @NotNull User getUserByToken(@NotNull String token, @NotNull HttpServletRequest request) {
    User user = TOKEN_CACHE.getIfPresent(token);
    if (user == null) {
      throw new BusinessException(ReturnCode.NOT_LOGIN_ERROR, "未登录", request);
    }
    return user;
  }

  @Override
  public void logout(@NotNull HttpServletRequest request) {
    // 从请求头获取token
    String token = request.getHeader(LOGIN_TOKEN);
    if (token == null) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "Token不存在", request);
    }
    TOKEN_CACHE.invalidate(token);

    request.getSession().removeAttribute(USER_LOGIN_STATE);
    request.getSession().removeAttribute(LOGIN_TOKEN);
  }

  public boolean checkIsLogin(HttpServletRequest request) {
    try {
      getLoginUser(request);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void updatePassword(@NotNull User user, @NotNull String password, @NotNull HttpServletRequest request) {
    if (!password.matches("^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{8,30}$")) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "密码格式错误", password, request);
    }
    // 判断修改的密码是否和原密码相同
    if (PasswordUtil.checkPassword(password, user.getPassword())) {
      throw new BusinessException(ReturnCode.PARAMS_ERROR, "新密码不能和原密码相同", request);
    }
    user.setPassword(PasswordUtil.encodePassword(password));
    this.updateById(user);
  }

  @Override
  public @NotNull User oAuthLogin(@NotNull AuthUser authUser, @NotNull OAuthPlatform oAuthPlatForm, @NotNull HttpServletRequest request) {
    // log.info("用户： {}", GsonProvider.normal().toJson(authUser));
    String username = authUser.getUsername();
    AuthToken authToken = authUser.getToken();
    JSONObject rawUserInfo = authUser.getRawUserInfo();
    String avatar = authUser.getAvatar();
    String email = authUser.getEmail();
    String openId = rawUserInfo.getString("id");
    String token = authToken.getAccessToken();
    AuthUserGender authUserGender = authUser.getGender();
    UserGender userGender = UserGender.valueOf(authUserGender);
    // 判断是否已经绑定过
    QueryWrapper oAuthQueryWrapper = new QueryWrapper();
    oAuthQueryWrapper.eq("platform", oAuthPlatForm.getCode());
    if (checkIsLogin(request)) {
      // bind
      User user = getLoginUser(request);
      oAuthQueryWrapper.eq("uid", user.getUid());
      OAuth oAuth;
      QueryWrapper oAuthQueryWrapper1 = new QueryWrapper();
      oAuthQueryWrapper1.eq("platform", oAuthPlatForm.getCode());
      oAuthQueryWrapper1.eq("openId", openId);
      oAuth = oAuthDao.getOne(oAuthQueryWrapper1);
      oAuth = new OAuth();
      oAuth.setUid(user.getUid());
      oAuth.setPlatform(oAuthPlatForm.getCode());
      oAuth.setOpenId(openId);
      oAuth.setToken(token);
      boolean saveResult = oAuthDao.save(oAuth);
      if (!saveResult) {
        throw new BusinessException(ReturnCode.SYSTEM_ERROR, "添加失败，数据库错误", request);
      }
      return user;
    }
    oAuthQueryWrapper.eq("openId", openId);
    OAuth oAuth = oAuthDao.getOne(oAuthQueryWrapper);
    if (oAuth != null) {
      // 已经绑定过，直接登录
      User user = this.getById(oAuth.getUid());
      if (user == null) {
        throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "账户信息不存在", request);
      }
      setLoginState(user, request);
      return user;
    }
    // 未绑定，创建账户
    User user = new User();
    username = generateUserName(username, oAuthPlatForm.name().toLowerCase(), request);
    user.setUsername(username);
    String randomPassword = StringUtil.getRandomString(10);
    user.setPassword(PasswordUtil.encodePassword(randomPassword));
    user.setEmail(email.toLowerCase());
    user.setUserGender(userGender);
    boolean saveResult = this.save(user);
    if (!saveResult) {
      throw new BusinessException(ReturnCode.SYSTEM_ERROR, "添加失败，数据库错误", request);
    }
    downloadAvatar(user, avatar, request);
    // 绑定账户
    oAuth = new OAuth();
    oAuth.setUid(user.getUid());
    oAuth.setPlatform(oAuthPlatForm.getCode());
    oAuth.setOpenId(String.valueOf(openId));
    oAuth.setToken(token);
    saveResult = oAuthDao.save(oAuth);
    if (!saveResult) {
      throw new BusinessException(ReturnCode.SYSTEM_ERROR, "添加失败，数据库错误", request);
    }
    setLoginState(user, request);
    return user;
  }

  @Async
  @Override
  public void downloadAvatar(@NotNull User user, @NotNull String avatarUrl, @NotNull HttpServletRequest request) {
    clearAvatar(user, request);
    try {
      OkHttpClient client = new OkHttpClient();
      okhttp3.Request req = new okhttp3.Request.Builder().url(avatarUrl).build();
      okhttp3.Response res = client.newCall(req).execute();
      InputStream inputStream = Objects.requireNonNull(res.body()).byteStream();
      String ext = Objects.requireNonNull(res.body().contentType()).subtype();
      byte[] data = res.body().bytes();
      String md5 = DigestUtils.md5DigestAsHex(data);
      String fileName = md5 + "." + ext;
      photoService.savePhotoByMd5(md5, ext, data.length, request);
      Path path = Paths.get("photos", fileName);
      Files.createDirectories(path.getParent());
      Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
      user.setAvatar(md5);
      this.updateById(user);
    } catch (IOException e) {
      generateDefaultAvatar(user, request);
      // throw new BusinessException(ReturnCode.OPERATION_ERROR, "Failed to download avatar", avatarUrl, request);
    }
  }


  @Override
  public void setupAvatar(User user, MultipartFile file, HttpServletRequest request) {
    // 获取旧头像
    String oldAvatar = user.getAvatar();
    clearAvatar(user, request);
    if (file == null || file.isEmpty()) {
      throw new BusinessException(ReturnCode.FORBIDDEN_ERROR, "图片为空", request);
    }
    try {
      // 获取图片的长度和宽度
      BufferedImage fi = ImageIO.read(file.getInputStream());
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      Thumbnails.of(file.getInputStream()).size(460, 460).toOutputStream(stream);
      // 获取图片类型拓展名
      String ext = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
      byte[] data = file.getBytes();
      String md5 = DigestUtils.md5DigestAsHex(data);
      String fileName = md5 + ext;
      photoService.savePhotoByMd5(md5, ext, data.length, request);
      Path path = Paths.get("photos", fileName);
      Files.createDirectories(path.getParent());
      byte[] bytes = stream.toByteArray();
      Files.write(path, bytes);
      user.setAvatar(md5);
      this.updateById(user);
    } catch (IOException e) {
      generateDefaultAvatar(user, request);
      throw new BusinessException(ReturnCode.OPERATION_ERROR, "Failed to upload avatar", request);
    }
  }

  @Override
  public long countToday() {
    // 记录今天的登录人数
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("to_char(create_time, 'YYYY-MM-DD')", "to_char(CURRENT_DATE, 'YYYY-MM-DD')");
    return this.count(queryWrapper);
  }

  @Override
  public @NotNull List<User> getAdmins() {
    List<User> ret = new ArrayList<>();
    for (User user : this.list()) {
      if (permissionService.checkPermission(user.getUid(), "group.admin")) {
        ret.add(user);
      }
    }
    return ret;
  }

  @Async
  @Override
  public void sendReportMailToAdmin(@NotNull String code, @NotNull String content, @NotNull HttpServletRequest request) {
    for (User admin : getAdmins()) {
      mailService.sendThanksMailToAdmin(admin.getEmail(), code, content, request);
    }
  }

  @Async
  @Override
  public void sendDeleteModpackMailToAdmin(@NotNull String reason, @NotNull Modpack modpack, @NotNull HttpServletRequest request) {
    for (User admin : getAdmins()) {
      mailService.sendDeleteModpackToAdmin(admin.getEmail(), modpack, reason, request);
    }
  }
}
