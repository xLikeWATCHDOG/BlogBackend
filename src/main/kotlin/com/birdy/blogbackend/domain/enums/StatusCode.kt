package com.birdy.blogbackend.domain.enums

/**
 * 错误码
 */
object StatusCode {
    const val OK: Int = 200
    const val CREATED: Int = 201
    const val BAD_REQUEST: Int = 400
    const val UNAUTHORIZED: Int = 401
    const val FORBIDDEN: Int = 403
    const val NOT_FOUND: Int = 404
    const val TOO_MANY_REQUESTS: Int = 429
    const val INTERNAL_SERVER_ERROR: Int = 500
}
