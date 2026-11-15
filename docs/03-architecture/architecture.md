# Architecture

## 1. Context

Number Garden is a single-activity Android app with a small, fixed feature set. The
architecture is deliberately plain: one Gradle module, manual dependency injection, pure
Kotlin session engines, Compose UI, and platform APIs for video and speech.

The goal is not to demonstrate architectural patterns. The goal is to be easy for one
caregiver-developer to audit, test, and change without introducing hidden behaviour.

```
┌─────────────────────────────────────────────────────────────┐
│ MainActivity                                                │
│  edge-to-edge, theme, Narrator, NavHost                     │
└───────────────┬─────────────────────────────────────────────┘
                │
┌───────────────▼─────────────────────────────────────────────┐
│ Compose UI                                                  │
│  screens/  components/  theme/  nav/                        │
│  StateFlow → collectAsStateWithLifecycle → immutable UI     │
└───────┬───────────────────────────────┬─────────────────────┘
        │ viewModel()                   │ LocalSpeech
┌───────▼──────────────┐        ┌───────▼─────────────────────┐
│ CountingViewModel    │        │ Narrator (platform TTS)     │
│ CompareViewModel     │        │  respects voice setting     │
└───────┬──────────────┘        └─────────────────────────────┘
        │ pure functions
┌───────▼─────────────────────────────────────────────────────┐
│ domain/                                                     │
│  CountingSession  CompareSession                            │
│  no Android imports; deterministic with injected Random     │
└───────┬─────────────────────────────────────────────────────┘
        │
┌───────▼─────────────────────────────────────────────────────┐
│ data/                                                       │
│  ContentRepository        assets/content.json + validation  │
│  PrefsProgressRepository  SharedPreferences + StateFlow     │
│  PrefsSettingsRepository  SharedPreferences + StateFlow     │
│  AssetVideoCache          assets/videos → cacheDir/videos   │
└───────┬─────────────────────────────────────────────────────┘
        │
┌───────▼─────────────────────────────────────────────────────┐
│ Platform                                                    │
│  VideoView + MediaPlayer, TextToSpeech, SharedPreferences   │
└─────────────────────────────────────────────────────────────┘
```

## 2. Module and package layout

Single Gradle module `:app`.

```
com.numbergarden.app
├── MainActivity.kt              edge-to-edge host, theme, narrator, nav
├── NumberGardenApplication.kt   creates AppContainer once
├── audio/Narrator.kt            Speech interface + TTS implementation
├── data/                        repositories + video cache
│   ├── ContentRepository.kt
│   ├── PrefsProgressRepository.kt
│   ├── PrefsSettingsRepository.kt
│   └── AssetVideoCache.kt
├── di/AppContainer.kt           manual composition root
├── domain/                      pure session engines
│   ├── CountingSession.kt
│   └── CompareSession.kt
├── model/                       serializable content + progress contracts
│   ├── Content.kt
│   ├── ContentValidator.kt
│   └── Progress.kt
├── ui/
│   ├── components/              scaffold, buttons, art, feedback, motion
│   ├── nav/NumberGardenNavHost.kt
│   ├── screens/                 one file per screen + two ViewModels
│   └── theme/                   colour, type, shape
└── util/NumberWords.kt          digits → English words
```

Rules of thumb:

- `domain/` and `model/` must not import Android framework classes.
- `data/` may import Android; it exposes plain Kotlin types and `StateFlow`.
- `ui/` may import Compose and Android; it does not read `SharedPreferences` directly.
- `MainActivity` is the only place that constructs the `Narrator` and provides it through
  `LocalSpeech`.

## 3. Startup

1. `NumberGardenApplication.onCreate()` creates `AppContainer` with the application context.
2. `MainActivity.onCreate()` enables edge-to-edge with transparent system bars, collects the
   settings `StateFlow`, and applies `NumberGardenTheme`.
3. It creates a `Narrator` keyed to the activity context, provides it via `LocalSpeech`, and
   disposes it when the composition leaves.
4. `NumberGardenNavHost` starts at `home`.
5. Home collects progress; no content is loaded until a child enters Watch, Count, Compare,
   Numbers, or a lesson player.

The app does not block startup on content parsing, TTS initialisation, or video copying.
Those happen lazily.

## 4. Navigation

`NumberGardenNavHost` uses Navigation Compose with string routes:

| Route | Screen | Arguments |
| --- | --- | --- |
| `home` | Home | — |
| `watch` | Watch list | — |
| `player/{lessonId}` | Lesson player | `lessonId` string |
| `count` | Counting | — |
| `compare` | Comparing | — |
| `numbers` | Number cards | — |
| `garden` | Garden | — |
| `grownups` | Parental gate | — |
| `settings` | Settings | — |

