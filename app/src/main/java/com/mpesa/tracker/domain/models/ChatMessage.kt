package com.mpesa.tracker.domain.models

import java.util.UUID

/**
 * Represents a single message in the AI financial chat conversation.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatRole,
    val content: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)

enum class ChatRole {
    USER,   // Message typed by the user
    MODEL   // Response from Gemini
}
