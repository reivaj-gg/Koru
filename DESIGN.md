# DESIGN: Koru Architecture
**Status:** Approved
**Pattern:** Clean Architecture + MVI

## Directory Structure
```
commonMain/kotlin/com/koru/
├── domain/        # Pure Kotlin logic. No platform dependencies.
│   ├── model/
│   ├── repository/
│   └── usecase/
├── data/          # SQLDelight, Ktor, Repository implementations.
│   ├── local/
│   ├── remote/
│   └── repository/
└── presentation/  # MVI ViewModels, Compose UI.
    ├── viewmodel/
    ├── screens/
    └── components/
```

## Dependency Injection (Koin)
- Single entry point: `KoinModules.kt` in `commonMain`.
- Constructor injection used for all `ViewModel`s, `UseCase`s, and `RepositoryImpl`s.
- Driver factory is injected via expect/actual mechanism per platform (`DatabaseDriverFactory`).

## Expect/Actual Boundaries
Used strictly when no KMP equivalent exists.
- **Audio Capture:** `NativeAudioRecorder` (Android: `AudioRecord`, iOS: `AVAudioRecorder`).
- **Permissions:** `MicrophonePermissionHelper`.
- **Database Driver:** `DatabaseDriverFactory`.

## Data Flow (Offline-First)
1. User dispatches `HomeIntent.SaveTrace`.
2. `HomeViewModel` delegates to `SaveTraceUseCase`.
3. `SaveTraceUseCase` validates and calls `TraceRepository.save()`.
4. `TraceRepositoryImpl` persists directly to `SQLDelight`.
5. (Asynchronous/Deferred) The AI sync is triggered separately.
6. The UI automatically updates via the `TraceRepository.observeAll()` Flow.
