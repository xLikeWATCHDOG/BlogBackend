package com.birdy.blogbackend.domain.enums;

import lombok.Getter;

/**
 * @author birdy
 */

@Getter
public enum ArticleStatus {
  /**
   * 正常
   */
  NORMAL(0, "正常"),
  /**
   * 禁用
   */
  BANNED(1, "禁用"),
  /**
   * 删除
   */
  DELETED(2, "删除");

  final int code;
  final String desc;

  ArticleStatus(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ArticleStatus valueOf(int value) {
    for (ArticleStatus status : ArticleStatus.values()) {
      if (status.code == value) {
        return status;
      }
    }
    return BANNED;
  }
}
