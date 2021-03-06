package cesarferreira.seguro.library.persistence

import android.content.Context

open class SharedPrefPersistence(context: Context, prefsFileName: String) : PersistenceManager {

    private val prefs = context.getSharedPreferences(prefsFileName, 0)

    override fun persistenceName() = "SharedPrefPersistence"

    @Synchronized
    override fun write(key: String, value: String) = prefs.edit().putString(key, value).commit()

    @Synchronized
    override fun read(key: String): String? = prefs.getString(key, null)

    @Synchronized
    override fun delete(key: String) = prefs.edit().remove(key).commit()

    @Synchronized
    override fun wipe(): Boolean = prefs.edit().clear().commit()

}