package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.ArticleCommentDao;
import com.birdy.blogbackend.domain.entity.ArticleComment;
import com.birdy.blogbackend.service.ArticleCommentService;
import com.mybatisflex.core.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author birdy
 */
@Service
@Slf4j
public class ArticleCommentServiceImpl implements ArticleCommentService {
    @Autowired
    private ArticleCommentDao articleCommentDao;

    @Override
    public BaseMapper<ArticleComment> getMapper() {
        return articleCommentDao.getMapper();
    }
}
