# PRD: Koru — Metacognitive Trace Journal
**Status:** Approved
**Serves criterion:** All — Creativity (40%) · KMP Usage (40%) · Kotlin Conventions (20%)
**Author:** Javier Quilumba
**Date:** 2026-05-12

---

## Overview

Koru is a cross-platform mobile application and the official sample project of the
**Kotlin Foundation KMP Contest Starter Kit**. It demonstrates production-grade
Kotlin Multiplatform patterns through a purposeful, emotionally resonant use case:
metacognitive self-awareness.

> **One sentence:** A second brain for your inner world — you capture how you react
> to the world, and AI detects your thinking patterns over time.

The name *Koru* comes from the Māori symbol of an unrolling silver fern frond,
representing growth, new beginnings, and the potential of the future.

---

## What

Koru enables users to capture **traces** — quick records of their reactions,
interpretations, and emotional responses to daily events — not the events themselves.
Over time, an on-device AI analysis layer surfaces metacognitive patterns:
recurring thought habits, emotional triggers, and belief structures the user may not
consciously recognize.

The core experience is built around three loops:

1. **Capture loop** — Record a trace in under 10 seconds (voice or text).
2. **Insight loop** — AI processes the trace and returns a short, personalized reflection.
3. **Growth loop** — The tree visualization grows with every trace; patterns emerge across weeks.

### The Philosophical Foundation

These are not decorative concepts. They are the product decisions behind every screen.

| Concept | Product implication |
|---|---|
| **Engram** | The minimum unit of capture is not *what happened* but *how I interpreted it* |
| **90/10 Principle** | 10% is the event, 90% is the meaning we give it. Koru captures that 90%. |
| **Metacognition** | Thinking about your thinking. The scientific foundation of the insight engine. |
| **Reframing** | The AI never tells users what to feel — it offers alternative perspectives. |
| **Transgenerational Legacy** | Traces and patterns are exportable as a personal legacy document. |

---

## Why

### User Problem

Existing tools solve adjacent problems but not this one:

| Category | Examples | Gap |
|---|---|---|
| Second brain apps | Notion, Obsidian, Reflect | Store external knowledge. Do not learn about the user. |
| Metacognition apps | Ahead, Animi, InsightApp | Generic CBT exercises. No personal history. No pattern detection. |
| Journaling apps | Day One, Journey | Passive text records. No AI insight. 80% abandonment in 7 days. |

**Nobody has built:** a second brain that stores your inner world, learns from your
specific reactions, and returns personalized insights based on your own accumulated history.

### Grant Alignment

| Judging Criterion | Weight | How Koru delivers |
|---|---|---|
| Creativity & novelty | 40% | No comparable app exists. Scientific grounding (metacognition, FTS5 local filter) adds rigor. |
| KMP & shared code | 40% | All business logic, AI integration, and UI live in `commonMain`. Platform code limited to microphone and notification bridges. |
| Kotlin Conventions | 20% | Strict adherence enforced via AGENTS.md, ktlint, and KDoc requirements on every public member. |

### Educational Value for Contest Participants

Koru demonstrates the KMP patterns most contestants will need:
- Offline-first architecture with SQLDelight
- AI service integration via Ktor (Gemini API)
- Audio capture via `expect/actual` (microphone bridge)
- MVI state management with Compose Multiplatform
- Local push notifications across Android and iOS
- Preference storage with Multiplatform Settings

---

## Core Features (MVP Scope)

### Feature 1 — Trace Capture
A trace is the atomic unit of Koru. Every other feature builds on it.

**Primary gesture (voice):**
1. User opens Koru → full-screen dark canvas with a breathing circle appears.
2. User holds the circle and speaks their reaction (max 90 seconds).
3. User releases → the audio is transcribed locally, and the trace flies as a node into the tree.

**Fallback gesture (text):**
- Same screen, same visual weight as voice. Not a second-class experience.
- User types their reaction and taps send.

**Captured data per trace:**
- `id`: UUID
- `content`: transcribed text or raw text input
- `capturedAt`: `Instant` (kotlinx-datetime)
- `emotionTag`: optional enum (`TENSION`, `SURPRISE`, `CLARITY`, `RESISTANCE`, `GRATITUDE`)
- `context`: optional free-text tag (e.g., "work", "family", "commute")

---

### Feature 2 — AI Insight Engine
After every trace is saved, a Gemini API call is dispatched asynchronously.

**Local FTS5 filter (mandatory before every AI call):**
```kotlin
val relevantTraces = database.traceQueries
    .searchFts(semanticQuery)
    .executeAsList()
    .take(MAX_CONTEXT_TRACES) // = 20
```

**Insight types:**
- `IMMEDIATE` — A single reflection returned seconds after capture.
- `PATTERN` — Surfaced after 7+ traces; describes a recurring thought habit.
- `REFRAME` — An alternative perspective on the captured reaction.

**AI System Prompt (immutable contract):**
```
You are a metacognitive counselor with deep knowledge of this person's history.

RULES — never violate:
1. Never diagnose. Never label the user with clinical or psychological terms.
2. Never give generic advice. Every sentence must reference the provided context.
3. End every response with exactly one facilitative question.
4. Tone: calm, direct, caring. Not clinical. Not cheerful.
5. Max response length: 3 sentences + 1 question.
```

---

### Feature 3 — The Tree Visualization
The tree is the emotional center of the app, not just a dashboard.

| Element | Meaning |
|---|---|
| Trunk | The user — the constant across all contexts |
| Roots | User-defined contexts (e.g., "Work", "Family", "Self") |
| Branches | Individual traces, positioned by date and context |
| Leaves | AI insights attached to each trace node |
| State | Tree pulses and grows with each new capture; hibernates after 14 days of inactivity |

