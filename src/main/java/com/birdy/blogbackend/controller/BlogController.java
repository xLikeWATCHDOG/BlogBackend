package com.birdy.blogbackend.controller;

import com.birdy.blogbackend.config.ConfigProperties;
import com.birdy.blogbackend.domain.ResultUtil;
import com.birdy.blogbackend.domain.dto.ArticleCommentVO;
import com.birdy.blogbackend.domain.dto.ArticleVO;
import com.birdy.blogbackend.domain.entity.Article;
import com.birdy.blogbackend.domain.entity.ArticleComment;
import com.birdy.blogbackend.domain.entity.Photo;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.vo.request.article.ArticleAddRequest;
import com.birdy.blogbackend.domain.vo.request.article.ArticleCommentQueryRequest;
import com.birdy.blogbackend.domain.vo.request.article.ArticleQueryRequest;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.ArticleCommentService;
import com.birdy.blogbackend.service.ArticleService;
import com.birdy.blogbackend.service.PhotoService;
import com.birdy.blogbackend.service.UserService;
import com.birdy.blogbackend.util.gson.GsonProvider;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.birdy.blogbackend.constant.UserConstant.LOGIN_TOKEN;

/**
 * @author birdy
 */
@RestController
@RequestMapping("/blog")
@Slf4j
public class BlogController {
    @Autowired
    private ConfigProperties configProperties;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private ArticleCommentService articleCommentService;

    @PostMapping("")
    public ResponseEntity<BaseResponse<Long>> addArticle(ArticleAddRequest articleAddRequest,
                                                         @RequestBody MultipartFile image,
                                                         HttpServletRequest request) {
        String[] tags = articleAddRequest.getTags();
        String title = articleAddRequest.getTitle();
        String summary = articleAddRequest.getSummary();
        String content = articleAddRequest.getContent();
        List<String> tagList = Arrays.asList(tags);
        String token = request.getHeader(LOGIN_TOKEN);
        User user = userService.getUserByToken(token, request);
        Article article = new Article();
        article.setTitle(title);
        article.setDescription(summary);
        article.setUid(user.getUid());
        article.setContent(content);
        article.setTags(GsonProvider.normal().toJson(tagList));
        try {
            // 获取图片类型拓展名
            String ext = Objects.requireNonNull(image.getOriginalFilename()).substring(image.getOriginalFilename().lastIndexOf("."));
            byte[] data = image.getBytes();
            String md5 = DigestUtils.md5DigestAsHex(data);
            String fileName = md5 + ext;
            Photo photo = photoService.savePhotoByMd5(md5, ext, data.length, request);
            article.setPid(photo.getPid());
            Path path = Paths.get("photos", fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ReturnCode.SYSTEM_ERROR, "Failed to save image", request);
        }
        articleService.save(article);
        return ResultUtil.ok(article.getId());
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<Page<ArticleVO>>> page(ArticleQueryRequest articleQueryRequest, HttpServletRequest request) {

        // 创建 QueryWrapper 并添加排序条件
        // 根据 ID 倒序
        QueryWrapper queryWrapper = QueryWrapper.create().orderBy("id", false);

        // 执行分页查询
        return getBaseResponseResponseEntity(articleQueryRequest, queryWrapper);
    }

    @GetMapping("/rank")
    public ResponseEntity<BaseResponse<Page<ArticleVO>>> rank(ArticleQueryRequest articleQueryRequest, HttpServletRequest request) {
        // 全部文章按浏览量排序
        QueryWrapper queryWrapper = QueryWrapper.create().orderBy("views", false).orderBy("id", false);
        return getBaseResponseResponseEntity(articleQueryRequest, queryWrapper);
    }

    @NotNull
    private ResponseEntity<BaseResponse<Page<ArticleVO>>> getBaseResponseResponseEntity(ArticleQueryRequest articleQueryRequest, QueryWrapper queryWrapper) {
        Page<Article> page = articleService.getMapper()
                .paginate(Page.of(articleQueryRequest.getCurrent(), articleQueryRequest.getPageSize()), queryWrapper);
        Page<ArticleVO> voPage = page.map(i -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(i, vo);
            List tags = GsonProvider.normal().fromJson(i.getTags(), List.class);
            vo.setTags(tags);
            Photo photo = photoService.getById(i.getPid());
            vo.setImage(photo.getMd5());
            User user = userService.getById(i.getUid());
            vo.setAuthor(user.getUsername());
            return vo;
        });
        return ResponseEntity.ok(BaseResponse.success(voPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ArticleVO>> getArticle(@PathVariable("id") Long id, HttpServletRequest request) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "Article not found", request);
        }
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);
        List tags = GsonProvider.normal().fromJson(article.getTags(), List.class);
        vo.setTags(tags);
        Photo photo = photoService.getById(article.getPid());
        vo.setImage(photo.getMd5());
        User user = userService.getById(article.getUid());
        vo.setAuthor(user.getUsername());
        // 更新浏览量
        article.setViews(article.getViews() + 1);
        articleService.updateById(article);
        return ResponseEntity.ok(BaseResponse.success(vo));
    }

    @GetMapping("/comment")
    public ResponseEntity<BaseResponse<Page<ArticleCommentVO>>> getComments(ArticleCommentQueryRequest articleCommentQueryRequest, HttpServletRequest request) {
        long id = articleCommentQueryRequest.getId();
        long current = articleCommentQueryRequest.getCurrent();
        long pageSize = articleCommentQueryRequest.getPageSize();
        QueryWrapper queryWrapper = QueryWrapper.create().eq("aid", id).orderBy("id", false);
        Page<ArticleComment> page = articleCommentService.getMapper().paginate(Page.of(current, pageSize), queryWrapper);
        Page<ArticleCommentVO> voPage = page.map(i -> {
            ArticleCommentVO vo = new ArticleCommentVO();
            BeanUtils.copyProperties(i, vo);
            User user = userService.getById(i.getUid());
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
            return vo;
        });
        return ResponseEntity.ok(BaseResponse.success(voPage));
    }

    @PostMapping("/comment")
    public ResponseEntity<BaseResponse<Long>> addComment(@RequestBody ArticleComment articleComment, HttpServletRequest request) {
        String token = request.getHeader(LOGIN_TOKEN);
        User user = userService.getUserByToken(token, request);
        if (user.getUid() == null) {
            throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "User not found", request);
        }
        if (!user.getUid().equals(articleComment.getUid())) {
            throw new BusinessException(ReturnCode.PARAMS_ERROR, "User not match", request);
        }
        articleCommentService.save(articleComment);
        return ResultUtil.ok(articleComment.getId());
    }
}
