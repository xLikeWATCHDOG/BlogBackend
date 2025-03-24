package com.birdy.blogbackend.domain.vo.request.modpack;

import com.alibaba.excel.annotation.ExcelProperty;
import com.birdy.blogbackend.domain.vo.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ModpackQueryRequest extends PageRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Schema(description = "UID")
  @ExcelProperty(value = "UID", index = 1)
  private Long uid;

  @Schema(description = "状态")
  @ExcelProperty(value = "状态", index = 2)
  private Integer status;

  private Boolean admin = false;
}
