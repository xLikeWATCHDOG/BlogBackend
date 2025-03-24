package com.birdy.blogbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author birdy
 */
@Data
@Component
@ConfigurationProperties(prefix = "common")
public class ConfigProperties {
  @NestedConfigurationProperty
  public TencentConfig tencent;

  @NestedConfigurationProperty
  public CaptchaConfig captcha;

  @NestedConfigurationProperty
  public AliyunConfig aliyun;

  @NestedConfigurationProperty
  public SmsConfig sms;

  @NestedConfigurationProperty
  public QQConfig qq;

  @NestedConfigurationProperty
  public GithubConfig github;

  @NestedConfigurationProperty
  public AlistConfig alist;

  @Data
  public static class TencentConfig {
    private String secretId;
    private String secretKey;
  }

  @Data
  public static class CaptchaConfig {
    private String captchaAppId;
    private String appSecretKey;
    private boolean enable;
  }

  @Data
  public static class AliyunConfig {
    private String secretId;
    private String secretKey;

  }

  @Data
  public static class SmsConfig {
    private String signName;
    private String templateCode;
  }

  @Data
  public static class QQConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private boolean enable;
  }

  @Data
  public static class GithubConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private boolean enable;
  }

  @Data
  public static class AlistConfig {
    private String path;
  }
}
