package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.MailService;
import com.birdy.blogbackend.util.CaffeineFactory;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.TimeUnit;

/**
 * @author birdy
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {
    public static final Cache<String, Boolean> emailSent = CaffeineFactory.INSTANCE.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${spring.mail.username:username@gmail.com}")
    private String from;
    @Value("${website.url}")
    private String websiteUrl;

    @Async
    @Override
    public void getEmailCode(String to, String code, HttpServletRequest request) {
        checkEmail(to);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("邮箱验证码");
            helper.setCc(from);
            Context context = new Context();
            context.setVariable("code", code);
            String text = templateEngine.process("EmailCode", context);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败，邮箱：{}，错误信息：{}", to, e.getMessage());
            throw new BusinessException(ReturnCode.SYSTEM_ERROR, "邮件发送失败，请稍后重试", null);
        }
    }

    @Async
    @Override
    public void forgetPassword(String to, String token, HttpServletRequest request) {
        checkEmail(to);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("找回密码");
            helper.setCc(from);
            Context context = new Context();
            String link = websiteUrl + "/user/forget/" + token;
            context.setVariable("link", link);
            String text = templateEngine.process("ForgetPassword", context);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败，邮箱：{}，错误信息：{}", to, e.getMessage());
            throw new BusinessException(ReturnCode.SYSTEM_ERROR, "邮件发送失败，请稍后重试", null);
        }
    }

    public void checkEmail(String to) {
        Boolean sent = emailSent.getIfPresent(to);
        if (sent != null) {
            throw new BusinessException(ReturnCode.TOO_MANY_REQUESTS_ERROR, "邮箱发送过于频繁", null);
        }
    }
}