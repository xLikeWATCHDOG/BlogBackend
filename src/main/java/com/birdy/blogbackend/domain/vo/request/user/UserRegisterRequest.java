package com.birdy.blogbackend.domain.vo.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class UserRegisterRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private String username;

  private String email;

  private String code;

  private String password;
}
