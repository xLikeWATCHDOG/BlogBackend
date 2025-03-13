package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.UserDao;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.enums.UserStatus;
import com.birdy.blogbackend.domain.vo.request.PhoneLoginRequest;
import com.birdy.blogbackend.domain.vo.request.UserLoginRequest;
import com.birdy.blogbackend.event.user.UserLoginEvent;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.UserService;
import com.birdy.blogbackend.util.CaffeineFactory;
import com.birdy.blogbackend.util.NetUtil;
import com.birdy.blogbackend.util.NumberUtil;
import com.birdy.blogbackend.util.PasswordUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.birdy.blogbackend.constant.UserConstant.LOGIN_TOKEN;
import static com.birdy.blogbackend.constant.UserConstant.USER_LOGIN_STATE;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final Cache<String, User> FORGET_PASSWORD_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
    public static final Cache<String, User> TOKEN_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(7, TimeUnit.DAYS).build();
    private static final Cache<String, Integer> FAIL_LOGIN_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
    private static final Cache<Long, User> USER_CACHE = CaffeineFactory.INSTANCE.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).build();

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private UserDao userDao;

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
        UserStatus userStatus = UserStatus.valueOf(String.valueOf(status));
        if (userStatus == UserStatus.DELETED) {
            throw new BusinessException(ReturnCode.PARAMS_ERROR, "账户已删除", status, request);
        }
        if (userStatus == UserStatus.BANNED) {
            throw new BusinessException(ReturnCode.PARAMS_ERROR, "账户已禁用", status, request);
        }

        return true;
    }

    public void setLoginState(User user, HttpServletRequest request, boolean check) {
        if (check) {
            checkStatus(user, request);
        }
        // 清除之前的Token
        String oldToken = (String) request.getSession().getAttribute(LOGIN_TOKEN);
        if (StringUtils.isNotBlank(oldToken)) {
            TOKEN_CACHE.invalidate(oldToken);
        }
        // 登录成功，设置登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 生成Token
        String token = UUID.randomUUID().toString();
        TOKEN_CACHE.put(token, user);
        request.getSession().setAttribute(LOGIN_TOKEN, token);
    }

    public void setLoginState(User user, HttpServletRequest request) {
        setLoginState(user, request, true);
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
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getUid() == null) {
            throw new BusinessException(ReturnCode.NOT_LOGIN_ERROR, "未登录", request);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long uid = currentUser.getUid();
        String oldPass = currentUser.getPassword();
        currentUser = this.getById(uid);
        if (currentUser == null || !currentUser.getPassword().equals(oldPass)) {
            throw new BusinessException(ReturnCode.NOT_LOGIN_ERROR, "未登录", request);
        }
        checkStatus(currentUser, request);
        request.getSession().setAttribute(USER_LOGIN_STATE, currentUser);
        return currentUser;
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
        UserLoginEvent event = new UserLoginEvent(this, user, request);
        eventPublisher.publishEvent(event);
        if (event.isCancelled()) {
            throw new BusinessException(ReturnCode.CANCELLED, "登录被取消", request);
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
            user.setPhone(phoneLoginRequest.getPhone());
            this.save(user);
        }
        setLoginState(user, request);
        return user;
    }
}
