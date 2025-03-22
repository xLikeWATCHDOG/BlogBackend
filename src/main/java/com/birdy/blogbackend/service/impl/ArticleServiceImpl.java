package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.ArticleDao;
import com.birdy.blogbackend.domain.entity.Article;
import com.birdy.blogbackend.service.ArticleService;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author birdy
 */
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleDao articleDao;

    @Override
    public BaseMapper<Article> getMapper() {
        return articleDao.getMapper();
    }

    @Override
    public long countToday() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("to_char(create_time, 'YYYY-MM-DD')", "to_char(CURRENT_DATE, 'YYYY-MM-DD')");
        return articleDao.getMapper().selectCountByQuery(queryWrapper);
    }
}
