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
import java.util.List;

/**
 * @author birdy
 */
@Data
public class ArticleVO implements Serializable {
    @Serial
    @Column(ignore = true)
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    @ExcelProperty(value = "ID", index = 0)
    private Long id;

    @Schema(description = "标题")
    @ExcelProperty(value = "标题", index = 1)
    private String title;

    @Schema(description = "描述")
    @ExcelProperty(value = "描述", index = 2)
    private String description;

    @Schema(description = "内容")
    @ExcelProperty(value = "内容", index = 3)
    private String content;

    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间", index = 4)
    private Date createTime;

    @Schema(description = "标签")
    @ExcelProperty(value = "标签", index = 5)
    private List<String> tags;

    @Schema(description = "浏览量")
    @ExcelProperty(value = "浏览量", index = 6)
    private Long views;

    @Schema(description = "图片地址")
    @ExcelProperty(value = "图片地址", index = 7)
    private String image;

    @Schema(description = "作者")
    @ExcelProperty(value = "作者", index = 8)
    private String author;
}
