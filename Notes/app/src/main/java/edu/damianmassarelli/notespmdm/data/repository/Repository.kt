package edu.damianmassarelli.notespmdm.data.repository

import edu.damianmassarelli.notespmdm.data.datasource.LocalDatasource
import edu.damianmassarelli.notespmdm.data.model.Note
import kotlinx.coroutines.flow.Flow

// Clase Repositorio de acceso a datos
class Repository(private val localDatasource: LocalDatasource) {

    // Tiene siempre actualizada la lista de notas
    val currentNotes: Flow<List<Note>> = localDatasource.currentNotes

    enum class SortOrder { AZ, ZA }

    // Metodos para transacciones con la BD local
    fun getNotes(sortOrder: SortOrder = SortOrder.AZ): Flow<List<Note>> {
        if (sortOrder.name.equals("AZ")) {
            return localDatasource.getAllNotes()
        } else {
            return localDatasource.getNotesSortedByTitleDesc()
        }
    }

    suspend fun getNoteById(idNote: Long): Note? = localDatasource.getNoteById(idNote)

    suspend fun insertNote(note: Note): Long = localDatasource.insertNote(note)

    suspend fun deleteNote(note: Note): Int = localDatasource.deleteNote(note)

}