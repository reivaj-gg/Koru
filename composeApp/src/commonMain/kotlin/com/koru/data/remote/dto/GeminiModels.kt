package com.koru.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
)

@Serializable
data class Content(
    val parts: List<Part>,
)

@Serializable
data class Part(
    val text: String,
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
) {
    fun getText(): String? = candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
}

@Serializable
data class Candidate(
    val content: Content,
)
