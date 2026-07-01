package edu.damianmassarelli.notespmdm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.damianmassarelli.notespmdm.MainActivity
import edu.damianmassarelli.notespmdm.R

// Composable encargado de pintar el menú de la TopBar de la pantalla principal
// (Orden de la lista)
@Composable
fun NotesListAppBarMenu(stateMenu: Boolean, onAZ: () -> Unit, onZA: () -> Unit) {
    val ctxt = LocalContext.current

    if (stateMenu) {
        Text(
            ctxt.getString(R.string.txt_opSortZA),
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onZA) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Order")
        }
    } else {
        Text(
            ctxt.getString(R.string.txt_opSortAZ),
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onAZ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Order")
        }
    }
}

// Composable encargado de pintar el menu de la TopBar de la pantalla detalles
// (ícono guardar o editar)
@Composable
fun NotesDetailAppBarMenu(stateMenu: Boolean, error: Boolean,
                          onSave: () -> Unit = {}, onEdit: () -> Unit = {} ) {
    if (stateMenu) {
        IconButton(
            onClick = onSave,
            enabled = !error
        ) {
            Icon(Icons.Default.AddCircle, "Guardar")
        }
    } else {
        IconButton(
            onClick = onEdit,
            enabled = !error
        ) {
            Icon(Icons.Default.Create, "Guardar")
        }
    }
}

// Composable encargado de cambiar entre modo oscuro y modo light.
// (Función extra para experimentar con los modos)
@Composable
fun BottomBar() {
    val ctxt = LocalContext.current

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                ctxt.getString(R.string.txt_modeDark),
                modifier = Modifier.padding(8.dp)
            )
            Switch(
                checked = MainActivity.modeDark.value,
                onCheckedChange = { MainActivity.modeDark.value = !MainActivity.modeDark.value }
            )
        }
    }
}