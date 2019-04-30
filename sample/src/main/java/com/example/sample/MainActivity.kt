package com.example.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cesarferreira.library.Seguro
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val seguro by lazy {
        Seguro.Builder(this)
            .enableCrypto(false, true)
            .setPassword("Password@123")
            .setFolderName("CESAR_FILES_BITCH")
            .build()
    }

    @SuppressLint("MissingPermission") // TODO PAY ATTENTION TO ME
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        seguro.clear()

        // READ
        readButton.setOnClickListener {
            textView.text = seguro.getString(TIME_KEY) ?: "cant find shit"
        }

        // WRITE
        writeButton.setOnClickListener {
            Seguro.Editor()
                .put(TIME_KEY, Date().toString())
                .apply()

            textView.text = "I WROTE STUFF"
        }

//        Log.d("seguro", "" + seguro.total)
    }

    companion object {
        val DEFAULT_DIRECTORY = "123"
        internal var TIME_KEY = "KEY_TIME"

    }
}




