package edu.damianmassarelli.notespmdm

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.damianmassarelli.notespmdm.data.repository.Repository
import edu.damianmassarelli.notespmdm.ui.NavigationHost
import edu.damianmassarelli.notespmdm.ui.Screens
import edu.damianmassarelli.notespmdm.ui.components.BottomBar
import edu.damianmassarelli.notespmdm.ui.components.NotesList
import edu.damianmassarelli.notespmdm.ui.components.NotesListAppBarMenu
import edu.damianmassarelli.notespmdm.ui.theme.NotesPMDMTheme
import edu.damianmassarelli.notespmdm.viewmodel.NotesViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        var modeDark = mutableStateOf(true)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

// Composable que obtiene la instancia del ViewModel y llama la
// navegación entre pantalla
@Composable
fun MainScreen() {
    val notesViewModel: NotesViewModel = viewModel()

    NotesPMDMTheme(MainActivity.modeDark.value) {
        NavigationHost(notesViewModel)
    }
}

// Composable encargado de pintar la pantalla de la lista de notas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(navController: NavController, notesViewModel: NotesViewModel) {
    val ctxt = LocalContext.current
    val noteList by notesViewModel.currentNotes.collectAsState()
    var azOrder by rememberSaveable { mutableStateOf(true) }
    val snackBar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBar) },
        topBar = {
            TopAppBar(
                title = { Text(ctxt.getString(R.string.app_name)) },
                actions = {
                    NotesListAppBarMenu(
                        azOrder,
                        {
                            notesViewModel.loadAllNotes(Repository.SortOrder.AZ)
                            azOrder = true
                        },
                        {
                            notesViewModel.loadAllNotes(Repository.SortOrder.ZA)
                            azOrder = false
                        }
                    )
                },
                expandedHeight = 80.dp,
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.NoteDetailScreen.route + "/-1") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(Icons.Default.Add, "Añadir")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            BottomBar()
        }
    ) { innerpadding ->
        NotesList(
            noteList,
            navController,
            Modifier.padding(innerpadding).fillMaxSize(),
            {
                note -> notesViewModel.deleteNote(note)
                scope.launch {
                    val result = snackBar.showSnackbar(
                        ctxt.getString(R.string.txt_noteDeleted, note.title),
                        ctxt.getString(R.string.txt_undo),
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        notesViewModel.saveNote(note)
                    }
                }
            }
        )
    }
}