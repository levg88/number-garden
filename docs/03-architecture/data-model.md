# Data model

Number Garden stores two kinds of data:

1. **Bundled content** — read-only, shipped in `assets/`, versioned by `schemaVersion`.
2. **Local state** — progress and settings in app-private `SharedPreferences`.

There is no database, no user table, no account, and no server. No personal data is stored.

## 1. Content pack

File: `app/src/main/assets/content.json`
Schema version: `1`
Decoder: `kotlinx.serialization` with strict keys.

```kotlin
@Serializable
data class ContentPack(
    val schemaVersion: Int,
    val lessons: List<Lesson>,
    val numberCards: List<NumberCard>,
    val countingValues: List<Int>,
    val compareRounds: List<CompareRound>,
    val maxSessionRounds: Int = 5,
    val optionCount: Int = 3,
)

@Serializable
data class Lesson(
    val id: String,
    val title: String,
    val skill: String,
    val video: String,
    val summary: String,
    val durationSec: Int,
    val captions: List<Caption>,
)

@Serializable
data class Caption(val atMs: Int, val text: String)

@Serializable
data class NumberCard(val value: Int, val word: String, val narration: String)

@Serializable
data class CompareRound(
    val left: Int,
    val right: Int,
    val target: CompareTarget,
)

@Serializable
enum class CompareTarget { MORE, FEWER }
```

### 1.1 Field rules

| Field | Type | Rule |
| --- | --- | --- |
| `schemaVersion` | int | Must be `1` for this build |
| `lessons` | array | At least 1; `id` unique and non-blank |
| `lessons[].title` | string | Non-blank |
| `lessons[].skill` | string | `counting` or `comparing` in the MVP |
| `lessons[].video` | string | Matches `^videos/[a-z0-9_]+\.mp4$`; asset must exist |
| `lessons[].summary` | string | Non-blank |
| `lessons[].durationSec` | int | 5–300; captions must fit inside |
| `lessons[].captions` | array | At least 1; non-blank; sorted by `atMs`; `0 ≤ atMs < durationSec × 1000` |
| `numberCards` | array | Exactly 10; values 1–10 exactly once |
| `numberCards[].word` | string | Non-blank English number word |
| `numberCards[].narration` | string | Non-blank sentence |
| `countingValues` | int array | Non-empty; each 1–10 |
| `compareRounds` | array | Non-empty; each side 1–10; left ≠ right |
| `compareRounds[].target` | enum | `MORE` or `FEWER` |
| `maxSessionRounds` | int | 1–10 |
| `optionCount` | int | 2–4; enough distinct numbers must exist |

### 1.2 Invariants not expressible in the schema

- A `MORE` round must have a strictly larger side; a `FEWER` round must have a strictly
  smaller side. The current validator only rejects equal groups; the UI derives the correct
  side from the values, so an impossible target would still be answerable but semantically
  wrong. **Roadmap:** add a target/values consistency check.
- `durationSec` must match the actual video length. The validator cannot inspect media.
  The manual script checks playback end behaviour.
- Captions should be readable for at least ~2.5 s. This is a style rule, not a hard
  validation rule.

### 1.3 Shipped content summary

| Lesson | Skill | Duration | Captions |
| --- | --- | --- | --- |
| `count_to_3` | counting | 26 s | 4 |
| `more_and_less` | comparing | 28 s | 4 |

- Number cards: 1–10.
- Counting values: `[3, 2, 4, 1, 5, 3, 2, 4, 5, 1]`.
- Compare rounds: 10 pairs covering more and fewer, values 1–5.
- `maxSessionRounds`: 5.
- `optionCount`: 3.

## 2. Progress

Interface: `model/Progress.kt` → `ProgressRepository`
Implementation: `data/PrefsProgressRepository.kt`
File: `number_garden_progress` (private `SharedPreferences`)

```kotlin
data class Progress(
    val completedLessons: Set<String> = emptySet(),
    val countingSessions: Int = 0,
    val compareSessions: Int = 0,
) {
    val totalSessions: Int get() = countingSessions + compareSessions
    val blooms: Int get() = totalSessions.coerceAtMost(MAX_BLOOMS)
    companion object { const val MAX_BLOOMS = 12 }
}
```

| Key | Type | Meaning | Default |
| --- | --- | --- | --- |
| `completed_lessons` | string set | Lesson IDs watched to the end | empty |
| `counting_sessions` | int | Completed counting sessions | 0 |
| `compare_sessions` | int | Completed comparing sessions | 0 |

Rules:

