package cesarferreira.seguro.library.persistance

interface FileManager {
    fun write(key: String, value: String): Boolean
    fun read(key: String): String?
    fun wipe(): Boolean
}