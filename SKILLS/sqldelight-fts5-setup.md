---
name: sqldelight-fts5-setup
description: "Setting up FTS5 virtual tables and SQLDelight queries for offline-first full-text search."
---

# SQLDelight & FTS5

## The Principle
Before making AI calls, Koru filters data locally using SQLite FTS5 (Full-Text Search) to respect privacy, reduce token costs, and guarantee offline capabilities.

## Setup
1. **Schema Definition**: Define standard tables in `.sq` files.
2. **Virtual Table**: Create an FTS5 virtual table synchronized with the main table via triggers.

```sql
CREATE VIRTUAL TABLE TraceFts USING fts5(
    id UNINDEXED,
    content,
    tokenize="unicode61"
);

CREATE TRIGGER trace_ai AFTER INSERT ON TraceEntity BEGIN
  INSERT INTO TraceFts(id, content)
  VALUES (new.id, new.content);
END;
```

## Querying
Use the `MATCH` operator for fast semantic pre-filtering.
```sql
searchFts:
SELECT TraceEntity.*
FROM TraceFts
JOIN TraceEntity ON TraceEntity.id = TraceFts.id
WHERE TraceFts.content MATCH :query
ORDER BY capturedAt DESC
LIMIT :limit;
```
