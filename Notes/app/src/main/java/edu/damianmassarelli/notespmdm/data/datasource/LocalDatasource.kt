package edu.damianmassarelli.notespmdm.data.datasource

import edu.damianmassarelli.notespmdm.data.model.Note
import kotlinx.coroutines.flow.Flow

// Clase con acceso al almacenamiento local del dispositivo.
// Intemediaria entre el Repositorio y la BD local
class LocalDatasource(private val dao: NotesDao) {
    val currentNotes: Flow<List<Note>> = dao.getAllNotes()

    fun getAllNotes(): Flow<List<Note>> = dao.getNotesSortedByTitle()

    fun getNotesSortedByTitleDesc(): Flow<List<Note>> = dao.getNotesSortedByTitleDesc()

    suspend fun getNoteById(idNote: Long): Note? = dao.getNoteById(idNote)

    suspend fun insertNote(note: Note): Long = dao.insertNote(note)

    suspend fun deleteNote(note: Note): Int = dao.deleteNote(note)

}