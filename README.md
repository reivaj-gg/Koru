# Koru

**Koru** is the sample application for the **Kotlin Foundation Kotlin Multiplatform Contest** starter kit: a metacognitive “inner world” journal where you capture **traces** (how you interpreted events), and on-device history plus AI help surface patterns over time.

The name *Koru* refers to the Māori symbol of an unfurling fern — growth and new beginnings.

## Current status

This repository starts from the **JetBrains Compose Multiplatform wizard** template (`org.example.project`). The **product scope, architecture, and stack** are defined in:

| Document | Purpose |
|---|---|
| [`PRD.md`](./PRD.md) | Product requirements and MVP acceptance criteria (source of truth for implementation) |
| [`AGENTS.md`](./AGENTS.md) | AI and human contributor rules — SDD workflow, Clean Architecture + MVI, stack constraints |
| [`koru-ideaSummary.md`](./koru-ideaSummary.md) | Ideation narrative and resolved product questions |

Implementation of Koru (SQLDelight, Ktor + Gemini, tree Canvas, notifications, etc.) is **in progress** relative to those documents.

## Requirements

- **JDK 17+** (Gradle / AGP 9; module JVM target is 11)
- **Android Studio** or IntelliJ with KMP plugins for Android development
- **macOS + Xcode** for running the iOS app and `iosSimulatorArm64` tests

## Build and run — Android

From the repository root:

**macOS / Linux**

```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

**Windows**

```powershell
.\gradlew.bat :composeApp:assembleDebug
.\gradlew.bat :composeApp:installDebug
```

Run configurations are also available from the IDE toolbar.

## Build and run — iOS

Open the [`iosApp`](./iosApp) directory in **Xcode** and run the app on a simulator or device. Shared Kotlin UI lives in `composeApp`; the iOS target hosts the Compose embedding entry point.

## Tests

```bash
./gradlew :composeApp:testDebugUnitTest
./gradlew :composeApp:allTests
```

On **macOS** with Xcode installed:

```bash
./gradlew :composeApp:iosSimulatorArm64Test
```

Continuous Integration (Android assemble + unit tests) runs on **GitHub Actions** — see [`.github/workflows/ci.yml`](./.github/workflows/ci.yml).

## Secrets and API keys

Do **not** commit Gemini or other API keys. Use a local `.env` or `local.properties` entry that is **gitignored** — see `PRD.md` risks and `.gitignore`.

## License

This project is licensed under the **MIT License** — see [`LICENSE`](./LICENSE).

## Contributing

Use **issues and small pull requests** as described in `AGENTS.md` (section **GitHub & professional collaboration**). A PR template is provided under `.github/pull_request_template.md`.

---

*Kotlin Multiplatform Contest Starter Kit · Kotlin Foundation · 2026*
