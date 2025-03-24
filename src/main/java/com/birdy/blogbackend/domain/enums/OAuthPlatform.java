package com.birdy.blogbackend.domain.enums;

import lombok.Getter;

/**
 * @author birdy
 */

@Getter
public enum OAuthPlatform {
  /**
   * 微信
   */
  WECHAT(1, "微信"),
  /**
   * QQ
   */
  QQ(2, "QQ"),
  /**
   * GitHub
   */
  GITHUB(3, "GitHub"),
  /**
   * 支付宝
   */
  ALIPAY(4, "支付宝"),
  /**
   * Gitee
   */
  GITEE(5, "Gitee"),
  /**
   * Microsoft
   */
  MICROSOFT(6, "Microsoft"),
  /**
   * Bilibili
   */
  BILIBILI(7, "哔哩哔哩");
  final int code;
  final String name;

  OAuthPlatform(int code, String name) {
    this.code = code;
    this.name = name;
  }

  public static OAuthPlatform valueOf(int code) {
    for (OAuthPlatform payPlatform : OAuthPlatform.values()) {
      if (payPlatform.code == code) {
        return payPlatform;
      }
    }
    return null;
  }
}
