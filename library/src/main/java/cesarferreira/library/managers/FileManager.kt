package cesarferreira.library.managers

import android.os.Environment
import java.io.File

open class FileManager(private val mainDirectoryName: String) {

    init {
        makeFileDirectory()
    }

    fun persist(key: String, value: String): Boolean {
        return true
    }

    open fun readFromFile(key: String): String? {
        return null
    }

    fun wipeData() {

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