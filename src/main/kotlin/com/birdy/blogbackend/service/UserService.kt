package com.birdy.blogbackend.service

import com.birdy.blogbackend.domain.entity.User
import com.birdy.blogbackend.domain.enums.OAuthPlatform
import com.birdy.blogbackend.domain.vo.request.phone.PhoneLoginRequest
import com.birdy.blogbackend.domain.vo.request.user.UserLoginRequest
import com.birdy.blogbackend.domain.vo.request.user.UserRegisterRequest
import com.mybatisflex.core.service.IService
import jakarta.servlet.http.HttpServletRequest
import me.zhyd.oauth.model.AuthUser
import org.jetbrains.annotations.Nullable
import org.springframework.scheduling.annotation.Async
import org.springframework.web.multipart.MultipartFile

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
    fun register(userRegisterRequest: UserRegisterRequest, request: HttpServletRequest): User

    @Async
    fun generateDefaultAvatar(uid: Long, request: HttpServletRequest)

    @Async
    fun generateDefaultAvatar(user: User, request: HttpServletRequest)
    fun getUserByToken(token: String, request: HttpServletRequest): User
    fun refreshToken(user: User, request: HttpServletRequest): String
    fun logout(request: HttpServletRequest)
    fun getByEmail(email: String, request: HttpServletRequest): User
    fun updatePassword(user: User, password: String, request: HttpServletRequest)
    fun oAuthLogin(authUser: AuthUser, oAuthPlatform: OAuthPlatform, request: HttpServletRequest): User

    @Async
    fun downloadAvatar(user: User, avatarUrl: String, request: HttpServletRequest)
    fun setupAvatar(user: User, file: MultipartFile, request: HttpServletRequest)
    fun countToday(): Long
    fun getAdmins(): List<User>

    @Async
    fun sendReportMailToAdmin(code: String, content: String, request: HttpServletRequest)
}