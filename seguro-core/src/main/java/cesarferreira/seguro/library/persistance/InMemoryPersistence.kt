package cesarferreira.seguro.library.persistance

open class InMemoryPersistence : PersistenceManager {

    private var map = HashMap<String, String>()

    @Synchronized
    override fun write(key: String, value: String): Boolean {
        map[key] = value
        return true
    }

    @Synchronized
    override fun read(key: String): String? = map[key]

    @Synchronized
    override fun wipe(): Boolean {
        map.clear()
        return true
    }

}