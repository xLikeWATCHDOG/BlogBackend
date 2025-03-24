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
@Table("security_log")
@Data
public class SecurityLog implements Serializable {
  @Serial
  @Column(ignore = true)
  private static final long serialVersionUID = 1L;
  /**
   * id
   */
  @Id(keyType = KeyType.Auto)
  @Schema(description = "ID")
  @ExcelProperty(value = "ID", index = 0)
  private Long id;

  @Schema(description = "UID")
  @ExcelProperty(value = "UID", index = 1)
  private Long uid;

  @Schema(description = "头像")
  @ExcelProperty(value = "头像", index = 2)
  private String avatar;

  @Schema(description = "标题")
  @ExcelProperty(value = "标题", index = 3)
  private String title;

  @Schema(description = "类型")
  @ExcelProperty(value = "类型", index = 4)
  private String types;

  @Schema(description = "IP")
  @ExcelProperty(value = "IP", index = 5)
  private String ip;

  @Schema(description = "详细")
  @ExcelProperty(value = "详细", index = 6)
  private String info;

  @Schema(description = "创建时间")
  @ExcelProperty(value = "创建时间", index = 7)
  private Date createTime;

  @Schema(description = "更新时间")
  @ExcelProperty(value = "更新时间", index = 8)
  @Column(onUpdateValue = "now()")
  private Date updateTime;

  @Schema(description = "可用性")
  @ExcelProperty(value = "可用性", index = 9)
  @Column(isLogicDelete = true)
  private Integer available;
}
