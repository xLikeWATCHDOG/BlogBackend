package com.birdy.blogbackend.domain.vo.request.modpack;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class ModpackDeleteRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private String reason;
}
