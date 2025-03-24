package com.birdy.blogbackend.annotation

/**
 * 权限校验
 *
 * @author birdy
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthCheck(
  /**
   * 有任何一个权限
   */
  val any: Array<String> = [""],
  /**
   * 必须有某些权限
   */
  val must: Array<String> = [""]
)

