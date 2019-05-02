package com.example.seguro

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cesarferreira.seguro.library.Seguro
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var seguro: Seguro

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        seguro = Seguro.Builder()
            .enableEncryption(encryptKey = true, encryptValue = true)
            .setEncryptionPassword("*QdfKPoRE[gC*vtqVxZ2Eg]ZM7TeWnHyYTHU}DuEocJd6QxuZ9WJ")
            .setPersistenceType(Seguro.PersistenceType.SDCard(".${BuildConfig.APPLICATION_ID}"))
            .enableLogging(BuildConfig.DEBUG)
//            .setPersistenceType(Seguro.PersistenceType.InMemory)
//            .setPersistenceType(Seguro.PersistenceType.SharedPreferences(applicationContext))
            .build()

        // READ
        readButton.setOnClickListener {

            val timeDelta = TimeDelta()
            val result = seguro.getString(TIME_KEY) ?: "cant find the TIME"
            timeDelta.finish()

            Log.d("TIME", "READ: " + timeDelta.delta.toString() + " ms")

            textView.text = ""
            textView.text = result
        }

        wipeButton.setOnClickListener {
            textView.text = "all data wiped"
            seguro.clear()
        }

        // WRITE
        writeButton.setOnClickListener {

            val timeDelta = TimeDelta()

            seguro.Editor()
                .put(TIME_KEY, Date().time)
                .put(NAME_KEY, "Cesar Ferreira")
                .commit()

            timeDelta.finish()

            Log.d("TIME", "WRITE: " + timeDelta.delta.toString() + " ms")

            textView.text = "I WROTE TO PERSISTENCE"
        }
    }

    private fun checkPermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {}
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                }
            }).check()
    }

    companion object {
        internal var TIME_KEY = "KEY_TIME"
        internal var NAME_KEY = "KEY_NAME"
    }
}




