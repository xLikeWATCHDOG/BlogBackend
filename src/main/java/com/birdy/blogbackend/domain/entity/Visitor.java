package com.birdy.blogbackend.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author birdy
 */
@Table("visitor")
@Data
public class Visitor implements Serializable {
  @Serial
  @Column(ignore = true)
  private static final long serialVersionUID = 1L;
  /**
   * id
   */
  @Id(keyType = KeyType.Auto)
  @Schema(description = "ID")
  @ExcelProperty(value = "ID", index = 0)
  private Date date;

  @Schema(description = "访问次数")
  @ExcelProperty(value = "访问次数", index = 1)
  private Long count;

  @Schema(description = "创建时间")
  @ExcelProperty(value = "创建时间", index = 2)
  private Date createTime;

  @Schema(description = "更新时间")
  @ExcelProperty(value = "更新时间", index = 3)
  @Column(onUpdateValue = "now()")
  private Date updateTime;

  @Schema(description = "可用性")
  @ExcelProperty(value = "可用性", index = 4)
  @Column(isLogicDelete = true)
  private Integer available;
}
