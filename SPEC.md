# SPEC: Koru — Technical Specification
**Status:** Approved
**Scope:** Core Offline-First Layer & Architecture

## Data Models

### `Trace`
Atomic unit of capture.
```kotlin
data class Trace(
    val id: String,
    val content: String,
    val context: String?,
    val capturedAt: Instant,
    val emotionTag: EmotionTag?,
) {
    init {
        require(id.isNotBlank()) { "Trace id must not be blank" }
        require(content.isNotBlank()) { "Trace content must not be blank" }
    }
}
```

### `EmotionTag` (Enum)
`TENSION`, `SURPRISE`, `CLARITY`, `RESISTANCE`, `GRATITUDE`

## Repository Interfaces

### `TraceRepository`
Single source of truth for Trace persistence.
```kotlin
interface TraceRepository {
    fun observeAll(): Flow<List<Trace>>
    suspend fun save(trace: Trace): Result<String>
    suspend fun search(semanticQuery: String, limit: Int = 20): Result<List<Trace>>
}
```

## ViewModel Contracts (MVI)

### `HomeState`
```kotlin
data class HomeState(
    val traces: List<Trace> = emptyList(),
    val isCapturing: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### `HomeIntent`
```kotlin
sealed class HomeIntent {
    data object OpenCapture : HomeIntent()
    data class SaveTrace(val content: String) : HomeIntent()
    data class TapNode(val traceId: String) : HomeIntent()
}
```

### `HomeEffect`
```kotlin
sealed class HomeEffect {
    data object TraceSaved : HomeEffect()
    data class NavigateToNode(val traceId: String) : HomeEffect()
    data class ShowError(val message: String) : HomeEffect()
}
```

## Business Rules
1. **Offline-first:** All traces must be persisted to the local SQLDelight database immediately before any network interaction.
2. **Immutability:** Traces cannot be edited once captured.
3. **Data Integrity:** Blank traces are rejected at the Domain layer (`init` block).
