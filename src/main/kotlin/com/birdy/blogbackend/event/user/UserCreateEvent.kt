package com.birdy.blogbackend.event.user

import com.birdy.blogbackend.domain.entity.User
import com.birdy.blogbackend.event.Cancellable
import jakarta.servlet.http.HttpServletRequest
import lombok.Getter
import org.springframework.context.ApplicationEvent

/**
 * @author birdy
 */
@Getter
class UserCreateEvent(source: Any, private val request: HttpServletRequest, private val user: User) :
    ApplicationEvent(source), Cancellable {
    override var isCancelled: Boolean = false
}
