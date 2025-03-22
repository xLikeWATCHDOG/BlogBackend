package com.birdy.blogbackend.domain.enums;

import lombok.Getter;

/**
 * @author birdy
 */
@Getter
public enum ReportType {
    /**
     * 评论
     */
    COMMENT(0, "评论"),
    /**
     * 文章
     */
    ARTICLE(1, "文章");

    final int code;
    final String desc;

    ReportType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReportType valueOf(int value) {
        for (ReportType status : ReportType.values()) {
            if (status.code == value) {
                return status;
            }
        }
        return ARTICLE;
    }
}
