package com.birdy.blogbackend.event

import com.birdy.blogbackend.domain.entity.Photo
import jakarta.servlet.http.HttpServletRequest
import lombok.Getter
import lombok.Setter
import lombok.extern.slf4j.Slf4j
import org.springframework.context.ApplicationEvent

@Getter
@Slf4j
class PhotoAddEvent(
    source: Any,
    @field:Setter private val photo: Photo,
    private val request: HttpServletRequest
) :
    ApplicationEvent(source), Cancellable {
    override var isCancelled: Boolean = false
}
