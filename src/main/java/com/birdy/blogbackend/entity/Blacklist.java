package com.birdy.blogbackend.entity;

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
@Table("blacklist")
@Data
public class Blacklist implements Serializable {
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

    @Schema(description = "IP")
    @ExcelProperty(value = "IP", index = 1)
    private String ip;

    @Schema(description = "关联的请求")
    @ExcelProperty(value = "关联的请求", index = 2)
    private Long log;

    @Schema(description = "原因")
    @ExcelProperty(value = "原因", index = 3)
    private String reason;

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
