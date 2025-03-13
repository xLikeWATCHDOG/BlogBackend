package com.birdy.blogbackend.service

import com.birdy.blogbackend.domain.entity.User
import com.birdy.blogbackend.domain.vo.request.PhoneLoginRequest
import com.birdy.blogbackend.domain.vo.request.UserLoginRequest
import com.mybatisflex.core.service.IService
import jakarta.servlet.http.HttpServletRequest
import org.jetbrains.annotations.Nullable

interface UserService : IService<User?> {
    @Nullable
    fun getLoginUserIgnoreError(request: HttpServletRequest): User?
    fun checkStatus(user: User, request: HttpServletRequest): Boolean
    fun userLogin(userLoginRequest: UserLoginRequest, request: HttpServletRequest): User
    fun checkDuplicates(email: String, request: HttpServletRequest): Boolean
    fun checkDuplicatesIgnoreError(userName: String, request: HttpServletRequest): Boolean
    fun generateUserName(login: String, prefix: String, request: HttpServletRequest): String
    fun phoneLogin(phoneLoginRequest: PhoneLoginRequest, request: HttpServletRequest): User
    fun getLoginUser(request: HttpServletRequest): User
}