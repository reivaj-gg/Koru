---
id: kmp-architecture
name: KMP Architecture
description: Module structure, layer boundaries, and Clean Architecture in Koru
---

# KMP Architecture

This project follows Clean Architecture + MVI. All code is structured into layers.

## Layers

- **Domain (`commonMain/kotlin/com/koru/domain`)**
  - Contains pure data classes, Repository interfaces, and UseCases.
  - Zero dependencies on external frameworks, UI, or Data implementations.
  - All data classes MUST validate their inputs using `init { require(...) }`.

- **Data (`commonMain/kotlin/com/koru/data`)**
  - Contains Repository implementations, local databases (SQLDelight), and networking (Ktor).
  - Only imports from `domain`.

- **Presentation (`commonMain/kotlin/com/koru/presentation`)**
  - Contains ViewModels (MVI), Screens (Compose Multiplatform), and UI logic.
  - Only imports from `domain` via UseCases. ViewModels must NOT import Repositories directly.

## Cross-layer Imports
- `domain` imports NOTHING from `data` or `presentation`.
- `data` imports from `domain` only.
- `presentation` imports from `domain` only.

## Expect/Actual
Use `expect/actual` only for APIs that have no KMP-native alternative (e.g., `AudioRecord` vs `AVAudioRecorder`). Everything else goes to `commonMain`.
