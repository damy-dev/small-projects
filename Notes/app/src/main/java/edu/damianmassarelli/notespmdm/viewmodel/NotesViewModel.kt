package edu.damianmassarelli.notespmdm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.damianmassarelli.notespmdm.data.datasource.LocalDatasource
import edu.damianmassarelli.notespmdm.data.datasource.NotesDatabase
import edu.damianmassarelli.notespmdm.data.model.Note
import edu.damianmassarelli.notespmdm.data.repository.Repository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// ViewModel que obtiene datos del repositorio y los muestra a la UI
// y viceversa
class NotesViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: Repository
    private val localDatasource: LocalDatasource

    private val _currentNotes = MutableStateFlow<List<Note>>(emptyList())
    val currentNotes: StateFlow<List<Note>> = _currentNotes

    init {
        val db = NotesDatabase.getInstance(app)
        val dao = db.notesDAO()
        localDatasource = LocalDatasource(dao)
        repository = Repository(localDatasource)

        loadAllNotes()
    }

    // Carga de datos y métodos asincrónicos para interactuar con el repositorio
    fun loadAllNotes(sortOrder: Repository.SortOrder = Repository.SortOrder.AZ) {
        viewModelScope.launch {
            repository.getNotes(sortOrder)
                .catch { e -> e.printStackTrace() }
                .collect { notes -> _currentNotes.value = notes }
        }
    }

    fun saveNote(note: Note): Deferred<Long> {
        return viewModelScope.async {
            repository.insertNote(note)
        }
    }

    fun deleteNote(note: Note): Deferred<Int> {
        return viewModelScope.async {
            repository.deleteNote(note)
        }
    }

    fun getNoteById(idNote: Long): Deferred<Note?> {
        return viewModelScope.async {
            repository.getNoteById(idNote)
        }
    }
}