package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.dao.PhotoDao;
import com.birdy.blogbackend.domain.entity.Photo;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.PhotoService;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * @author birdy
 */
@Service
@Slf4j
public class PhotoServiceImpl implements PhotoService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PhotoDao photoDao;

    @Override
    public BaseMapper<Photo> getMapper() {
        return photoDao.getMapper();
    }

    @Override
    public @NotNull Photo savePhotoByMd5(@NotNull String md5, @NotNull String ext, long size, @NotNull HttpServletRequest request) {
        // 查询md5是否已经记录
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("md5", md5);
        Photo photo = this.getOne(queryWrapper);
        if (photo != null) {
            return photo;
        }
        // 保存
        photo = new Photo();
        photo.setMd5(md5);
        // 处理ext,若第一个字符为.则去掉
        if (ext.startsWith(".")) {
            ext = ext.substring(1);
        }
        photo.setExt(ext);
        photo.setSize(size);
        this.save(photo);
        return photo;
    }

    @Override
    public @NotNull Path getPhotoPathByMd5(@NotNull String md5, @NotNull HttpServletRequest request) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("md5", md5);
        Photo photo = this.getOne(queryWrapper);
        if (photo == null) {
            throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "photo not found", request);
        }
        return Path.of("photos", photo.getMd5() + "." + photo.getExt());
    }
}
