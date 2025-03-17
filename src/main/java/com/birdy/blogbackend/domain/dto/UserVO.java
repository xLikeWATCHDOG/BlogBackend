package com.birdy.blogbackend.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author birdy
 */
@Data
public class UserVO implements Serializable {
    @Serial
    @Column(ignore = true)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    @ExcelProperty(value = "UD", index = 0)
    private Long uid;

    @Schema(description = "用户名")
    @ExcelProperty(value = "用户名", index = 1)
    private String username;

    @Schema(description = "邮箱")
    @ExcelProperty(value = "邮箱", index = 3)
    private String email;

    @Schema(description = "性别")
    @ExcelProperty(value = "性别", index = 4)
    private Integer gender;

    @Schema(description = "头像地址")
    @ExcelProperty(value = "头像地址", index = 5)
    private String avatar;

    @Schema(description = "状态")
    @ExcelProperty(value = "状态", index = 6)
    private Integer status;

    @Schema(description = "token")
    @ExcelProperty(value = "token", index = 7)
    private String token;

    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间", index = 8)
    private Date createTime;

    @Schema(description = "更新时间")
    @ExcelProperty(value = "更新时间", index = 9)
    private Date updateTime;
}
