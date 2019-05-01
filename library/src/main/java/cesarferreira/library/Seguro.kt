package cesarferreira.library

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Application
import androidx.annotation.RequiresPermission
import cesarferreira.library.managers.AESEncryptionManager
import cesarferreira.library.managers.FileManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class Seguro private constructor(
    private val config: Config,
    private val fileManager: FileManager,
    private val encryptionManager: AESEncryptionManager
) {

    fun clear() {
        fileManager.wipeData()
    }

    fun getString(key: String): String? {
        val fromFile = fileManager.readFromFile(hashKey(key))
        return decryptValue(fromFile!!)
    }

    // EDITOR
    inner class Editor {

        private var pendingWrites = HashMap<String, String>()

        fun put(key: String, value: String): Editor {
            pendingWrites[hashKey(key)] = encryptValue(value)
            return this
        }

        fun put(key: String, value: Int) {
            put(key, Integer.toString(value))
        }

        fun put(key: String, value: Long) {
            put(key, java.lang.Long.toString(value))
        }

        fun put(key: String, value: Double) {
            put(key, java.lang.Double.toString(value))
        }

        fun put(key: String, value: Float) {
            put(key, java.lang.Float.toString(value))
        }

        fun put(key: String, value: Boolean) {
            put(key, java.lang.Boolean.toString(value))
        }

        fun put(key: String, value: List<*>) {
            put(key, value.toString())
        }

        fun put(key: String, bytes: ByteArray) {
            put(key, String(bytes))
        }

        @RequiresPermission(allOf = [READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
        fun apply() {
            // persist
            pendingWrites.forEach {
                println("key: ${it.key}, value: ${it.value}")
                fileManager.persist(it.key, it.value)
            }

            // wipe pending writes
            pendingWrites = hashMapOf()
        }

    }

    internal data class Config(
        var encryptKey: Boolean = false,
        var encryptValue: Boolean = false,
        var password: String = "",
        var folderName: String = ""
    )

    // BUILDER
    class Builder {

        private val config = Config()

        fun enableCrypto(encryptKey: Boolean, encryptValue: Boolean): Builder {
            config.encryptKey = encryptKey
            config.encryptValue = encryptValue
            return this
        }

        fun setPassword(password: String): Builder {
            config.password = password
            return this
        }

        fun setFolderName(folderName: String): Builder {
            config.folderName = folderName
            return this
        }

        fun build(): Seguro {
            val encryptionManager = AESEncryptionManager()
            val fileManager = FileManager(config.folderName)

            return Seguro(config, fileManager, encryptionManager)
        }
    }


    private fun encryptValue(value: String): String {
        return if (config.encryptValue) {
            encryptionManager.encrypt(config.password, value)
        } else {
            value
        }
    }

    private fun decryptValue(value: String): String {
        return if (config.encryptValue) {
            encryptionManager.decrypt(config.password, value)
        } else {
            value
        }
    }

    private fun hashKey(key: String): String {
        if (config.encryptKey) {
            try {
                val HEX_CHARS = "0123456789ABCDEF"
                val md = MessageDigest.getInstance("SHA-256").digest(key.toByteArray())
                val result = StringBuilder(md.size * 2)

                md.forEach {
                    val i = it.toInt()
                    result.append(HEX_CHARS[i shr 4 and 0x0f])
                    result.append(HEX_CHARS[i and 0x0f])
                }
                return result.toString()

            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }

        return key
    }


    companion object {

        @JvmStatic
        fun init(application: Application) {
        }
    }
}
