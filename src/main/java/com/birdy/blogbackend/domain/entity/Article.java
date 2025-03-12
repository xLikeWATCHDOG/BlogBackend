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
@Table("article")
@Data
public class Article implements Serializable {
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

    @Schema(description = "用户ID")
    @ExcelProperty(value = "用户ID", index = 1)
    private Long uid;

    @Schema(description = "照片ID")
    @ExcelProperty(value = "照片ID", index = 2)
    private Long pid;

    @Schema(description = "标题")
    @ExcelProperty(value = "标题", index = 3)
    private String title;

    @Schema(description = "描述")
    @ExcelProperty(value = "描述", index = 4)
    private String description;

    @Schema(description = "内容")
    @ExcelProperty(value = "内容", index = 5)
    private String content;

    @Schema(description = "标签")
    @ExcelProperty(value = "标签", index = 6)
    private String tags;

    @Schema(description = "浏览量")
    @ExcelProperty(value = "浏览量", index = 7)
    private Long views;

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
    private Integer available;
}
