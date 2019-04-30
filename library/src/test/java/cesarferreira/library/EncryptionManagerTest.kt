package cesarferreira.library

import cesarferreira.library.managers.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test

class EncryptionManagerTest {

    private var encryptionManager = EncryptionManager()

    @Test
    @Throws(Exception::class)
    fun testEncryptionAndDecryption() {

        val password = "Password@123"
        val originalText = "I'm pretty fly for a white guy"

        val encrypted = encryptionManager.encrypt(password, originalText)
        val decrypted = encryptionManager.decrypt(password, encrypted)

        assertEquals(originalText, decrypted)
    }
}