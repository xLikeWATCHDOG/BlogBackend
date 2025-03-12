package com.birdy.blogbackend.domain.enums

import lombok.Getter

/**
 * @author birdy
 */
@Getter
enum class UserStatus(val code: Int, val desc: String) {
    /**
     * 正常
     */
    NORMAL(0, "正常"),

    /**
     * 禁用
     */
    BANNED(1, "禁用"),

    /**
     * 删除
     */
    DELETED(2, "删除");

    companion object {
        fun valueOf(value: Int): UserStatus {
            for (status in entries) {
                if (status.code == value) {
                    return status
                }
            }
            return BANNED
        }
    }
}
