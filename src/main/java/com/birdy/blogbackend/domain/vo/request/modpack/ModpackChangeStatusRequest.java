package com.birdy.blogbackend.domain.vo.request.modpack;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class ModpackChangeStatusRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private Long id;
  private String reason;
  private Integer status;
}
