package com.birdy.blogbackend.constant

object ExcelConstant {
  //一个sheet装100w数据
  const val PER_SHEET_ROW_COUNT: Int = 1000000

  //每次查询20w数据，每次写入20w数据
  const val PER_WRITE_ROW_COUNT: Int = 200000
}
