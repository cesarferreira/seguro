package cesarferreira.seguro.library

import cesarferreira.seguro.library.encryption.AESEncryptionManager
import cesarferreira.seguro.library.persistance.InMemoryPersistence
import cesarferreira.seguro.library.persistance.PersistenceManager
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.lang.reflect.Constructor


class SeguroTest {

    private val defaultConfig = Seguro.Builder.Config(
        encryptKey = false,
        encryptValue = false,
        folderName = "asd",
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
        seguro.Editor().put(NAME_KEY, stringToEncrypt).commit()

        // THEN
        val decrypted = seguro.getString(NAME_KEY)

        decrypted shouldEqual stringToEncrypt

    }

    @Test
    fun `Write and retrieve unencrypted values`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptValue = false
        })

        // WHEN
        seguro.Editor().put(NAME_KEY, stringToEncrypt).commit()

        // THEN
        val decrypted = seguro.getString(NAME_KEY)

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
        seguro.Editor().put(NAME_KEY, stringToEncrypt).commit()

        // THEN
        val decrypted = seguro.getString(NAME_KEY)

        decrypted shouldEqual stringToEncrypt
    }

    private fun makeSeguro(
        persistenceManagerMock: PersistenceManager,
        customConfig: Seguro.Builder.Config
    ): Seguro {
        val constructor: Constructor<Seguro> = Seguro::class.java.getDeclaredConstructor(
            Seguro.Builder.Config::class.java,
            PersistenceManager::class.java,
            AESEncryptionManager::class.java
        )

        constructor.isAccessible = true

        return constructor.newInstance(customConfig, persistenceManagerMock, aesEncryptionManager)
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
        const val NAME_KEY = "NAME_KEY"
    }
}