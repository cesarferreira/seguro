package cesarferreira.seguro.library

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import androidx.annotation.CheckResult
import androidx.annotation.RequiresPermission
import cesarferreira.seguro.library.encryption.AESEncryptionManager
import cesarferreira.seguro.library.persistance.InMemoryPersistence
import cesarferreira.seguro.library.persistance.PersistenceManager
import cesarferreira.seguro.library.persistance.SdCardPersistence
import cesarferreira.seguro.library.persistance.SharedPrefPersistence
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class Seguro private constructor(
    private val config: Builder.Config,
    private val persistenceManager: PersistenceManager,
    private val encryptionManager: AESEncryptionManager
) {

    fun clear() {
        persistenceManager.wipe()
    }

    fun getString(key: String): String? {
        val fromFile = persistenceManager.read(hashKey(key))
        return decryptValue(fromFile!!)
    }


    @CheckResult
    fun getInt(key: String): Int? {
        try {
            val value = getString(key) ?: return -99

            return Integer.parseInt(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Integer data type", e)
            return -99
        }

    }

    @CheckResult
    fun getInt(key: String, defaultValue: Int): Int? {
        try {
            val value = getString(key) ?: return defaultValue

            return Integer.parseInt(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Integer data type", e)
            return -99
        }

    }

    @CheckResult
    fun getFloat(key: String): Float? {
        try {
            val value = getString(key) ?: return 0f

            return java.lang.Float.parseFloat(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Float data type", e)
            return 0f
        }

    }

    @CheckResult
    fun getFloat(key: String, defaultValue: Float): Float? {
        try {
            val value = getString(key) ?: return defaultValue

            return java.lang.Float.parseFloat(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Float data type", e)
            return defaultValue
        }

    }

    @CheckResult
    fun getDouble(key: String): Double? {
        try {
            val value = getString(key) ?: return 0.0
            return java.lang.Double.parseDouble(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Double data type", e)
            return 0.0
        }

    }

    @CheckResult
    fun getDouble(key: String, defaultValue: Double): Double? {
        try {
            val value = getString(key) ?: return defaultValue

            return java.lang.Double.parseDouble(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Double data type", e)
            return defaultValue
        }

    }

    @CheckResult
    fun getLong(key: String): Long? {
        try {
            val value = getString(key) ?: return 0L

            return java.lang.Long.parseLong(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Long data type", e)
            return 0L
        }

    }

    @CheckResult
    fun getLong(key: String, defaultValue: Long): Long? {
        try {
            val value = getString(key) ?: return defaultValue

            return java.lang.Long.parseLong(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Long data type", e)
            return defaultValue
        }

    }

    @CheckResult
    fun getBoolean(key: String): Boolean? {
        try {
            val value = getString(key)
            return value != null && java.lang.Boolean.parseBoolean(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Boolean data type", e)
            return false
        }

    }

    @CheckResult
    fun getBoolean(key: String, defaultValue: Boolean): Boolean? {
        try {
            val value = getString(key) ?: return defaultValue

            return java.lang.Boolean.parseBoolean(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Boolean data type", e)
            return false
        }

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
            // write
            pendingWrites.forEach {
                println("key: ${it.key}, value: ${it.value}")
                persistenceManager.write(it.key, it.value)
            }

            // wipe pending writes
            pendingWrites = hashMapOf()
        }
    }

    // BUILDER
    class Builder {

        internal data class Config(
            var encryptKey: Boolean = false,
            var encryptValue: Boolean = false,
            var password: String = "",
            var folderName: String = "",
            var persistenceType: PersistenceType = PersistenceType.InMemory
        )

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

        fun setPersistentType(type: PersistenceType): Builder {
            config.persistenceType = type
            return this
        }

        fun build(): Seguro {
            val encryptionManager = AESEncryptionManager()
            val fileManager = when (config.persistenceType) {
                is PersistenceType.None -> object :
                    PersistenceManager {
                    override fun write(key: String, value: String): Boolean = true
                    override fun read(key: String): String? = null
                    override fun wipe() = true
                }
                is PersistenceType.SharedPreferences -> SharedPrefPersistence(
                    (config.persistenceType as PersistenceType.SharedPreferences).context,
                    BuildConfig.APPLICATION_ID
                )
                is PersistenceType.SdCard -> SdCardPersistence(config.folderName)
                is PersistenceType.InMemory -> InMemoryPersistence()
            }

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

    private fun throwRunTimeException(message: String, throwable: Throwable) =
        RuntimeException(message, throwable).printStackTrace()

    sealed class PersistenceType {
        object None : PersistenceType()
        data class SharedPreferences(val context: Context) : PersistenceType()
        object SdCard : PersistenceType()
        object InMemory : PersistenceType()
    }
}
