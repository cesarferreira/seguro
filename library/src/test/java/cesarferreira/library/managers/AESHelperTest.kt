package cesarferreira.library.managers

import org.junit.Assert.*
import org.junit.Test

class AESHelperTest {
    private var encryptionManager = AESHelper()

    @Test
    @Throws(Exception::class)
    fun testEncryptionAndDecryption() {

        val password = "Password@123"
        val originalText = "I'm pretty fly for a white guy"

        val encrypted = AESHelper.encrypt(password, originalText)
        val decrypted = AESHelper.decrypt(password, encrypted)

        assertEquals(originalText, decrypted)
    }
}