All four navigation transitions are `None`. Screen changes are instant cuts. The app never
uses a bottom bar, drawer, tab row, or gesture-only navigation.

`goHome` pops back to `home` if it exists, otherwise navigates with `launchSingleTop`. The
gate navigates to settings with `popUpTo(gate) { inclusive = true }`, so Back from settings
does not return to a solved gate.

## 5. State and data flow

### 5.1 Settings

`PrefsSettingsRepository` loads `SharedPreferences` once, exposes `StateFlow<AppSettings>`,
and writes each change immediately. `MainActivity` collects it to choose the theme;
screens collect it for behaviour (voice, sound, rounds).

`AppSettings` fields:

```kotlin
data class AppSettings(
    val voice: Boolean = true,
    val sound: Boolean = true,
    val reduceMotion: Boolean = false,
    val highContrast: Boolean = false,
    val roundsPerSession: Int = 5,
)
```

`roundsPerSession` is coerced to `1..5` on load and write. The UI offers 3 and 5; the
repository is tolerant of other stored values.

### 5.2 Progress

`PrefsProgressRepository` exposes `StateFlow<Progress>` and writes on each change:

- `completedLessons: Set<String>`
- `countingSessions: Int`
- `compareSessions: Int`

`Progress.blooms = (countingSessions + compareSessions).coerceAtMost(12)`. The cap is
computed, not stored, so it cannot drift.

`reset()` clears only the progress preferences file. Settings are untouched.

### 5.3 Sessions

`CountingViewModel` and `CompareViewModel` load content, build a session, and expose one
`State`:

```kotlin
sealed interface State {
    data object Loading : State
    data class Failed(val message: String) : State
    data class Ready(val ui: Ui) : State
}

data class Ui(
    val sessionId: Long,
    val game: CountingState, // or CompareState
)
```

`sessionId` changes on restart so one-shot effects (speech, completion) can key off it.

User actions call pure functions:

- `CountingSession.select(state, value)` returns a new `CountingState`.
- `CountingSession.next(state)` advances or completes.
- `CompareSession.choose(state, side)` and `CompareSession.next(state)` mirror this.

The ViewModel persists completion exactly once when the engine transitions to
`completed == true`. It records a session and exposes a new UI state. Restart builds a new
round list and increments `sessionId`.

### 5.4 Randomness

Session build functions take a `Random` parameter. Production passes `Random.Default`;
tests pass a seeded `Random`. This keeps round generation varied but reproducible under test.

## 6. Content loading and validation

`ContentRepository.load()`:

1. Returns a cached `ContentPack` if present (double-checked locking).
2. Opens `assets/content.json` and decodes it with strict `kotlinx.serialization`
   (`ignoreUnknownKeys = false`, `isLenient = false`).
3. Runs `ContentValidator.validate(pack)`.
4. Verifies every lesson's video asset can be opened.
5. Throws `ContentLoadException` with a human-readable message on any failure.
6. Caches the result.

Validation failures are not swallowed. A bad content pack produces the error screen rather
than a partially working activity.

The same validator runs in `ContentPackTest` against the shipped asset and checks that both
MP4 files exist and are larger than 10 KB.

## 7. Video playback

`LessonPlayerScreen`:

1. Loads the lesson from the content repository.
2. Copies the asset to `cacheDir/videos/<name>.mp4` on `Dispatchers.IO` via
   `AssetVideoCache.ensure`.
3. Creates a platform `VideoView` inside `AndroidView`.
4. Starts paused. The child must tap Play.
5. Tracks position on a 120 ms coroutine loop while playing and maps it to the active
   caption.
6. Speaks each new caption through `LocalSpeech` when voice is enabled.
7. Pauses on `ON_STOP` and clears `FLAG_KEEP_SCREEN_ON`.
8. Saves position across rotation with `rememberSaveable`.
9. Marks the lesson watched once on completion.

Why copy to cache: `MediaPlayer`/`VideoView` handling of `file:///android_asset` is
inconsistent across OEM devices. A real file path is more reliable. The copy is small
(≈ 320 KB per video) and happens once per install/cache eviction.

The player uses `VideoView` rather than Media3/ExoPlayer to avoid a large dependency and
because the media is local, short, and simple. See
[ADR 0004](adr/0004-platform-video.md).

## 8. Speech

`Speech` is a two-method interface (`say`, `stop`) with a no-op implementation for tests and
previews. `Narrator` wraps `TextToSpeech`:

