package cesarferreira.seguro.library.persistence

interface IPersistenceManager {
    fun write(key: String, value: String): Boolean
    fun read(key: String): String?
    fun wipe(): Boolean

    fun persistenceName(): String
}