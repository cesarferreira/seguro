package cesarferreira.seguro.library.persistance

import android.os.Environment
import java.io.File
import java.io.InputStream

open class SdCardPersistence(private val folder: String) : PersistenceManager {

    private val baseDir = File(Environment.getExternalStorageDirectory(), folder)

    @Synchronized
    override fun write(key: String, value: String): Boolean {
        createDirectoryIfDoesntExist()
        val targetFile = getFileByKey(key)

        targetFile.writeText(value)

        return targetFile.exists()
    }

    @Synchronized
    override fun read(key: String): String? {

        val file = getFileByKey(key)

        if (!file.exists()) return null

        return try {
            val inputStream: InputStream = file.inputStream()
            inputStream.bufferedReader().use { it.readText() }
        } catch (exp: Exception) {
            exp.printStackTrace()
            null
        }
    }

    private fun getFileByKey(key: String): File = File("$baseDir/$key")

    override fun wipe(): Boolean {
        deleteRecursive(baseDir)
        return !baseDir.exists()
    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles().forEach { deleteRecursive(it) }
        }

        fileOrDirectory.delete()
    }

    private fun createDirectoryIfDoesntExist(): String {
        val fullDirectory = "$folder/"

        if (!baseDir.exists() || !baseDir.isDirectory) baseDir.mkdirs()

        return fullDirectory
    }
}
