package com.birdy.blogbackend.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.birdy.blogbackend.domain.entity.Permission;
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

  @Schema(description = "用户组")
  @ExcelProperty(value = "用户组", index = 10)
  private Permission group;

  private Stats stats;

  @Data
  public static class Stats {
    private Long articleCount = 0L;
    private Long modpackCount = 0L;
    private Long followersCount = 0L;
    private Long followingCount = 0L;
  }
}
