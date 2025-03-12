package com.birdy.blogbackend.event.user

import com.birdy.blogbackend.domain.entity.User
import com.birdy.blogbackend.event.Cancellable
import jakarta.servlet.http.HttpServletRequest
import lombok.Getter

/**
 * @author birdy
 */
@Getter
class UserCreateEvent(private val request: HttpServletRequest, private val user: User) :
    Cancellable {
    override var isCancelled: Boolean = false
}
