package com.birdy.blogbackend.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.birdy.blogbackend.domain.enums.ReportStatus;
import com.birdy.blogbackend.domain.enums.ReportType;
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
@Table("report")
@Data
public class Report implements Serializable {
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

  @Schema(description = "举报者")
  @ExcelProperty(value = "举报者", index = 1)
  private Long reporter;

  @Schema(description = "被举报者")
  @ExcelProperty(value = "被举报者", index = 2)
  private Long itemId;

  @Schema(description = "举报类型")
  @ExcelProperty(value = "举报类型", index = 3)
  private Integer type;

  @Schema(description = "举报内容")
  @ExcelProperty(value = "举报内容", index = 4)
  private String reason;

  @Schema(description = "状态")
  @ExcelProperty(value = "状态", index = 5)
  private Integer status;

  @Schema(description = "创建时间")
  @ExcelProperty(value = "创建时间", index = 6)
  private Date createTime;

  @Schema(description = "更新时间")
  @ExcelProperty(value = "更新时间", index = 7)
  @Column(onUpdateValue = "now()")
  private Date updateTime;

  @Schema(description = "可用性")
  @ExcelProperty(value = "可用性", index = 8)
  @Column(isLogicDelete = true)
  private Integer available;

  public ReportStatus getReportStatus() {
    if (status == null) {
      return ReportStatus.WAITING;
    }
    return ReportStatus.fromCode(status);
  }

  public void setReportStatus(ReportStatus reportStatus) {
    this.status = reportStatus.getCode();
  }

  public ReportType getReportType() {
    return ReportType.valueOf(type);
  }
}
