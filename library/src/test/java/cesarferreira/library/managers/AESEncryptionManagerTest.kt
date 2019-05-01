package cesarferreira.library.managers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class AESEncryptionManagerTest {

    private var encryptionManager = AESEncryptionManager()


    @Test
    @Throws(Exception::class)
    fun `test Encryption And Decryption Simple Text`() {

        val password = "Password@123"
        val originalText = "I'm pretty fly for a white guy"

        val encrypted = encryptionManager.encrypt(password, originalText)
        val decrypted = encryptionManager.decrypt(password, encrypted)

        assertEquals(originalText, decrypted)
    }

    @Test
    @Throws(Exception::class)
    fun `test Encryption And Decryption Special Characters`() {

        val password = "Password@123"
        val originalText = "*QdfKPoRE[gC*vtqVxZ2Eg]ZM7TeWnHyYTHU}DuEocJd6QxuZ9WJ"

        val encrypted = encryptionManager.encrypt(password, originalText)
        val decrypted = encryptionManager.decrypt(password, encrypted)

        assertEquals(originalText, decrypted)
    }

    @Test
    @Throws(Exception::class)
    fun `test Encryption And Decryption des output same result`() {

        val password = "Password@123"
        val originalText = "*QdfKPoRE[gC*vtqVxZ2Eg]ZM7TeWnHyYTHU}DuEocJd6QxuZ9WJ"

        val encrypted1 = encryptionManager.encrypt(password, originalText)
        val encrypted2 = encryptionManager.encrypt(password, originalText)
        val encrypted3 = encryptionManager.encrypt(password, originalText)

        assertEquals(originalText, encryptionManager.decrypt(password, encrypted1))
        assertEquals(originalText, encryptionManager.decrypt(password, encrypted2))
        assertEquals(originalText, encryptionManager.decrypt(password, encrypted3))
//        assertTrue("They should all be the same", (encrypted1 == encrypted2 && encrypted1 == encrypted3))
    }
}