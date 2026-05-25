package com.koru.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Root request object for Gemini API.
 */
@Serializable
data class GeminiRequest(
    /** List of message contents. */
    val contents: List<Content>,
    /** Optional system instructions to guide the model. */
    val systemInstruction: Content? = null,
) {
    init {
        require(contents.isNotEmpty()) { "contents must not be empty" }
    }
}

/**
 * Content block containing parts.
 */
@Serializable
data class Content(
    /** List of parts in this content block. */
    val parts: List<Part>,
) {
    init {
        require(parts.isNotEmpty()) { "parts must not be empty" }
    }
}

/**
 * A single part of a content block.
 */
@Serializable
data class Part(
    /** The text content of this part. */
    val text: String,
) {
    init {
        require(text.isNotBlank()) { "text must not be blank" }
    }
}

/**
 * Root response object from Gemini API.
 */
@Serializable
data class GeminiResponse(
    /** List of candidates returned by the model. */
    val candidates: List<Candidate>? = null,
) {
    /**
     * Extracts the first text part from the first candidate.
     */
    fun getText(): String? = candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
}

/**
 * A candidate response from the model.
 */
@Serializable
data class Candidate(
    /** The content of the candidate. */
    val content: Content,
)
