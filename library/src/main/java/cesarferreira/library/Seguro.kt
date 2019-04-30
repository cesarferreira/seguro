package cesarferreira.library

import android.Manifest
import android.app.Application
import android.content.Context
import androidx.annotation.RequiresPermission
import java.io.File

class Seguro {

    fun clear() {
    }

    fun getString(key: String): String? {

    }

    // EDITOR
    class Editor {

        fun put(key: String, value: String): Editor {
            return this
        }

        fun apply() {}


//        fun put(key: String, value: Int) {
//            put(key, Integer.toString(value))
//        }
//
//        fun put(key: String, value: Long) {
//            put(key, java.lang.Long.toString(value))
//        }
//
//        fun put(key: String, value: Double) {
//            put(key, java.lang.Double.toString(value))
//        }
//
//        fun put(key: String, value: Float) {
//            put(key, java.lang.Float.toString(value))
//        }
//
//        fun put(key: String, value: Boolean) {
//            put(key, java.lang.Boolean.toString(value))
//        }
//
//        fun put(key: String, value: List<*>) {
//            put(key, value.toString())
//        }
//
//        fun put(key: String, bytes: ByteArray) {
//            put(key, String(bytes))
//        }

        @RequiresPermission(
            allOf = [Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE]
        )
        fun put(key: String, file: File?, deleteOldFile: Boolean): File? {
//            TODO()
        }

    }


    // BUILDER
    class Builder(context: Context) {

        fun enableCrypto(encryptKey: Boolean, encryptValue: Boolean): Builder {
            return this
        }

        fun setPassword(password: String): Builder {
            return this
        }

        fun setFolderName(folderName: String): Builder {
            return this
        }

        fun build(): Seguro {
            // TODO BUILD stuff
            return Seguro()
        }
    }


    companion object {

        @JvmStatic
        fun init(application: Application) {
        }
    }
}