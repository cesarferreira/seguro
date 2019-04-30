package com.example.sample

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cesarferreira.library.Seguro
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener



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

        checkPermissions()

//        seguro.clear()

        // READ
        readButton.setOnClickListener {
            textView.text = seguro.getString(TIME_KEY) ?: "cant find shit"
        }

        // WRITE
        writeButton.setOnClickListener {
            seguro.Editor()
                .put(TIME_KEY, Date().toString())
                .put("name", "cesar ferreira")
                .apply()

            textView.text = "I WROTE STUFF"
        }

//        Log.d("seguro", "" + seguro.total)
    }

    private fun checkPermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {/* ... */
                }
            }).check()
    }

    companion object {
        internal var TIME_KEY = "KEY_TIME"

    }
}




