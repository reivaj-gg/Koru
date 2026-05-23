---
name: kmp-architecture
description: "Module structure, layer boundaries, and Clean Architecture rules in Kotlin Multiplatform."
---

# KMP Architecture

## The Principle
Koru follows Clean Architecture adapted for Kotlin Multiplatform. The goal is maximum code sharing (`commonMain`) while preserving strict boundaries between data, business logic, and UI.

## Layers
1. **Domain (`domain/`)**: The core. Pure Kotlin. Zero platform dependencies. Contains `model/`, `repository/` (interfaces only), and `usecase/`. 
   *Rule: Never import from `data` or `presentation`.*
2. **Data (`data/`)**: Infrastructure. Contains `local/` (SQLDelight), `remote/` (Ktor), and `repository/` (implementations of domain interfaces).
   *Rule: Only depends on `domain`.*
3. **Presentation (`presentation/`)**: UI and state. Contains `viewmodel/` (MVI) and `screens/` (Compose).
   *Rule: Only depends on `domain`. Never calls a repository directly.*

## Code Sharing
Maximize `commonMain`. Use `expect/actual` only when interacting with platform-specific hardware (e.g., microphones) or OS features (e.g., file paths, notifications).
