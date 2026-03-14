package com.mpesa.tracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String, // e.g., "Groceries", "Utilities", "Transport"
    val colorCode: String, // Hex color for the UI charts
    val iconName: String // Identifier for Material Icons
)
