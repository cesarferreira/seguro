package cesarferreira.seguro.library.persistance

import android.os.Environment
import java.io.File

open class SdCardFileManagerImpl(private val mainDirectoryName: String) :
    FileManager {

    @Synchronized
    override fun write(key: String, value: String): Boolean {
        createDirectoryIfDoesntExist()

        return true
    }

    @Synchronized
    override fun read(key: String): String? {
        return null
    }

    override fun wipe() = true

    private fun createDirectoryIfDoesntExist(): String {

        val fullDirectory = "$mainDirectoryName/"

        val dir = File(Environment.getExternalStorageDirectory(), mainDirectoryName)
        if (!dir.exists() || !dir.isDirectory) dir.mkdirs()

        return fullDirectory
    }

}