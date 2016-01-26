package com.mcxiaoke.next.kotlin

/**
 * User: mcxiaoke
 * Date: 16/1/26
 * Time: 16:45
 */


fun main(args: Array<String>) {
    val ca = callable { 100 }
    val r = ca.call()
    println("$r: ${r.javaClass}")

    var pool = Android.newCachedThreadPool("demo")
    pool.execute { println("{${Thread.currentThread().name}}") }

}