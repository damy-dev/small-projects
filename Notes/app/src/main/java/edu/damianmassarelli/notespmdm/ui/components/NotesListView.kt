package edu.damianmassarelli.notespmdm.ui.components

import edu.damianmassarelli.notespmdm.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.damianmassarelli.notespmdm.data.model.Note
import edu.damianmassarelli.notespmdm.ui.Screens
import edu.damianmassarelli.notespmdm.ui.theme.LightRed
//import edu.damianmassarelli.notespmdm.ui.theme.LightRed
import kotlinx.coroutines.delay

// Composable que pinta la lista de notas, con los métodos
// para clickear y borrar
@Composable
fun NotesList(notes: List<Note>, navController: NavController,
              modifier: Modifier, onNoteDelete: (Note) -> Unit) {

    val ctxt = LocalContext.current

    if (notes.isEmpty()) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(ctxt.getString(R.string.txt_noNotes))
        }
    }
    LazyColumn(
        modifier = modifier) {
        items(notes) { note ->
            NoteCard(
                note,
                {
                    navController.navigate(Screens.NoteDetailScreen.route + "/" + note.idNote)
                },
                { onNoteDelete(note) }
            )
        }
    }
}

// Composable que pinta cada elemento de la lista
@Composable
fun NoteCard(note: Note, onNoteClick: () -> Unit, onNoteDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        onClick = onNoteClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(8.dp))
        {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    note.title,
                    modifier = Modifier.padding(8.dp),
                    fontSize = 22.sp
                )
                IconButton(
                    onClick = { onNoteDelete() }
                ) {
                    Icon(Icons.Filled.Delete, "Eliminar", tint = LightRed)
                }
            }
            Text(
                note.date,
                modifier = Modifier.padding(8.dp).fillMaxSize(),
                textAlign = TextAlign.End
            )
        }
    }
}