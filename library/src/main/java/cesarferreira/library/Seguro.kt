package cesarferreira.library

import android.Manifest
import android.app.Application
import android.content.Context
import androidx.annotation.RequiresPermission
import cesarferreira.library.managers.EncryptionManager
import cesarferreira.library.managers.FileManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class Seguro private constructor(
    private val config: Config,
    private val fileManager: FileManager,
    private val encryptionManager: EncryptionManager
) {

    fun clear() {
    }

    fun getString(key: String): String? {
        return fileManager.readFromFile(hashKey(key))
    }

    private fun encryptValue(value: String): String {
        return if (config.encryptValue) {
//            encryptionManager.encrypt()
            value
        } else {
            value

        }
    }


    private fun hashKey(key: String): String {
        if (config.encryptKey) {
            try {
                val HEX_CHARS = "0123456789ABCDEF"
                val md = MessageDigest.getInstance("SHA-256").digest(key.toByteArray());
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


    // EDITOR
    inner class Editor {

        private var pendingWrites = HashMap<String, Any>()

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

        @RequiresPermission(
            allOf = [Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE]
        )
        fun apply() {

            // TODO write them to a filesystem

            // wipe them
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
    class Builder(context: Context) {

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
            val encryptionManager = EncryptionManager()
            val fileManager = FileManager(config.folderName, config.password)

            return Seguro(config, fileManager, encryptionManager)
        }
    }


    companion object {

        @JvmStatic
        fun init(application: Application) {
        }
    }
}
