package com.birdy.blogbackend.controller;

import com.birdy.blogbackend.annotation.AuthCheck;
import com.birdy.blogbackend.domain.ResultUtil;
import com.birdy.blogbackend.domain.dto.AdminStats;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author birdy
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ModpackService modpackService;
    @Autowired
    private VisitorService visitorService;
    @Autowired
    private ArticleCommentService articleCommentService;
    @Autowired
    private ReportService reportService;

    @AuthCheck(must = "group.admin")
    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<AdminStats>> getStats() {
        AdminStats stats = new AdminStats();
        stats.setUserCount(userService.count());
        stats.setArticleCount(articleService.count());
        stats.setModpackCount(modpackService.count());
        stats.setCommentCount(modpackService.count());
        stats.setViewCount(visitorService.getToday());
        stats.setCommentCount(articleCommentService.count());

        // 今日数据
        long newUserCount = userService.countToday();
        long newArticleCount = articleService.countToday();
        long newModpackCount = modpackService.countToday();

        stats.setNewUserCount(newUserCount);
        stats.setNewArticleCount(newArticleCount);
        stats.setNewModpackCount(newModpackCount);

        long pendingModpackCount = modpackService.countPending();
        stats.setPendingModpackCount(pendingModpackCount);

        long reported = reportService.count();
        stats.setReported(reported);

        return ResultUtil.ok(stats);
    }
}
