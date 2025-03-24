package com.birdy.blogbackend.controller;

import com.birdy.blogbackend.annotation.AuthCheck;
import com.birdy.blogbackend.domain.ResultUtil;
import com.birdy.blogbackend.domain.entity.ArticleComment;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.service.ArticleCommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author birdy
 */
@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {
  @Autowired
  private ArticleCommentService articleCommentService;

  @AuthCheck(must = "group.admin")
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<ArticleComment>> getComment(@PathVariable Integer id,
                                                                 HttpServletRequest request) {
    ArticleComment stats = articleCommentService.getById(id);
    return ResultUtil.ok(stats);
  }

  @AuthCheck(must = "group.admin")
  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponse<Boolean>> deleteComment(@PathVariable Integer id,
                                                             HttpServletRequest request) {
    articleCommentService.removeById(id);
    return ResultUtil.ok(true);
  }
}
