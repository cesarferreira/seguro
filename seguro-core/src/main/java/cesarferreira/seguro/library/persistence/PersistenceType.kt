package cesarferreira.seguro.library.persistence

import android.content.Context

sealed class PersistenceType {
    object None : PersistenceType()
    data class SharedPreferences(val context: Context) : PersistenceType()
    data class SDCard(val destinationFolder: String) : PersistenceType()
    object InMemory : PersistenceType()
}