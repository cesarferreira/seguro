package cesarferreira.seguro.library

import org.amshove.kluent.shouldEqual
import org.junit.Test

class DeleteTest : BaseSeguroTest() {

    @Test
    fun `Delete KEYS from storage, unencrypted`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptKey = false
            encryptValue = false
        })

        // WHEN
        seguro.Editor().put(KEY_NAME, stringToEncrypt).commit()

        // THEN
        seguro.getString(KEY_NAME) shouldEqual stringToEncrypt

        // WHEN
        seguro.Editor().delete(KEY_NAME).commit()

        // THEN
        seguro.getString(KEY_NAME) shouldEqual null
    }


    @Test
    fun `Delete KEYS from storage, encrypted`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptKey = true
            encryptValue = true
        })

        // WHEN
        seguro.Editor().put(KEY_NAME, stringToEncrypt).commit()

        // THEN
        seguro.getString(KEY_NAME) shouldEqual stringToEncrypt

        // WHEN
        seguro.Editor().delete(KEY_NAME).commit()

        // THEN
        seguro.getString(KEY_NAME) shouldEqual null
    }


}