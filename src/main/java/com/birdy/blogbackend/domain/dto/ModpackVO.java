package com.birdy.blogbackend.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.birdy.blogbackend.domain.enums.ModpackStatus;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
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
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModpackVO implements Serializable {
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

    @Schema(description = "图标ID")
    @ExcelProperty(value = "图标ID", index = 2)
    private Long logoId;

    @Schema(description = "启动参数")
    @ExcelProperty(value = "启动参数", index = 3)
    private String launchArguments;

    @Schema(description = "简介")
    @ExcelProperty(value = "简介", index = 4)
    private String brief;

    @Schema(description = "作者链接")
    @ExcelProperty(value = "作者链接", index = 5)
    private String client;

    @Schema(description = "整合包版本")
    @ExcelProperty(value = "整合包版本", index = 6)
    private String version;

    @Schema(description = "整合包地址")
    @ExcelProperty(value = "整合包地址", index = 7)
    private String filePath;

    @Schema(description = "整合包大小")
    @ExcelProperty(value = "整合包大小", index = 8)
    private Long fileSize;

    @Schema(description = "MD5")
    @ExcelProperty(value = "MD5", index = 9)
    private String md5;

    @Schema(description = "状态")
    @ExcelProperty(value = "状态", index = 10)
    private Integer status = ModpackStatus.WAITING.getCode();

    @Schema(description = "原因")
    @ExcelProperty(value = "原因", index = 11)
    private String reason;

    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间", index = 12)
    @Column(onInsertValue = "now()")
    private Date createTime;

    @Schema(description = "更新时间")
    @ExcelProperty(value = "更新时间", index = 13)
    private Date updateTime;

    @Schema(description = "Logo的md5")
    @ExcelProperty(value = "Logo的md5", index = 14)
    private String logoMd5;
}