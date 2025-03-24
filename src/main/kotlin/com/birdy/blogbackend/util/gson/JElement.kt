package com.birdy.blogbackend.util.gson

import com.google.gson.JsonElement

/**
 * Stupidly simple fluent gson wrappers
 */
interface JElement {
  fun toJson(): JsonElement?
}
