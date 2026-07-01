package edu.damianmassarelli.notespmdm.ui

// Clase con los objetos de las pantallas de navegación
sealed class Screens(val route: String) {
    object NotesListScreen: Screens("notes_list")
    object NoteDetailScreen: Screens("note_detail")
}