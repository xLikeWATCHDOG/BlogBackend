package com.birdy.blogbackend.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
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
public class ArticleCommentVO implements Serializable {
    @Serial
    @Column(ignore = true)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    @ExcelProperty(value = "ID", index = 0)
    public Long id;

    @Schema(description = "用户ID")
    @ExcelProperty(value = "用户ID", index = 1)
    private Long uid;

    @Schema(description = "文章ID")
    @ExcelProperty(value = "文章ID", index = 2)
    private Long aid;

    @Schema(description = "内容")
    @ExcelProperty(value = "内容", index = 3)
    private String content;

    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间", index = 4)
    private Date createTime;

    @Schema(description = "更新时间")
    @ExcelProperty(value = "更新时间", index = 5)
    private Date updateTime;

    @Schema(description = "用户名")
    @ExcelProperty(value = "用户名", index = 6)
    private String username;

    @Schema(description = "头像")
    @ExcelProperty(value = "头像", index = 7)
    private String avatar;
}
