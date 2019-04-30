package cesarferreira.library.managers

import org.junit.Assert.assertEquals
import org.junit.Test

class AESEncryptionManagerTest {

    private var encryptionManager = AESEncryptionManager()

    @Test
    @Throws(Exception::class)
    fun testEncryptionAndDecryptionSimpleText() {

        val password = "Password@123"
        val originalText = "I'm pretty fly for a white guy"

        val encrypted = encryptionManager.encrypt(password, originalText)
        val decrypted = encryptionManager.decrypt(password, encrypted)

        assertEquals(originalText, decrypted)
    }

    @Test
    @Throws(Exception::class)
    fun testEncryptionAndDecryptionSpecialCharacters() {

        val password = "Password@123"
        val originalText = "*QdfKPoRE[gC*vtqVxZ2Eg]ZM7TeWnHyYTHU}DuEocJd6QxuZ9WJ"

        val encrypted = encryptionManager.encrypt(password, originalText)
        val decrypted = encryptionManager.decrypt(password, encrypted)

        assertEquals(originalText, decrypted)
    }
}