package com.birdy.blogbackend.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.scheduling.annotation.Async

/**
 * @author birdy
 */
interface MailService {
    @Async
    fun getEmailCode(to: String?, code: String?, request: HttpServletRequest?)

    @Async
    fun forgetPassword(to: String?, token: String?, request: HttpServletRequest?)
}
