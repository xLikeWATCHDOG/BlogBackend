package com.birdy.blogbackend.domain.enums;

import lombok.Getter;

/**
 * @author birdy
 */
@Getter
public enum Group {
  /**
   * 默认
   */
  DEFAULT(0, 0, 0),
  ADMIN(100000, 0, 0);

  /**
   * 数字越大越优先
   */
  final int priority;
  /**
   * 多少钱/月(单位:分CNY)
   */
  final int price;

  /**
   * 优惠百分比
   */
  final float discount;

  Group(int priority, int price, float discount) {
    this.priority = priority;
    this.price = price;
    this.discount = discount;
  }
}
