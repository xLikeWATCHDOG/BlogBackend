package com.birdy.blogbackend.domain.vo.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class TencentCaptchaResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int ret;
    private String ticket;
    /**
     * 腾讯云验证码应用ID
     */
    private Long CaptchaAppId;
    private String bizState;
    private String randstr;
}
