package edu.damianmassarelli.notespmdm.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.damianmassarelli.notespmdm.R
import edu.damianmassarelli.notespmdm.data.model.Note
import edu.damianmassarelli.notespmdm.viewmodel.NotesViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Composable encargado de pintar la pantalla de detalles
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(navController: NavController, notesViewModel: NotesViewModel, noteId: Long) {
    var note by remember { mutableStateOf<Note?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    // Obtiene la nota seleccionada y recompone la UI al recuperarla
    LaunchedEffect(note) {
        note = notesViewModel.getNoteById(noteId).await()
        note?.let {
            title = it.title
            desc = it.description
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    if (note == null) {
                        stringResource( R.string.txt_opAddNote)
                    } else {
                        title
                    }

                ) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Atras")
                    }
                },
                actions = {
                    NotesDetailAppBarMenu(
                        if (note == null) true else false,
                        error,
                        onSave = {
                            if (!title.isEmpty()) {
                                val format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                val date = LocalDateTime.now().format(format)
                                note = Note(0, title, desc, date)
                                notesViewModel.saveNote(note!!)
                                navController.popBackStack()
                            } else {
                                error = true
                            }
                        },
                        onEdit = {
                            if (!title.isEmpty()) {
                                note?.let {
                                    it.title = title
                                    it.description = desc
                                    notesViewModel.saveNote(note!!)
                                    navController.popBackStack()
                                }
                            } else {
                                error = true
                            }
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
        bottomBar = {
            BottomBar()
        }
    ) { innerpadding ->
        Column(
            modifier = Modifier.padding(innerpadding).fillMaxSize()
        ) {
            OutlinedTextField(
                value = title,
                label = { Text(stringResource(R.string.txt_titleNote)) },
                onValueChange = {
                    title = it
                    if (title.isEmpty()) {
                        error = true
                    } else {
                        error = false
                    }
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                isError = error,
            )
            if (error) {
                Text(
                    stringResource(R.string.txt_errorTitle), color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            OutlinedTextField(
                value = desc,
                label = { Text(stringResource(R.string.txt_descriptionNote)) },
                onValueChange = { desc = it },
                modifier = Modifier.padding(8.dp).fillMaxWidth().height(300.dp),
            )

        }
    }
}