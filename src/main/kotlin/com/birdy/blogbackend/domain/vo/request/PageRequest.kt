package com.birdy.blogbackend.domain.vo.request

import com.birdy.blogbackend.constant.CommonConstant
import java.io.Serializable

/**
 * 分页请求
 */
data class PageRequest(
    var current: Long = 1,
    var pageSize: Long = 10,
    var sortField: String? = null,
    var sortOrder: String = CommonConstant.SORT_ORDER_DESC
) : Serializable
