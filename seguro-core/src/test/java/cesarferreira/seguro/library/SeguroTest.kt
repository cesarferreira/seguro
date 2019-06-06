package cesarferreira.seguro.library

import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.util.*


class SeguroTest : BaseSeguroTest() {

    @Test
    fun `Write and retrieve encrypted values`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptValue = true
        })

        // WHEN
        seguro.Editor().put(KEY_NAME, stringToEncrypt).commit()

        // THEN
        val decrypted = seguro.getString(KEY_NAME)

        decrypted shouldEqual stringToEncrypt

    }

    @Test
    fun `Write and retrieve various different types of values`() {

        // GIVEN
        val name = "Cesar Ferreira"
        val age = 31
        val time = Date().time

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptValue = true
            encryptKey = true
        })

        // WHEN
        seguro.Editor()
            .put(KEY_TIME, time)
            .put(KEY_NAME, name)
            .put(KEY_AGE, age)
            .commit()

        // THEN
        seguro.getLong(KEY_TIME, -1L) shouldEqual time
        seguro.getString(KEY_NAME) shouldEqual name
        seguro.getInt(KEY_AGE, -1) shouldEqual age

    }

    @Test
    fun `Write and retrieve unencrypted values`() {

        // GIVEN
        val stringToEncrypt = "i am such a secret"

        val seguro = buildSeguroWithParams(defaultConfig.apply {
            encryptValue = false
        })

        // WHEN
        seguro.Editor().put(KEY_NAME, stringToEncrypt).commit()

        // THEN
        val decrypted = seguro.getString(KEY_NAME)

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
        seguro.Editor().put(KEY_NAME, stringToEncrypt).commit()

        // THEN
        val decrypted = seguro.getString(KEY_NAME)

        decrypted shouldEqual stringToEncrypt
    }
}