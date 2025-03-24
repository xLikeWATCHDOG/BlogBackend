package com.birdy.blogbackend.domain.vo.request.report;

import com.birdy.blogbackend.domain.vo.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportQueryRequest extends PageRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private Long uid;
}
