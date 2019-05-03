package cesarferreira.seguro.library

import cesarferreira.seguro.library.encryption.AESEncryptionManager
import cesarferreira.seguro.library.persistence.InMemoryPersistence
import cesarferreira.seguro.library.persistence.PersistenceManager
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.lang.reflect.Constructor
import java.util.*


class SeguroTest {

    private val defaultConfig = Seguro.Builder.Config(
        encryptKey = false,
        encryptValue = false,
        folderName = ".com.example.seguro",
        password = "password123",
        persistenceType = Seguro.PersistenceType.InMemory
    )

    private val aesEncryptionManager = AESEncryptionManager()

    @Test
    fun `Write and retrieve encrypted values`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptValue = true
        })

        // WHEN
        seguro.Editor().put(KEY_NAME, stringToEncrypt).commit()

        // THEN
        val decrypted = seguro.getString(KEY_NAME)

        decrypted shouldEqual stringToEncrypt

    }

    @Test
    fun `Write and retrieve various different types of values`() {

        // GIVEN
        val name = "Cesar Ferreira"
        val age = 31
        val time = Date().time

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptValue = true
            encryptKey = true
        })

        // WHEN
        seguro.Editor()
            .put(KEY_TIME, time)
            .put(KEY_NAME, name)
            .put(KEY_AGE, age)
            .commit()

        // THEN
        seguro.getLong(KEY_TIME) shouldEqual time
        seguro.getString(KEY_NAME) shouldEqual name
        seguro.getInt(KEY_AGE) shouldEqual age

    }

    @Test
    fun `Write and retrieve unencrypted values`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptValue = false
        })

        // WHEN
        seguro.Editor().put(KEY_NAME, stringToEncrypt).commit()

        // THEN
        val decrypted = seguro.getString(KEY_NAME)

        decrypted shouldEqual stringToEncrypt
    }

    @Test
    fun `Write and retrieve encrypted KEYS and VALUES`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptKey = true
            encryptValue = true
        })

        // WHEN
        seguro.Editor().put(KEY_NAME, stringToEncrypt).commit()

        // THEN
        val decrypted = seguro.getString(KEY_NAME)

        decrypted shouldEqual stringToEncrypt
    }

    private fun buildSeguroWithParams(testConfig: Seguro.Builder.Config): Seguro {

        val persistenceManagerMock = InMemoryPersistence()

        val constructor: Constructor<Seguro> = Seguro::class.java.getDeclaredConstructor(
            Seguro.Builder.Config::class.java,
            PersistenceManager::class.java,
            AESEncryptionManager::class.java
        )

        constructor.isAccessible = true

        return constructor.newInstance(testConfig, persistenceManagerMock, aesEncryptionManager)
    }

    companion object {
        const val KEY_NAME = "KEY_NAME"
        const val KEY_TIME = "KEY_TIME"
        const val KEY_AGE = "KEY_AGE"
    }
}