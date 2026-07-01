package edu.damianmassarelli.notespmdm.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.damianmassarelli.notespmdm.data.model.Note
import kotlinx.coroutines.flow.Flow

// Interface para realizar transacciones con la BD
@Dao
interface NotesDao {
    @Query("SELECT * FROM note")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note ORDER BY title COLLATE NOCASE")
    fun getNotesSortedByTitle(): Flow<List<Note>>

    @Query("SELECT * FROM note ORDER BY title COLLATE NOCASE DESC")
    fun getNotesSortedByTitleDesc(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE idNote = :idNote")
    suspend fun getNoteById(idNote: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Delete
    suspend fun deleteNote(note: Note): Int
}