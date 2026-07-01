package edu.damianmassarelli.notespmdm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Modelo de Tabla/Objeto Note
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val idNote: Long = 0,
    var title: String = "",
    var description: String = "",
    val date: String = ""
)
