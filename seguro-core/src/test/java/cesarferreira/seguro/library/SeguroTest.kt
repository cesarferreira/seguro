package cesarferreira.seguro.library

import cesarferreira.seguro.library.encryption.AESEncryptionManager
import cesarferreira.seguro.library.persistance.PersistenceManager
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.amshove.kluent.any
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.lang.reflect.Constructor


class SeguroTest {

    private val defaultConfig = Seguro.Builder.Config(
        encryptKey = false,
        encryptValue = false,
        folderName = "asd",
        password = "password123"
    )

    private val aesEncryptionManager = AESEncryptionManager()

    @Test
    fun `Write and retrieve encrypted values`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(stringToEncrypt, defaultConfig.apply {
            encryptValue = true
        })

        // WHEN
        seguro.Editor().put(NAME_KEY, stringToEncrypt).apply()

        // THEN
        val decrypted = seguro.getString(NAME_KEY)

        decrypted shouldEqual stringToEncrypt

    }

    @Test
    fun `Write and retrieve unencrypted values`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(stringToEncrypt, defaultConfig.apply {
            encryptValue = false
        })

        // WHEN
        seguro.Editor().put(NAME_KEY, stringToEncrypt).apply()

        // THEN
        val decrypted = seguro.getString(NAME_KEY)

        decrypted shouldEqual stringToEncrypt
    }

    @Test
    fun `Write and retrieve encrypted KEYS and VALUES`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(stringToEncrypt, defaultConfig.apply {
            encryptKey = true
            encryptValue = true
        })

        // WHEN
        seguro.Editor().put(NAME_KEY, stringToEncrypt).apply()

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

    private fun buildSeguroWithParams(stringToEncrypt: String, testConfig: Seguro.Builder.Config): Seguro {

        val valueFromFile = if (testConfig.encryptValue) {
            aesEncryptionManager.encrypt(testConfig.password, stringToEncrypt)
        } else {
            stringToEncrypt
        }

        val fileManagerMock = mock<PersistenceManager> {
            on { read(any()) } doReturn valueFromFile
            on { write(any(), any()) } doReturn true
        }

        return makeSeguro(fileManagerMock, testConfig)
    }

    companion object {
        const val NAME_KEY = "NAME_KEY"
    }
}