package edu.damianmassarelli.notespmdm.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.damianmassarelli.notespmdm.NotesListScreen
import edu.damianmassarelli.notespmdm.ui.components.NoteDetailScreen
import edu.damianmassarelli.notespmdm.viewmodel.NotesViewModel

// Compose encargado de la navegación entre pantallas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationHost(notesViewModel: NotesViewModel) {

    val navController: NavHostController = rememberNavController()

    NavHost(navController, startDestination = Screens.NotesListScreen.route) {
        composable(Screens.NotesListScreen.route) {
            NotesListScreen(navController, notesViewModel)
        }
        // Obtiene el id de la nota por argumento
        composable(Screens.NoteDetailScreen.route + "/{id}") { note ->
            val noteId = note.arguments?.getString("id")
            noteId?.let {
                NoteDetailScreen(navController, notesViewModel, noteId.toLong())
            }
        }
    }
}