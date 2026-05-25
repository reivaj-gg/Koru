package com.koru.data.remote

import com.koru.domain.model.InsightType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeminiClientTest {
    private fun buildMockClient(
        responseJson: String,
        statusCode: HttpStatusCode = HttpStatusCode.OK,
    ): HttpClient {
        val mockEngine =
            MockEngine { request ->
                if (statusCode == HttpStatusCode.OK) {
                    respond(
                        content = responseJson,
                        status = statusCode,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                } else {
                    respondError(statusCode)
                }
            }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun given_valid_response_when_generateInsight_then_returns_insight_text() =
        runTest {
            val mockResponse =
                """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "This is a great insight."
                          }
                        ]
                      }
                    }
                  ]
                }
                """.trimIndent()
            val client = GeminiClient(buildMockClient(mockResponse), "fake-api-key")

            val result = client.generateInsight("Some context", InsightType.IMMEDIATE)
            assertTrue(result.isSuccess)
            assertEquals("This is a great insight.", result.getOrNull())
        }

    @Test
    fun given_http_error_when_generateInsight_then_returns_failure() =
        runTest {
            val client = GeminiClient(buildMockClient("", HttpStatusCode.InternalServerError), "fake-api-key")
            val result = client.generateInsight("context", InsightType.IMMEDIATE)
            assertTrue(result.isFailure)
        }
}
