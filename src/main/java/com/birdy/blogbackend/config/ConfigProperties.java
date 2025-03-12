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
    public SmsConfig sms;

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
    public static class SmsConfig {
        private String secretId;
        private String secretKey;
        private String smsSdkAppId;
        private String signName;
        private String templateId;
    }
}
