package com.birdy.blogbackend.util

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

/**
 * @author birdy
 */
object CaffeineFactory {
    /**
     * Our own fork joins pool for FloraCore cache operations.
     *
     *
     * By default, Caffeine uses the ForkJoinPool.commonPool instance.
     * However... ForkJoinPool is a fixed size pool limited by Runtime.availableProcessors.
     * Some (bad) plugins incorrectly use this pool for i/o operations, make calls to Thread.sleep
     * or otherwise block waiting for something else to complete. This prevents the FC cache loading
     * operations from running.
     *
     *
     * By using our own pool, we ensure this will never happen.
     */
    private val loaderPool = ForkJoinPool()

    fun newBuilder(): Caffeine<Any, Any> {
        return Caffeine.newBuilder().executor(loaderPool)
    }

    fun executor(): Executor {
        return loaderPool
    }
}
