---
id: offline-first-sync
name: Offline-First Sync
description: Offline-first architecture using SQLDelight and Ktor for sync
---

# Offline-First Sync

This project enforces an offline-first architecture for all data operations.

## Rules

1. **Local persistence is immediate:** Any data captured (traces, audio, etc.) MUST be saved to the local database (SQLDelight) first.
2. **Network operations are asynchronous:** API calls (Ktor) and synchronization happen in the background and must NOT block the UI or local saving.
3. **Graceful degradation:** If there is no connection or the API timeouts, the local operation still succeeds, and the sync is queued for later.
4. **Context retrieval:** Network operations that require historical context must query the local database to build the payload (e.g. FTS5 to get recent traces) before sending to the remote server.

## Sync Pattern

```kotlin
suspend fun saveTrace(trace: Trace): Result<String> {
    return runCatching {
        // 1. Save locally FIRST
        localRepository.save(trace)
        
        // 2. Schedule async sync (do not await or block here)
        syncManager.scheduleSync(trace.id)
        
        // 3. Return success immediately
        trace.id
    }
}
```
