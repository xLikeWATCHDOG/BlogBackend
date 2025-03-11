package com.birdy.blogbackend.service

import com.birdy.blogbackend.domain.entity.Log
import com.mybatisflex.core.service.IService
import jakarta.servlet.http.HttpServletRequest
import org.jetbrains.annotations.Nullable
import org.springframework.scheduling.annotation.Async

/**
 * @author birdy
 */
interface LogService : IService<Log?> {
    @Async
    fun addLog(log: Log?, request: HttpServletRequest?)

    @Nullable
    fun getLog(requestId: String?, request: HttpServletRequest?): Log?
}
