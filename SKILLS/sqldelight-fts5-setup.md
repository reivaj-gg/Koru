---
id: sqldelight-fts5-setup
name: SQLDelight FTS5 Setup
description: FTS5 virtual tables and search in SQLDelight
---

# SQLDelight FTS5

For full-text search, we use SQLDelight's FTS5 extension instead of standard `LIKE` queries. This is mandatory for generating context for the AI.

## Table Definition

```sql
CREATE VIRTUAL TABLE TraceFts USING fts5(
    id UNINDEXED,
    content,
    emotionTag,
    tokenize='porter'
);
```

## Insertion / Triggers

Always keep the FTS table synced with the main table using triggers:

```sql
CREATE TRIGGER after_trace_insert
AFTER INSERT ON TraceEntity
BEGIN
    INSERT INTO TraceFts (id, content, emotionTag)
    VALUES (new.id, new.content, new.emotionTag);
END;
```

## Query Pattern

Search using the `MATCH` operator to find relevant text quickly. This is done before calling the Gemini API to retrieve local context.

```sql
searchFts:
SELECT TraceEntity.*
FROM TraceFts
JOIN TraceEntity ON TraceEntity.id = TraceFts.id
WHERE TraceFts MATCH :query
ORDER BY TraceEntity.capturedAt DESC
LIMIT :limit;
```
