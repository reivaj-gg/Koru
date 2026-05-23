---
name: offline-first-sync
description: "Local-first save, background sync, and conflict resolution patterns."
---

# Offline-First Sync

## The Principle
The local database (SQLDelight) is the single source of truth. Features must never fail or block the UI due to lack of network connectivity.

## The Flow
1. **Write Local**: All data is immediately inserted into SQLite.
2. **Reflect UI**: The UI reacts instantly because it observes a `Flow<List<T>>` directly from the database.
3. **Queue Sync**: A background coroutine or worker is dispatched to sync the data to the remote service.
4. **Handle Failure**: If the network fails, the local data remains. The sync queue is retried upon reconnection.

## Rules
- Never `await()` a network call to update the UI if the data can be persisted locally first.
- Catch network exceptions (e.g. `UnresolvedAddressException`) gracefully and log them without crashing.