Implementation: **Compose Canvas** in `commonMain`. No platform-specific rendering.

---

### Feature 4 — Engagement & Retention System
The journaling abandonment problem is solved with two complementary mechanisms:

**Phase 1 — Habit Stacking (Days 1–7):**
- During onboarding, the user picks an anchor habit (morning coffee, before sleep, commute).
- Koru schedules a local notification at that time daily.
- Notification copy is never generic: it reflects the user's stated anchor.

**Phase 2 — AI-Driven Insight Notifications (7+ traces):**
- Once the AI has enough data, notifications become intelligent.
- Example: *"Three weeks ago you captured something that connects with what happened today."*
- These notifications are generated asynchronously and delivered via local push.
- Threshold: minimum 7 traces before AI pattern notifications activate.

---

### Feature 5 — Offline-First Architecture
Every feature must work without a network connection.

- All traces are written to SQLDelight immediately on capture.
- AI calls are queued when offline and processed when connectivity is restored.
- The tree renders entirely from local data.
- Preferences (notification time, onboarding state) stored in Multiplatform Settings.

---

## Out of Scope (MVP)

The following are **explicitly excluded** from the initial release.
They are documented here so contestants understand what v2 looks like.

| Feature | Reason for deferral |
|---|---|
| Group / shared trees | Requires multi-user auth, real-time sync, and privacy controls — 3+ weeks of extra scope |
| Web target | Compose Multiplatform Web is experimental; deferred to reduce risk |
| Audio playback of past traces | Storage and UX complexity; text transcription is sufficient for MVP |
| Export / legacy document | Meaningful only after significant trace history exists |
| Onboarding tutorial | Replaced by progressive disclosure via the breathing circle interaction |

---

## Technical Requirements

| Requirement | Decision |
|---|---|
| Platforms | Android (primary), iOS (primary) |
| Minimum Android SDK | 26 (Android 8.0) |
| Minimum iOS | 16.0 |
| Build system | AGP 9 + Kotlin 2.2+ |
| UI framework | Compose Multiplatform 1.10+ — all UI in `commonMain` |
| Local database | SQLDelight 2.x — all queries in `.sq` files |
| Networking | Ktor Client — no OkHttp or Retrofit |
| AI service | Gemini API via Ktor |
| DI | Koin KMP — constructor injection throughout |
| Navigation | Navigation 3 (Compose Multiplatform) |
| Date/Time | kotlinx-datetime — no java.time |
| Preferences | Multiplatform Settings (russhwolf) |
| Image loading | Coil 3 |
| Logging | Napier — no println in production |
| Serialization | kotlinx.serialization |
| Testing | kotlin.test + Turbine + Ktor MockEngine |
| License | MIT |

---

## Acceptance Criteria

### Capture
- [ ] A voice trace can be recorded and saved in under 10 seconds on a cold start.
- [ ] A text trace can be saved in under 10 seconds on a cold start.
- [ ] Traces are persisted to SQLDelight and available offline immediately after capture.
- [ ] `Trace.content` and `Trace.capturedAt` are validated in `init {}` — blank content throws `IllegalArgumentException`.

### AI Insight
- [ ] An `IMMEDIATE` insight is returned within 5 seconds on a stable connection.
- [ ] FTS5 local filter runs before every Gemini API call — verified by unit test.
- [ ] AI errors (timeout, no connection) surface as typed `InsightError` states — never crash.
- [ ] The system prompt is never modified by user input or runtime state.

### Tree Visualization
- [ ] The tree renders correctly with 0 traces (empty state).
- [ ] The tree renders correctly with 100+ traces without frame drops on a mid-range device.
- [ ] Tapping a node displays the trace content and its associated insight.
- [ ] The tree enters `HIBERNATING` visual state after 14 days of inactivity.

### Retention
- [ ] Onboarding sets a notification anchor in under 3 steps.
- [ ] Local notification fires at the configured time on both Android and iOS.
- [ ] AI pattern notifications do not activate before 7 traces exist — verified by unit test.

### Architecture
- [ ] Zero imports from `data` or `presentation` in any `domain` class.
- [ ] Zero business logic in any `@Composable` function.
- [ ] All public members have KDoc.
- [ ] `./gradlew :composeApp:allTests` passes green.
- [ ] `./gradlew :composeApp:iosSimulatorArm64Test` passes green.

---

## Risks

| Risk | Severity | Mitigation |
|---|---|---|
| Gemini API latency > 5s on insight delivery | Medium | Show optimistic UI immediately; insight appears when ready |
| Audio transcription accuracy on noisy input | Medium | Display transcription for user confirmation before saving |
| Compose Canvas tree performance with 100+ nodes | Medium | Virtualize off-screen nodes; benchmark early in sdd-apply |
| iOS microphone `expect/actual` complexity | High | Spike this in the first task of sdd-apply; unblock rest of work |
| User abandonment before 7-trace threshold | High | Habit stacking Phase 1 must feel meaningful on its own |
| Gemini API key exposure in open source repo | High | Keys loaded from local `.env` file excluded via `.gitignore`; documented in README |

---

## Next Steps

```
PRD (this document) ✅
     ↓
sdd-spec  → Define Trace data model, repository interfaces, ViewModel contracts
     ↓
sdd-design → Module structure, Koin wiring, expect/actual boundaries, data flow
     ↓
sdd-tasks  → Atomic task breakdown with risk + done-when definitions
     ↓
sdd-apply  → Red → Green → Refactor, task by task
```

---

*Koru — Official KMP Contest Starter Kit*
*Kotlin Foundation · 2026*
