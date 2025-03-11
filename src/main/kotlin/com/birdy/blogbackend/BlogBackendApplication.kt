package com.birdy.blogbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
class BlogBackendApplication

fun main(args: Array<String>) {
    runApplication<BlogBackendApplication>(*args)
}
