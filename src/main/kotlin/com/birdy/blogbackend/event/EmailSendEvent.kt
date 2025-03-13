package com.birdy.blogbackend.event

import jakarta.mail.internet.MimeMessage
import jakarta.servlet.http.HttpServletRequest
import lombok.Getter
import lombok.Setter
import lombok.extern.slf4j.Slf4j
import org.springframework.context.ApplicationEvent

@Getter
@Slf4j
class EmailSendEvent(
    source: Any,
    @field:Setter private val to: String,
    @field:Setter private val mimeMessage: MimeMessage,
    private val request: HttpServletRequest
) :
    ApplicationEvent(source), Cancellable {
    override var isCancelled: Boolean = false
}
