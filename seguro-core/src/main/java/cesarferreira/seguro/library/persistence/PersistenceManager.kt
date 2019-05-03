package cesarferreira.seguro.library.persistence

interface PersistenceManager {
    fun write(key: String, value: String): Boolean
    fun read(key: String): String?
    fun wipe(): Boolean
}