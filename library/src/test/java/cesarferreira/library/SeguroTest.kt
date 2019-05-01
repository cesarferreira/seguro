package cesarferreira.library

import cesarferreira.library.managers.AESEncryptionManager
import cesarferreira.library.managers.FileManager
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.amshove.kluent.any
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.lang.reflect.Constructor


class SeguroTest {

    private val defaultConfig = Seguro.Config(
        encryptKey = false,
        encryptValue = false,
        folderName = "asd",
        password = "password123"
    )

    private val aesEncryptionManager = AESEncryptionManager()

    @Test
    fun `Write and retrieve encrypted values`() {

        // GIVEN
        val originalValue = "i am such a secret"

        val seguro = buildSeguroWithParams(originalValue, defaultConfig.apply {
            encryptValue = true
        })

        // WHEN
        seguro.Editor().put(NAME_KEY, originalValue).apply()

        // THEN
        val decrypted = seguro.getString(NAME_KEY)

        decrypted shouldEqual originalValue

    }

    @Test
    fun `Write and retrieve unencrypted values`() {

        // GIVEN
        val originalValue = "i am such a secret"

        val seguro = buildSeguroWithParams(originalValue, defaultConfig.apply {
            encryptValue = false
        })

        // WHEN
        seguro.Editor().put(NAME_KEY, originalValue).apply()

        // THEN
        val decrypted = seguro.getString(NAME_KEY)

        decrypted shouldEqual originalValue
    }

    @Test
    fun `Write and retrieve encrypted KEYS and VALUES`() {

        // GIVEN
        val originalValue = "i am such a secret"

        val seguro = buildSeguroWithParams(originalValue, defaultConfig.apply {
            encryptKey = true
            encryptValue = true
        })

        // WHEN
        seguro.Editor().put(NAME_KEY, originalValue).apply()

        // THEN
        val decrypted = seguro.getString(NAME_KEY)

        decrypted shouldEqual originalValue

    }

    private fun makeSeguro(
        fileManagerMock: FileManager,
        customConfig: Seguro.Config
    ): Seguro {
        val constructor: Constructor<Seguro> = Seguro::class.java.getDeclaredConstructor(
            Seguro.Config::class.java,
            FileManager::class.java,
            AESEncryptionManager::class.java
        )

        constructor.isAccessible = true

        return constructor.newInstance(customConfig, fileManagerMock, aesEncryptionManager)
    }

    private fun buildSeguroWithParams(originalValue: String, testConfig: Seguro.Config): Seguro {

        val valueFromFile = if (testConfig.encryptValue) {
            aesEncryptionManager.encrypt(testConfig.password, originalValue)
        } else {
            originalValue
        }

        val fileManagerMock = mock<FileManager> {
            on { readFromFile(any()) } doReturn valueFromFile
        }

        return makeSeguro(fileManagerMock, testConfig)
    }

    companion object {
        const val NAME_KEY = "name"
    }
}