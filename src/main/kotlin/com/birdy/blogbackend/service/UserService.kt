package com.birdy.blogbackend.service

import com.birdy.blogbackend.domain.entity.User
import com.mybatisflex.core.service.IService
import jakarta.servlet.http.HttpServletRequest
import org.jetbrains.annotations.Nullable

interface UserService : IService<User?> {
    @Nullable
    fun getLoginUserIgnoreError(request: HttpServletRequest): User?
}