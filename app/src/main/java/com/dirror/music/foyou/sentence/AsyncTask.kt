package com.dirror.foyou.sentence

import java.util.concurrent.Executors

// 线程池
private val pool by lazy {
    Executors.newCachedThreadPool()
}

// 异步
class AsyncTask(private val block: () -> Unit) {
    fun execute() = pool.execute(block)
}