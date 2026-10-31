package fr.parentsapariscentre.badgecreche.data.repository

import android.content.Context
import fr.parentsapariscentre.badgecreche.data.model.CrecheDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class CrecheCatalogRepository private constructor(private val context: Context) {

    private var directory: CrecheDirectory? = null
    private val mutex = Mutex()

    suspend fun getDirectory(): CrecheDirectory = withContext(Dispatchers.IO) {
        directory?.let { return@withContext it }
        mutex.withLock {
            directory?.let { return@withLock it }
            val jsonString = context.assets.open("creches-paris.json")
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
            val parsed = CrecheDirectory.parse(jsonString)
            directory = parsed
            parsed
        }
    }

    companion object {
        @Volatile
        private var instance: CrecheCatalogRepository? = null

        fun getInstance(context: Context): CrecheCatalogRepository {
            return instance ?: synchronized(this) {
                instance ?: CrecheCatalogRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
