package com.birdy.blogbackend.domain.vo.request.phone;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class PhoneCodeSendRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private String phone;
}
