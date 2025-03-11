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
@Table("permission")
@Data
public class Permission implements Serializable {
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

    @Schema(description = "权限")
    @ExcelProperty(value = "权限", index = 2)
    private String permission;

    @Schema(description = "过期时间")
    @ExcelProperty(value = "过期时间", index = 3)
    private Long expiry;

    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间", index = 4)
    private Date createTime;

    @Schema(description = "更新时间")
    @ExcelProperty(value = "更新时间", index = 5)
    @Column(onUpdateValue = "now()")
    private Date updateTime;

    @Schema(description = "可用性")
    @ExcelProperty(value = "可用性", index = 6)
    @Column(isLogicDelete = true)
    private Boolean available;
}
