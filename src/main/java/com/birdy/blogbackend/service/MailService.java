package com.birdy.blogbackend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;

/**
 * @author birdy
 */
public interface MailService {
    @Async
    void getEmailCode(String to, String code, HttpServletRequest request);

    @Async
    void forgetPassword(String to, String token, HttpServletRequest request);
}
