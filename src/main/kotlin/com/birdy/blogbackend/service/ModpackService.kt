package com.birdy.blogbackend.service

import com.birdy.blogbackend.domain.entity.Modpack
import com.mybatisflex.core.service.IService

interface ModpackService : IService<Modpack?> {
    fun countToday(): Long
    fun countPending(): Long
}