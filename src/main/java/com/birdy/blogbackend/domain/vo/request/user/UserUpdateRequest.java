package com.birdy.blogbackend.domain.vo.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class UserUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer gender;

    private String username;
}
