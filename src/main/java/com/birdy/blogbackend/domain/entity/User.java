package com.birdy.blogbackend.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.birdy.blogbackend.domain.enums.UserGender;
import com.birdy.blogbackend.domain.enums.UserStatus;
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
@Table("user")
@Data
public class User implements Serializable {
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

    @Schema(description = "密码")
    @ExcelProperty(value = "密码", index = 2)
    private String password;

    @Schema(description = "邮箱")
    @ExcelProperty(value = "邮箱", index = 3)
    private String email;

    @Schema(description = "手机号")
    @ExcelProperty(value = "手机号", index = 4)
    private String phone;

    @Schema(description = "性别")
    @ExcelProperty(value = "性别", index = 5)
    private Integer gender;

    @Schema(description = "头像地址")
    @ExcelProperty(value = "头像地址", index = 6)
    private String avatar;

    @Schema(description = "状态")
    @ExcelProperty(value = "状态", index = 7)
    private Integer status;

    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间", index = 8)
    private Date createTime;

    @Schema(description = "更新时间")
    @ExcelProperty(value = "更新时间", index = 9)
    private Date updateTime;

    @Schema(description = "可用性")
    @ExcelProperty(value = "可用性", index = 10)
    @Column(isLogicDelete = true)
    private Integer available;

    public UserGender getUserGender() {
        return UserGender.valueOf(gender);
    }

    public void setUserGender(UserGender userGender) {
        this.gender = userGender.getCode();
    }

    public UserStatus getUserStatus() {
        return UserStatus.valueOf(status);
    }

    public void setUserStatus(UserStatus userStatus) {
        this.status = userStatus.getCode();
    }
}