- `markLessonWatched` is idempotent; watching again does not change the set.
- `recordSession` increments exactly once per completed session.
- Negative stored counts are coerced to 0 on load.
- `blooms` is computed and capped at 12; it is never stored, so it cannot drift or decay.
- `reset()` clears the progress file only.
- A completed session is recorded when the session engine reports `completed`; leaving
  early does not record anything.

### 2.1 Why no session history

The app does not need per-round history to function. Storing it would create a behavioural
record about a child, which the product deliberately avoids. Aggregate session counts are
the minimum needed to grow the garden.

## 3. Settings

Interface: `model/Progress.kt` → `SettingsRepository`
Implementation: `data/PrefsSettingsRepository.kt`
File: `number_garden_settings` (private `SharedPreferences`)

```kotlin
data class AppSettings(
    val voice: Boolean = true,
    val sound: Boolean = true,
    val reduceMotion: Boolean = false,
    val highContrast: Boolean = false,
    val roundsPerSession: Int = 5,
)
```

| Key | Type | Meaning | Default |
| --- | --- | --- | --- |
| `voice` | boolean | TTS narration | true |
| `sound` | boolean | Video/chime audio | true |
| `reduce_motion` | boolean | Skip decorative animation | false |
| `high_contrast` | boolean | High-contrast theme | false |
| `rounds_per_session` | int | Rounds per counting/compare session | 5 |

Rules:

- `roundsPerSession` is coerced to `1..5` on load and write. The UI offers 3 and 5.
- Every setter updates the in-memory `StateFlow` first, then writes with `apply()`.
- Settings survive `reset()`.
- Settings are not backed up or transferred.

## 4. Derived UI state

### 4.1 Counting

```kotlin
data class CountingRound(val quantity: Int, val options: List<Int>)

data class CountingState(
    val rounds: List<CountingRound>,
    val index: Int = 0,
    val selected: Int? = null,
    val misses: Int = 0,
    val phase: CountPhase = CountPhase.ASK,
    val completed: Boolean = false,
)
```

- `quantity` is 1–5 in the shipped pack (validator permits 1–10).
- `options` contains the answer plus `optionCount - 1` distractors, sorted ascending.
- `phase`: `ASK` → `CORRECT` or (after 2 misses) `TEACH`.
- `hintLevel`: 0 in `ASK` with no misses, 1 after one miss, 2 in `TEACH`.

### 4.2 Comparing

```kotlin
data class CompareUiRound(left: Int, right: Int, target: CompareTarget) {
    val correctSide: GroupSide
    fun isCorrect(side: GroupSide): Boolean
}

data class CompareState(
    val rounds: List<CompareUiRound>,
    val index: Int = 0,
    val phase: ComparePhase = ComparePhase.ASK,
    val misses: Int = 0,
    val selectedSide: GroupSide? = null,
    val completed: Boolean = false,
)
```

- `GroupSide`: `LEFT` or `RIGHT`.
- `hintLevel`: same rule as counting.
- The correct side is derived, never stored.

## 5. Ephemeral state

| State | Lifetime | Stored? |
| --- | --- | --- |
| Current round index | Session composition | No |
| Misses / hint level | Session composition | No |
| Tapped apple ordinals | Current round | No |
| Video position | Rotation via `rememberSaveable` | No (process death loses it) |
| Caption index | Current playback | No |
| Pending TTS line | Until TTS ready | No |

Losing ephemeral state on process death is acceptable: progress and settings persist, and
the child returns to a predictable home screen.

## 6. Retention and deletion

- Progress and settings remain until the caregiver uses **Reset progress** or clears app
  storage/uninstalls.
- Reset removes all progress. It does not remove settings.
- There is no account deletion flow because there is no account.
- There is no export. Data is two small preference files on one device.
- Backups and device-to-device transfer are disabled, so the data does not leave the device
  through Android backup.

## 7. Schema evolution

For content:

1. Increment `schemaVersion`.
2. Update the Kotlin model and `ContentValidator`.
3. Keep the previous pack under version control for comparison.
4. Add a migration note to `CHANGELOG.md`.
5. Run `testDebugUnitTest`; the shipped-asset test must pass.

Because content is read-only and bundled, there is no on-device migration. A new app build
ships a new pack. The validator fails fast if the model and pack disagree.

For preferences:

- Unknown keys are ignored by `SharedPreferences`.
- Missing keys fall back to defaults.
- New settings must have a safe default that preserves the calm experience.
- Do not rename keys without a migration; there is currently no migration mechanism.
