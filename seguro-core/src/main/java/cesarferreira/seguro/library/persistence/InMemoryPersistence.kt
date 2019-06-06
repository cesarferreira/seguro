package cesarferreira.seguro.library.persistence

open class InMemoryPersistence : PersistenceManager {

    private var map = HashMap<String, String>()

    override fun persistenceName(): String = "InMemoryPersistence"

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

    @Synchronized
    override fun delete(key: String): Boolean {
        map.remove(key)
        return !map.containsKey(key)
    }

}
