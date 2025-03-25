package com.birdy.blogbackend.service

import com.birdy.blogbackend.domain.entity.Modpack
import com.birdy.blogbackend.domain.entity.Report
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

  @Async
  fun sendThanksMail(to: String, code: String, request: HttpServletRequest)

  @Async
  fun sendThanksMailToAdmin(to: String, code: String, content: String, request: HttpServletRequest)

  @Async
  fun sendReportChangeMailToUser(report: Report, request: HttpServletRequest)

  @Async
  fun sendDeleteModpackToAdmin(to: String, modpack: Modpack, reason: String, request: HttpServletRequest)
}
