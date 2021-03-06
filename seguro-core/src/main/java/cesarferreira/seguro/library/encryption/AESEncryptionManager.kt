package cesarferreira.seguro.library.encryption

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class AESEncryptionManager {

    /**
     * Encrypt text with a symmetric key
     *
     * @param password symmetric key
     * @param plainText text to encrypt
     * @return encrypted plain text in hex
     */
    @Throws(Exception::class)
    fun encrypt(password: String, plainText: String): String {
        return encryptToByteArray(password, plainText).toHex()
    }

    /**
     * Decrypt text with a symmetric key
     *
     * @param password symmetric key
     * @param hexEncryptedText encrypted text in hex
     * @return decrypted text
     */
    @Throws(Exception::class)
    fun decrypt(password: String, hexEncryptedText: String): String {
        return decryptFromByteArray(password, hexEncryptedText.hexToByteArray())
    }

    private val digits = "0123456789ABCDEF"

    private fun ByteArray.toHex(): String {
        val hexChars = CharArray(this.size * 2)
        for (i in this.indices) {
            val v = this[i].toInt() and 0xff
            hexChars[i * 2] = digits[v shr 4]
            hexChars[i * 2 + 1] = digits[v and 0xf]
        }
        return String(hexChars)
    }

    private fun String.hexToByteArray() =
        ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }

    @Throws(Exception::class)
    private fun encryptToByteArray(key: String, plainText: String): ByteArray {
        val clean = plainText.toByteArray()

        // Generating IV.
        val ivSize = 16
        val iv = ByteArray(ivSize)
        val random = SecureRandom()
        random.nextBytes(iv)
        val ivParameterSpec = IvParameterSpec(iv)

        // Hashing key.
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(key.toByteArray(charset("UTF-8")))
        val keyBytes = ByteArray(16)
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.size)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")

        // Encrypt.
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encrypted = cipher.doFinal(clean)

        // Combine IV and encrypted part.
        val encryptedIVAndText = ByteArray(ivSize + encrypted.size)
        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize)
        System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.size)

        return encryptedIVAndText
    }

    @Throws(Exception::class)
    private fun decryptFromByteArray(key: String, encryptedIvTextBytes: ByteArray): String {
        val ivSize = 16
        val keySize = 16

        // Extract IV.
        val iv = ByteArray(ivSize)
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.size)
        val ivParameterSpec = IvParameterSpec(iv)

        // Extract encrypted part.
        val encryptedSize = encryptedIvTextBytes.size - ivSize
        val encryptedBytes = ByteArray(encryptedSize)
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize)

        // Hash key.
        val keyBytes = ByteArray(keySize)
        val md = MessageDigest.getInstance("SHA-256")
        md.update(key.toByteArray())
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.size)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")

        // Decrypt.
        val cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val decrypted = cipherDecrypt.doFinal(encryptedBytes)

        return String(decrypted)
    }
}
