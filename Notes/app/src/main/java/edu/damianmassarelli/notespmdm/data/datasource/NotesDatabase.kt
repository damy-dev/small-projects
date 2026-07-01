package edu.damianmassarelli.notespmdm.data.datasource

import android.content.Context
import androidx.room.Room
import edu.damianmassarelli.notespmdm.data.model.Note
import androidx.room.Database
import androidx.room.RoomDatabase

// Clase que devuelve una instancia única de la BD.
// Si la BD no existe, la crea.
@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = true
)
abstract class NotesDatabase : RoomDatabase() {
    // Obtiene la instancia del DAO que estará asociada a la BD
    abstract fun notesDAO(): NotesDao

    // Singleton de la instancia de la BD
    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getInstance(context: Context): NotesDatabase {
            return synchronized(this) {
                Room.databaseBuilder(context.applicationContext,
                    NotesDatabase::class.java,
                    "notes.db")
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}