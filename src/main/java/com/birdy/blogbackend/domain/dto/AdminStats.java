package com.birdy.blogbackend.domain.dto;

import com.mybatisflex.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminStats implements Serializable {
    @Serial
    @Column(ignore = true)
    private static final long serialVersionUID = 1L;

    private Long userCount;
    private Long articleCount;
    private Long modpackCount;
    private Long commentCount;
    private Long viewCount;
    private Long newUserCount;
    private Long newArticleCount;
    private Long newModpackCount;
    private Long pendingModpackCount;
    private Long reported;
}
