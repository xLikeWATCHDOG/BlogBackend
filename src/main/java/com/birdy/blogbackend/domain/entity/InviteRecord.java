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
@Table("invite_code")
@Data
public class InviteRecord implements Serializable {
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

    @Schema(description = "邀请码")
    @ExcelProperty(value = "邀请码", index = 2)
    private String code;

    @Schema(description = "过期时间")
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
}
