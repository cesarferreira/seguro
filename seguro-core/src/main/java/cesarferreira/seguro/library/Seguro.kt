package cesarferreira.seguro.library

import cesarferreira.seguro.library.encryption.AESEncryptionManager
import cesarferreira.seguro.library.persistence.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class Seguro private constructor(
        private val config: Builder.Config,
        private val caches: ArrayList<IPersistenceManager>,
        private val encryptionManager: AESEncryptionManager
) {

    /**
     * Wipes everything from persistence
     */
    fun clear() = caches.forEach { it.wipe() }

    fun getString(key: String): String? {
        log("Looking for: $key")

        val hashKey = getHashedKey(key)

        val fromFile = getStringFrom(hashKey)
        val decryptedValue = fromFile?.let { decryptValue(it) }

        log("READ[\"$key\"] = $decryptedValue")

        return decryptedValue
    }

    private fun log(str: String) = if (config.enableLogging) println(str) else Unit

    private fun getStringFrom(key: String): String? {

        var foundIt: String? = null
        var foundItAtPosition = -1

        caches.forEachIndexed { index, cache ->
            val result = cache.read(key)
            if (result != null) {
                foundIt = result
                foundItAtPosition = index
                return@forEachIndexed
            }
        }

        if (foundIt != null) {
            // cache HIT
            log("Found it at ${caches[foundItAtPosition].persistenceName()}")
        } else {
            // cache MISS
            log("Could not find it")
        }

        return foundIt
    }


    fun getInt(key: String, defaultValue: Int): Int? {
        try {
            val value = getString(key) ?: return defaultValue
            return Integer.parseInt(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Integer data type", e)
            return null
        }
    }

    fun getFloat(key: String, defaultValue: Float): Float? {
        try {
            val value = getString(key) ?: return defaultValue
            return java.lang.Float.parseFloat(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Float data type", e)
            return defaultValue
        }
    }

    fun getDouble(key: String, defaultValue: Double): Double? {
        try {
            val value = getString(key) ?: return defaultValue
            return java.lang.Double.parseDouble(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Double data type", e)
            return defaultValue
        }
    }

    fun getLong(key: String, defaultValue: Long): Long? {
        try {
            val value = getString(key) ?: return defaultValue
            return java.lang.Long.parseLong(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Long data type", e)
            return defaultValue
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean? {
        try {
            val value = getString(key) ?: return defaultValue
            return java.lang.Boolean.parseBoolean(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Boolean data type", e)
            return null
        }
    }

    // EDITOR
    inner class Editor {

        private var pendingWrites = hashMapOf<String, String>()

        fun put(key: String, value: String): Editor {
            val hashedKey = getHashedKey(key)
            val encryptedValue = encryptValue(value)

            if (config.enableLogging) {
                log("WRITE[\"$key\"] = $encryptedValue")
            }
            pendingWrites[hashedKey] = encryptedValue

            return this
        }

        fun put(key: String, value: Int): Editor {
            return put(key, Integer.toString(value))
        }

        fun put(key: String, value: Long): Editor {
            return put(key, java.lang.Long.toString(value))
        }

        fun put(key: String, value: Double): Editor {
            return put(key, java.lang.Double.toString(value))
        }

        fun put(key: String, value: Float): Editor {
            return put(key, java.lang.Float.toString(value))
        }

        fun put(key: String, value: Boolean): Editor {
            return put(key, java.lang.Boolean.toString(value))
        }

        fun put(key: String, value: List<*>): Editor {
            return put(key, value.toString())
        }

        fun put(key: String, bytes: ByteArray): Editor {
            return put(key, String(bytes))
        }

        fun commit() {

            // write
            pendingWrites.forEach { pending -> caches.forEach { it.write(pending.key, pending.value) } }

            // wipe pending writes
            pendingWrites.clear()
        }
    }

    // BUILDER
    class Builder {

        internal data class Config(
                var encryptKey: Boolean = false,
                var encryptValue: Boolean = false,
                var password: String = "",
                var folderName: String = "",
                var enableLogging: Boolean = false,
                var enabledCacheTypes: ArrayList<PersistenceType> = arrayListOf(PersistenceType.InMemory)
        )

        private val config = Config()

        fun enableEncryption(encryptKey: Boolean, encryptValue: Boolean): Builder {
            config.encryptKey = encryptKey
            config.encryptValue = encryptValue
            return this
        }

        fun setEncryptionPassword(password: String): Builder {
            config.password = password
            return this
        }

        fun addPersistence(type: PersistenceType): Builder {
            config.enabledCacheTypes.add(type)
            return this
        }

        fun enableLogging(shouldLog: Boolean): Builder {
            config.enableLogging = shouldLog
            return this
        }

        fun build(): Seguro {
            val encryptionManager = AESEncryptionManager()

            val caches: ArrayList<IPersistenceManager> = arrayListOf()

            config.enabledCacheTypes.forEach {

                val persistence = when (it) {
                    is PersistenceType.None -> object :
                            IPersistenceManager {
                        override fun persistenceName(): String = "None"
                        override fun write(key: String, value: String): Boolean = true
                        override fun read(key: String): String? = null
                        override fun wipe() = true
                    }
                    is PersistenceType.SharedPreferences -> SharedPrefPersistence(
                            it.context,
                            BuildConfig.APPLICATION_ID
                    )
                    is PersistenceType.SDCard -> SdCardPersistence(it.destinationFolder)
                    is PersistenceType.InMemory -> InMemoryPersistence()
                }

                caches.add(persistence)
            }

            return Seguro(config, caches, encryptionManager)
        }
    }

    private fun encryptValue(value: String): String {
        return if (config.encryptValue) {
            return encryptionManager.encrypt(config.password, value)
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

    private fun getHashedKey(key: String): String {
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

}

