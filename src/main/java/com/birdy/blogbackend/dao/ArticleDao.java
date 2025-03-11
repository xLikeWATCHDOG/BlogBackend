package com.birdy.blogbackend.dao;

import com.birdy.blogbackend.domain.entity.Article;
import com.birdy.blogbackend.mapper.ArticleMapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * @author birdy
 */
@Repository
public class ArticleDao extends ServiceImpl<ArticleMapper, Article> implements IService<Article> {
}
