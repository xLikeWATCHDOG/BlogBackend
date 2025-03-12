package com.birdy.blogbackend.event

import com.birdy.blogbackend.domain.entity.Log
import jakarta.servlet.http.HttpServletRequest
import lombok.Getter
import lombok.Setter
import lombok.extern.slf4j.Slf4j
import org.springframework.context.ApplicationEvent

/**
 * @author birdy
 */
@Getter
@Slf4j
class LogAddEvent(source: Any, @field:Setter val log: Log, private val request: HttpServletRequest) :
    ApplicationEvent(source), Cancellable {
    override var isCancelled: Boolean = false
}
