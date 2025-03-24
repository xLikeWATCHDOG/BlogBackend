package com.birdy.blogbackend.domain.vo.request.permission;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class PermissionUpdateRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private Long id;
  private String permission;
  private Long expiry;
}
