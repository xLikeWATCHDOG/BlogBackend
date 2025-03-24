package com.birdy.blogbackend.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.birdy.blogbackend.domain.enums.ModpackStatus;
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
@Table("modpack")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Modpack implements Serializable {
  @Serial
  @Column(ignore = true)
  private static final long serialVersionUID = 1L;

  @Schema(description = "ID")
  @ExcelProperty(value = "ID", index = 0)
  @Id(keyType = KeyType.Auto)
  private Long id;

  @Schema(description = "UID")
  @ExcelProperty(value = "UID", index = 1)
  private Long uid;

  @Schema(description = "名称")
  @ExcelProperty(value = "名称", index = 2)
  private String name;

  @Schema(description = "图标ID")
  @ExcelProperty(value = "图标ID", index = 3)
  private Long logoId;

  @Schema(description = "启动参数")
  @ExcelProperty(value = "启动参数", index = 4)
  private String launchArguments;

  @Schema(description = "简介")
  @ExcelProperty(value = "简介", index = 5)
  private String brief;

  @Schema(description = "作者链接")
  @ExcelProperty(value = "作者链接", index = 6)
  private String client;

  @Schema(description = "整合包版本")
  @ExcelProperty(value = "整合包版本", index = 7)
  private String version;

  @Schema(description = "整合包地址")
  @ExcelProperty(value = "整合包地址", index = 8)
  private String filePath;

  @Schema(description = "整合包大小")
  @ExcelProperty(value = "整合包大小", index = 9)
  private Long fileSize;

  @Schema(description = "MD5")
  @ExcelProperty(value = "MD5", index = 10)
  private String md5;

  @Schema(description = "状态")
  @ExcelProperty(value = "状态", index = 11)
  private Integer status;

  @Schema(description = "原因")
  @ExcelProperty(value = "原因", index = 12)
  private String reason;

  @Schema(description = "创建时间")
  @ExcelProperty(value = "创建时间", index = 13)
  @Column(onInsertValue = "now()")
  private Date createTime;

  @Schema(description = "更新时间")
  @ExcelProperty(value = "更新时间", index = 14)
  @Column(onInsertValue = "now()", onUpdateValue = "now()")
  private Date updateTime;

  @Schema(description = "可用性")
  @ExcelProperty(value = "可用性", index = 15)
  private Integer available;

  public ModpackStatus getModpackStatus() {
    if (status == null) {
      return ModpackStatus.WAITING;
    }
    return ModpackStatus.fromCode(status);
  }

  public void setModpackStatus(ModpackStatus modpackStatus) {
    this.status = modpackStatus.getCode();
  }
}
