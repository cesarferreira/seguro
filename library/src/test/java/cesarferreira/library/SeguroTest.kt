package cesarferreira.library

import cesarferreira.library.managers.AESEncryptionManager
import cesarferreira.library.managers.FileManager
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.amshove.kluent.any
import org.amshove.kluent.shouldBe
import org.junit.Test
import java.lang.reflect.Constructor


class SeguroTest {

    private val config = Seguro.Config(
        encryptKey = false,
        encryptValue = false,
        folderName = "asd",
        password = "password123"
    )

    private val aesEncryptionManager = AESEncryptionManager()

    private fun makeSeguro(fileManagerMock: FileManager): Seguro {
        val constructor: Constructor<Seguro> = Seguro::class.java.getDeclaredConstructor(
            Seguro.Config::class.java,
            FileManager::class.java,
            AESEncryptionManager::class.java
        )

        constructor.isAccessible = true

        return constructor.newInstance(config, fileManagerMock, aesEncryptionManager)
    }

    @Test
    fun `Write and retrieve values with seguro`() {

        // GIVEN
        val originalValue = "i am such a secret"

        val fileManagerMock = mock<FileManager> {
            on { readFromFile(any()) } doReturn originalValue
        }

        val seguro = makeSeguro(fileManagerMock)

        // WHEN
        seguro.Editor().put(NAME_KEY, originalValue).apply()

        // THEN
        val decrypted = seguro.getString(NAME_KEY)

        decrypted shouldBe originalValue

    }

    companion object {
        const val NAME_KEY = "name"
    }
}