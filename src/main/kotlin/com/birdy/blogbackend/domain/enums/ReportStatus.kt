package com.birdy.blogbackend.domain.enums

/**
 * Represents the audit status of a modpack
 */
enum class ReportStatus(val code: Int, val description: String) {
  WAITING(0, "等待审核"),
  AUDITING(1, "审核中"),
  PASSED(2, "审核通过"),
  FAILED(3, "审核不通过");

  companion object {
    /**
     * Get ModpackStatus by code
     */
    @JvmStatic
    fun fromCode(code: Int): ReportStatus? = entries.find { it.code == code }
  }

  /**
   * Check if the review process is complete
   */
  fun isReviewComplete(): Boolean = this == PASSED || this == FAILED

  /**
   * Check if the modpack is approved
   */
  fun isApproved(): Boolean = this == PASSED
}
