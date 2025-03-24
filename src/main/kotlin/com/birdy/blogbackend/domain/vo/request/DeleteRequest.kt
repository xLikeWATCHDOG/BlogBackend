package com.birdy.blogbackend.domain.vo.request

import java.io.Serializable

/**
 * 删除请求
 */
data class DeleteRequest(
  var id: Long? = null
) : Serializable {
  companion object {
    @JvmStatic
    private val serialVersionUID: Long = 1L
  }
}
