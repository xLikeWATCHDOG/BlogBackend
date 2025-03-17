package com.birdy.blogbackend.domain.vo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class ArticleAddRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String title;

    private String summary;

    private String content;

    private String[] tags;
}