- Initialises asynchronously; if `say` arrives before ready, it stores one pending line.
- Checks the voice setting on every `say`; turning voice off therefore stops future speech.
- Uses `QUEUE_FLUSH` by default, `QUEUE_ADD` when counting apples so each ordinal can
  finish.
- Rate 0.88, pitch 1.0, `Locale.US`.
- Fails silently. No UI depends on speech succeeding.
- Releases the engine in `onDispose`.

TTS is intentionally not used for the video's soundtrack; the video has its own quiet chime
bed and burned-in captions. The app layer only speaks the timed caption text.

## 9. Error handling

| Failure | Behaviour |
| --- | --- |
| Content JSON missing/invalid | `ContentErrorScreen` with message, retry, home |
| Video asset missing | `ContentErrorScreen` on load; if playback fails, "This video is not ready." |
| TTS unavailable | Silent; no error UI |
| Video decode/playback error | Video area stays black; controls remain; back/home work |
| Progress prefs unreadable | Defaults to empty progress |
| Settings prefs unreadable | Defaults to calm settings |

There are no retry loops, exponential backoff, or background jobs. Every failure is local
and terminates in a screen where the child or caregiver can go home.

## 10. Threading

- Content decoding and asset copying run off the main thread (`Dispatchers.IO`).
- Session engines are pure and run on the caller's thread.
- `SharedPreferences` writes use `apply()`; state is updated in memory first.
- No custom dispatchers, WorkManager, or background services.
- The caption loop is a `LaunchedEffect` scoped to the player composition and cancelled
  when it leaves.

## 11. Lifecycle and configuration

- The activity is not locked to portrait; rotation is supported.
- The video position uses `rememberSaveable`, so rotation resumes near the last position.
- Playback pauses on `ON_STOP` so audio does not continue in the background.
- `FLAG_KEEP_SCREEN_ON` is set only while a video is playing.
- The narrator is released when the composition is disposed.
- Process death loses in-memory session state; progress and settings survive in
  `SharedPreferences`. The child returns to home.

## 12. Security and privacy boundaries

- The manifest requests no system permissions.
- No network libraries, analytics, crash reporting, ads, or tracking. The only third-party
  runtime is the MIT-licensed Rive animation engine, which does not require the `INTERNET`
  permission.
- Backups and device transfer are disabled (`allowBackup=false`, `fullBackupContent=false`,
  `dataExtractionRules`).
- Content is read-only from assets; no downloads or remote config.
- The parental gate is friction, not authentication.
- See [security and privacy](security-privacy.md).

## 13. Testing seams

| Seam | How it is tested |
| --- | --- |
| Session engines | Pure JVM tests with seeded `Random` |
| Content validator | JVM tests over the shipped JSON |
| Asset presence | JVM test opens each video and checks size |
| Number words | JVM tests for 1–10 |
| Garden cap | JVM test on `Progress` |
| UI smoke | Compose test on device/emulator |
| Device behaviour | Manual script |

The design avoids mocks: repositories are small, real, and Android-only at the edges; the
interesting logic is pure.

## 14. Trade-offs and known limits

- **No DI framework.** Manual construction is transparent at this size. If the app grows to
  multiple features with shared scopes, revisit.
- **No database.** Two small preference files are enough. Session history is intentionally
  not stored.
- **No Media3.** `VideoView` is less capable but much smaller and adequate for two local
  MP4s. If captions, multiple audio tracks, or streaming are added, revisit.
- **No adaptive difficulty.** The MVP uses a fixed content pack and caregiver-selected
  session length. Adaptive behaviour would need evidence and safeguards.
- **No offline voice bundled.** TTS depends on the device. A caregiver can choose an offline
  system voice; the app documents this.
- **Single module.** Fast to build and easy to audit. Split only when a second app or a
  shared library appears.
- **Compose-only UI.** No XML layouts except themes and launcher resources. This keeps one
  rendering model.
- **No screenshot tests.** The manual script covers visual checks for now; screenshot tests
  are a roadmap item if the design stabilises.

## 15. Decisions

- [0001 — Native Kotlin + Jetpack Compose](adr/0001-native-kotlin-compose.md)
- [0002 — Offline-only, no accounts or telemetry](adr/0002-offline-only.md)
- [0003 — Validated JSON content pack](adr/0003-json-content-pack.md)
- [0004 — Platform video player](adr/0004-platform-video.md)
- [0005 — SharedPreferences over Room](adr/0005-shared-preferences.md)
- [0006 — Manual dependency injection](adr/0006-manual-di.md)
- [0007 — TTS narration instead of baked voice](adr/0007-tts-narration.md)
- [0008 — Errorless scaffolding and no scores](adr/0008-errorless-no-scores.md)
