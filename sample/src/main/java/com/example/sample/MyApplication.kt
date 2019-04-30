package com.example.sample

import android.app.Application
import cesarferreira.library.Seguro

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Seguro.init(this)
    }
}