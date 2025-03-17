package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.ArticleComment;
import com.birdy.blogbackend.mapper.ArticleCommentMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class ArticleCommentDao extends ServiceImpl<ArticleCommentMapper, ArticleComment> implements IService<ArticleComment> {
}
