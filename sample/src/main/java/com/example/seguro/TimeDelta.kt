package com.example.seguro

import java.util.*

class TimeDelta(private val startTime: Date = Date()) {

    private lateinit var endTime: Date

    fun finish(endTime: Date = Date()) {
        this.endTime = endTime
    }

    /**
     * Diference
     */
    var delta: Long = 0
        get() = endTime.time - startTime.time

    var deltaInSeconds: Long = 0
        get() = delta / 1000
}