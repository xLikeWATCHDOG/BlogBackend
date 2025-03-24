package com.birdy.blogbackend.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author birdy
 */
@Table(value = "log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Log implements Serializable {
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

  @Schema(description = "请求ID")
  @ExcelProperty(value = "请求ID", index = 2)
  private String requestId;

  @Schema(description = "IP")
  @ExcelProperty(value = "IP", index = 3)
  private String ip;

  @Schema(description = "请求头")
  @ExcelProperty(value = "请求头", index = 4)
  private String headers;

  @Schema(description = "URL")
  @ExcelProperty(value = "URL", index = 5)
  private String url;

  @Schema(description = "请求方法")
  @ExcelProperty(value = "请求方法", index = 6)
  private String method;

  @Schema(description = "请求参数")
  @ExcelProperty(value = "请求参数", index = 7)
  private String params;

  @Schema(description = "结果")
  @ExcelProperty(value = "结果", index = 8)
  private String result;

  @Schema(description = "请求代码")
  @ExcelProperty(value = "请求代码", index = 9)
  private Integer httpCode;

  @Schema(description = "花费时间")
  @ExcelProperty(value = "花费时间", index = 10)
  private Long cost;

  @Schema(description = "创建时间")
  @ExcelProperty(value = "创建时间", index = 11)
  private Date createTime;

  @Schema(description = "更新时间")
  @ExcelProperty(value = "更新时间", index = 12)
  @Column(onUpdateValue = "now()")
  private Date updateTime;

  @Schema(description = "可用性")
  @ExcelProperty(value = "可用性", index = 13)
  @Column(isLogicDelete = true)
  private Integer available;
}
