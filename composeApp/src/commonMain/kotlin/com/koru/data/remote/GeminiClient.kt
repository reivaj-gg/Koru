package com.koru.data.remote

import com.koru.data.remote.dto.Content
import com.koru.data.remote.dto.GeminiRequest
import com.koru.data.remote.dto.GeminiResponse
import com.koru.data.remote.dto.Part
import com.koru.domain.model.InsightType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Client for interacting with the Gemini API using Ktor.
 */
class GeminiClient(
    private val httpClient: HttpClient,
    private val apiKey: String,
) {
    /**
     * Sends a prompt and context to the Gemini API to generate an insight.
     *
     * @param context The formatted string of the user's past traces.
     * @param type The type of insight being requested.
     * @return A [Result] containing the raw response text on success, or an exception on failure.
     */
    suspend fun generateInsight(
        context: String,
        type: InsightType,
    ): Result<String> =
        runCatching {
            val systemPrompt =
                """
                You are a metacognitive counselor with deep knowledge of this person's history.
                
                RULES — never violate:
                1. Never diagnose. Never label the user with clinical or psychological terms.
                2. Never give generic advice. Every sentence must reference the provided context.
                3. End every response with exactly one facilitative question.
                4. Tone: calm, direct, caring. Not clinical. Not cheerful.
                5. Max response length: 3 sentences + 1 question.
                """.trimIndent()

            val prompt = "Based on these recent traces, generate an insight of type $type:\n\n$context"

            val requestPayload =
                GeminiRequest(
                    contents =
                        listOf(
                            Content(parts = listOf(Part(text = prompt))),
                        ),
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                )

            val response =
                httpClient.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey") {
                    contentType(ContentType.Application.Json)
                    setBody(requestPayload)
                }

            if (response.status.value in 200..299) {
                val body = response.body<GeminiResponse>()
                body.getText() ?: throw Exception("Empty text returned from Gemini")
            } else {
                throw Exception("HTTP Error: ${response.status.value}")
            }
        }
}
