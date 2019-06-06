package cesarferreira.seguro.library

import cesarferreira.seguro.library.encryption.AESEncryptionManager
import cesarferreira.seguro.library.persistence.InMemoryPersistence
import cesarferreira.seguro.library.persistence.PersistenceManager
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.lang.reflect.Constructor
import java.util.*


open class BaseSeguroTest {

    internal val defaultConfig = Seguro.Builder.Config(
        encryptKey = false,
        encryptValue = false,
        folderName = ".com.example.seguro",
        password = "password123",
        enableLogging = true,
        persistenceType = Seguro.PersistenceType.InMemory
    )

    internal val aesEncryptionManager = AESEncryptionManager()


    internal fun buildSeguroWithParams(testConfig: Seguro.Builder.Config): Seguro {

        val persistenceManagerMock = InMemoryPersistence()

        val constructor: Constructor<Seguro> = Seguro::class.java.getDeclaredConstructor(
            Seguro.Builder.Config::class.java,
            PersistenceManager::class.java,
            AESEncryptionManager::class.java
        )

        constructor.isAccessible = true

        return constructor.newInstance(testConfig, persistenceManagerMock, aesEncryptionManager)
    }

    companion object {
        const val KEY_NAME = "KEY_NAME"
        const val KEY_TIME = "KEY_TIME"
        const val KEY_AGE = "KEY_AGE"
    }
}