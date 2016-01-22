package com.mcxiaoke.next.kotlin.ext

import java.util.concurrent.*

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 15:35
 */


class CounterThreadFactory(name: String?) : ThreadFactory {
    private var count: Int = 0
    private val name: String

    init {
        this.name = name ?: "Android"

    }

    override fun newThread(r: Runnable): Thread {
        val thread = Thread(r)
        thread.name = name + "-thread #" + count++
        return thread
    }
}
