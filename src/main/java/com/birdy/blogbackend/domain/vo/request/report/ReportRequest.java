package com.birdy.blogbackend.domain.vo.request.report;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class ReportRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private String detail;
  private Long itemId;
  private String reason;
  private Integer type;
  private Long uid;
}
