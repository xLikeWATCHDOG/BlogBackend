package com.birdy.blogbackend.domain.vo.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class UserEmailRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String newEmail;
    private String password;
}
