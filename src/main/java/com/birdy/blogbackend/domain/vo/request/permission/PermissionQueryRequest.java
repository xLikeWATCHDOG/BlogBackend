package com.birdy.blogbackend.domain.vo.request.permission;

import com.birdy.blogbackend.domain.vo.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author birdy
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PermissionQueryRequest extends PageRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private Long id;
  private Long uid;
  private String permission;
  private Long expiry;
  private Date createTime;
  private Date updateTime;
}
