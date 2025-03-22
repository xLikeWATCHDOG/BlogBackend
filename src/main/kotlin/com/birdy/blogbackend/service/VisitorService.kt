package com.birdy.blogbackend.service

import com.birdy.blogbackend.domain.entity.Visitor
import com.mybatisflex.core.service.IService

interface VisitorService : IService<Visitor?> {
    val today: Long

    fun addOne()
}