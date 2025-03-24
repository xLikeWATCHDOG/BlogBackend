package com.birdy.blogbackend.domain.enums

/**
 * 错误码
 */
enum class ReturnCode(val code: Int, val message: String, val status: Int) {
  /**
   * 操作成功
   */
  SUCCESS(20000, "Operation succeeded"),

  /**
   * 请求参数错误
   */
  PARAMS_ERROR(40000, "Request parameter error", StatusCode.BAD_REQUEST),

  /**
   * 数据已存在
   */
  DATA_EXISTED(40001, "Data already exists", StatusCode.BAD_REQUEST),

  /**
   * 未登录
   */
  NOT_LOGIN_ERROR(40100, "Not logged in", StatusCode.UNAUTHORIZED),

  /**
   * 无权限
   */
  NO_AUTH_ERROR(40101, "No permission", StatusCode.UNAUTHORIZED),

  /**
   * 验证失败
   */
  VALIDATION_FAILED(40102, "Validation failed", StatusCode.UNAUTHORIZED),

  /**
   * 账号被封禁
   */
  ACCOUNT_BANED(40103, "The account has been baned.", StatusCode.UNAUTHORIZED),

  /**
   * 请求数据不存在
   */
  NOT_FOUND_ERROR(40400, "Request data does not exist", StatusCode.NOT_FOUND),

  /**
   * 禁止访问
   */
  FORBIDDEN_ERROR(40300, "Forbidden Access", StatusCode.FORBIDDEN),

  /**
   * 请求次数过多
   */
  TOO_MANY_REQUESTS_ERROR(42900, "Too many requests", StatusCode.TOO_MANY_REQUESTS),

  /**
   * 系统内部异常
   */
  SYSTEM_ERROR(50000, "System internal exception", StatusCode.INTERNAL_SERVER_ERROR),

  /**
   * 操作失败
   */
  OPERATION_ERROR(50001, "Operation failed", StatusCode.INTERNAL_SERVER_ERROR),

  /**
   * 被取消
   */
  CANCELLED(50002, "Cancelled", StatusCode.INTERNAL_SERVER_ERROR);

  constructor(code: Int, message: String) : this(code, message, 200)
}
