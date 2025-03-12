package com.birdy.blogbackend.event.user

import com.birdy.blogbackend.domain.entity.OAuth
import com.birdy.blogbackend.domain.entity.User
import com.birdy.blogbackend.event.Cancellable
import jakarta.servlet.http.HttpServletRequest
import lombok.Setter
import org.springframework.context.ApplicationEvent

/**
 * @author birdy
 */
class UserLoginEvent(source: Any, private val user: User, private val request: HttpServletRequest) :
    ApplicationEvent(source), Cancellable {
    @Setter
    private val token: String? = null

    @Setter
    private val oAuth: OAuth? = null
    override var isCancelled: Boolean = false
}
