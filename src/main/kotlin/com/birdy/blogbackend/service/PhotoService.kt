package com.birdy.blogbackend.service

import com.birdy.blogbackend.domain.entity.Photo
import com.mybatisflex.core.service.IService
import jakarta.servlet.http.HttpServletRequest
import java.nio.file.Path

interface PhotoService : IService<Photo?> {
  fun getPhotoPathByMd5(md5: String, request: HttpServletRequest): Path
  fun savePhotoByMd5(md5: String, ext: String, size: Long, request: HttpServletRequest): Photo
}
