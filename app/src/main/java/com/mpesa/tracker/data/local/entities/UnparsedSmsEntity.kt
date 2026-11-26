package com.mpesa.tracker.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Logs recognized financial-sender SMS that could not be parsed so users can inspect
 * them in Settings -> SMS Diagnostics without losing message provenance.
 */
@Entity(
    tableName = "unparsed_sms",
    indices = [Index(value = ["bodyHash"], unique = true)]
)
data class UnparsedSmsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String,
    val body: String,
    val bodyHash: String,
    val receivedAt: Long
)
