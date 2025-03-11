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
@Table("photo")
@Data
public class Photo implements Serializable {
    @Serial
    @Column(ignore = true)
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    @ExcelProperty(value = "PID", index = 0)
    private Long pid;

    @Schema(description = "MD5")
    @ExcelProperty(value = "MD5", index = 1)
    private String md5;

    @Schema(description = "后缀")
    @ExcelProperty(value = "后缀", index = 2)
    private String ext;

    @Schema(description = "大小")
    @ExcelProperty(value = "大小", index = 3)
    private Long size;

    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间", index = 4)
    private Date createTime;

    @Schema(description = "更新时间")
    @ExcelProperty(value = "更新时间", index = 5)
    private Date updateTime;

    @Schema(description = "可用性")
    @ExcelProperty(value = "可用性", index = 6)
    @Column(isLogicDelete = true)
    private Boolean available;
}
