package cesarferreira.library.managers

import android.os.Environment
import java.io.File

class FileManager(private val mainDirectoryName: String, password: String) {

    init {
        makeFileDirectory()
    }

    fun write(key: String, value: String): Boolean {
        return true
    }

    fun readFromFile(key: String): String? {
        return null
    }

    private fun makeDirectory(): String {

        val fullDirectory = "$mainDirectoryName/"

        val dir = File(Environment.getExternalStorageDirectory(), mainDirectoryName)
        if (!dir.exists() || !dir.isDirectory) dir.mkdirs()

        return fullDirectory
    }

    private fun makeFileDirectory(): String {
        return makeDirectory() //+ DEFAULT_FILES_FOLDER
    }
}