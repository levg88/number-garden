# Handoff: current state of Number Garden

Last updated: 14 November 2026. This document is the fastest way for another agent or a
human to continue exactly where this build stopped. Read it first, then the
[README](../README.md) and [test plan](04-qa/test-plan.md).

## 1. What this repository is

- Product: **Number Garden**, a calm offline early-math Android app for a 5-year-old autistic
  child (ages 4–6 by design).
- Package: `com.numbergarden.app`.
- Stack: Kotlin + Jetpack Compose, single Gradle module, manual dependency injection.
- Version: `0.1.0` (versionCode 1). MVP is feature-complete.
- Licences: code MIT (`LICENSE`); artwork, videos, and written content CC BY-NC 4.0
  (`docs/content-license.md`).

Everything needed to build is committed, including the Gradle wrapper JAR and both MP4s.
`local.properties` is intentionally not committed.

## 2. Build and test commands

Verified environment: macOS arm64, JDK 21, Android SDK platform 36, build-tools 36.0.0,
Gradle 8.14.3 via the wrapper.

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export ANDROID_HOME="$HOME/android-sdk"

./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleRelease
./gradlew :app:connectedDebugAndroidTest      # needs a device/emulator
```

Create `local.properties` with `sdk.dir=/absolute/path/to/Android/sdk` if `ANDROID_HOME` is
not set.

Current artifacts:

| Artifact | Path | Size |
| --- | --- | --- |
| Debug APK | `app/build/outputs/apk/debug/app-debug.apk` | ~13 MB |
| Release APK (unsigned) | `app/build/outputs/apk/release/app-release-unsigned.apk` | ~1.8 MB |

## 3. Verification status

| Check | Status | Evidence |
| --- | --- | --- |
| JVM unit tests | Passing | `:app:testDebugUnitTest` |
| Android lint | Passing | `:app:lintDebug`, no errors |
| Debug build | Passing | `:app:assembleDebug` |
| Release build (R8 + shrinking) | Passing | `:app:assembleRelease`, unsigned |
| Compose smoke test | Passing | `:app:connectedDebugAndroidTest`, 1 test |
| Emulator manual pass | Complete | Pixel 6 AVD `ng_api36`, Android 16 / API 36, 100% and 200% font scale |
| Target hardware | **Not run** | See open items |
| Signed release | **Not produced** | No keystore configured |

### What was exercised on the emulator

First launch and home; Watch list and the watched marker; lesson playback through the end
state and "The end."; counting hint, reveal, completion, and flower; comparing hint and
reveal for both more/fewer; all number cards 1–10; empty/partial garden; parental gate hold
screen, arithmetic alternative, and wrong-answer path; Settings including live high-contrast
toggle and persisted switches; back/home navigation.

Screenshots were captured under `app/build/*-emulator.png`. They are **not committed**
because `build/` is ignored; the observations above and the fixes below are the durable
record.

Two caveats about emulator evidence:

- `adb exec-out screencap` renders the `VideoView` surface black. Playback was confirmed via
  moving captions, progress, completion, and the watched marker, not by screenshot pixels.
- Emulator TTS and video codecs are not proof of what a real device or a particular OEM
  build will do.

### Fixes found by the emulator pass

1. **Compare teaching sentence** used to produce "Yes. one is fewer than Two." It now orders
   the larger quantity first, capitalises correctly, and uses the right plural group
   description. Covered by `ComparisonWordPairTest`.
2. **Home at 200%** broke "Compare"/"Numbers" mid-word in the two-column grid. At
   `fontScale >= 1.5` home now renders one full-width `LargeActionTile` per row.
3. **Watch at 200%** broke "Watched" into "Watche/d". The duration/watched metadata row is
   now a `FlowRow`, so the badge wraps as a unit.
4. **Numbers at 200%** clipped "Previous" in a half-width button. Previous/Next now stack
   full-width at `fontScale >= 1.5`.
5. **Parental gate at 200%** spilled the hold label outside the fixed 216 dp circle. The
   circle grows to 288 dp and the label padding adjusts at `fontScale >= 1.5`.

## 4. Emulator setup that was used

```bash
# AVD created earlier in this environment
"$ANDROID_HOME/emulator/emulator" -avd ng_api36 -no-window -no-audio -no-boot-anim \
  -gpu swiftshader_indirect -no-snapshot

# Reinstall and test at 200% text
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell settings put system font_scale 2.0
adb shell am force-stop com.numbergarden.app
adb shell am start -n com.numbergarden.app/.MainActivity
```

Reset text size with `adb shell settings put system font_scale 1.0`. The emulator screen is
1080×2400 at 420 dpi. Use `uiautomator dump` for exact tap bounds; screenshots are scaled in
some viewers.

## 5. Security and privacy facts to re-check before release

- `AndroidManifest.xml` declares **no system permissions**, including no `INTERNET`.
- `aapt dump permissions` shows only
  `com.numbergarden.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`. This is an app-private
  signature permission added by AndroidX for dynamically registered receivers. It is not a
  runtime permission and cannot be granted to other apps.
- The merged manifest also contains AndroidX's non-exported `InitializationProvider` and its
  exported `ProfileInstallReceiver`, which is protected by `android.permission.DUMP`.
- `android:allowBackup="false"`, `android:fullBackupContent="false"`,
  `android:usesCleartextTraffic="false"`, and `res/xml/data_extraction_rules.xml` exclude
  cloud backup and device transfer.
- Progress and settings live in app-private `SharedPreferences`.
- TTS is provided by the device. A network-backed voice may download data; the in-app privacy
  text tells caregivers to choose an offline voice if they want a fully offline device.
- The parental gate is friction, not a security boundary.

Re-run the manifest checks against the final artifact:

```bash
"$ANDROID_HOME/build-tools/36.0.0/aapt" dump permissions \
  app/build/outputs/apk/release/app-release-unsigned.apk
```

## 6. Remaining work, in recommended order

1. **Target-hardware pass.** Install the debug APK on the child's actual device. Follow
   `04-qa/manual-test-script.md`. Prioritise TTS voice quality, video playback, insets and
   cutouts, TalkBack traversal, rotation, background/resume, process death, and airplane mode.
2. **Release build install.** The release APK is unsigned. Create a keystore outside the
   repository, wire signing via environment/Gradle properties, then install and smoke-test the
   signed release (R8 bugs only appear there).
3. **TalkBack audit.** Verify one focus stop per control, heading order, live-region feedback,
   and that decorative art is not announced. The 200% visual pass is not a TalkBack pass.
4. **Rotation and process death.** Activities are portrait-agnostic and state is saveable, but
   this has not been exercised end to end.
5. **Content review.** An educator/OT/SLP should review the counting/comparing sequence and
   the words "more"/"fewer". The app is not clinically validated and must not be described as
   a medical or diagnostic tool.
6. **Caregiver review.** Confirm the sensory defaults, garden reward, and gate friction match
   this child and household.
7. **Optional polish.** Consider screenshot tests now that the layout is stable. Revisit
   switch access, AAC, and audio-first activities only with professional collaboration.

Do not mark the release checklist complete until the signed artifact has been installed and
spot-checked on target hardware.

## 7. Code map for the next engineer

| Area | Where |
| --- | --- |
| App entry / navigation | `app/src/main/java/com/numbergarden/app/MainActivity.kt`, `ui/NumberGardenApp.kt` |
| Screens | `ui/screens/` — `HomeScreen`, `WatchScreen`, `LessonPlayerScreen`, `CountingScreen`, `CompareScreen`, `NumbersScreen`, `GardenScreen`, `GrownUpsGateScreen`, `SettingsScreen`, plus the Play games (`MatchScreen`, `TapNumberScreen`, `PatternScreen`, `AdditionScreen`, `ShapesScreen`, `MissingScreen`, `MathScreen`, `OrderScreen`, `BondScreen`, `PositionScreen`) |
| Shared components | `ui/components/GardenComponents.kt`, `SessionCompletionScreen.kt`, `ContentErrorScreen.kt` |
| Animation | `ui/components/GameMotion.kt` (per-game scenes), `ui/components/GardenBuddy.kt` (Rive mascot + fallback) |
| Session logic | `domain/` — counting/comparing/pattern/math/order/bond/position engines and round generation |
| Content model + validation | `model/`, `data/ContentRepository.kt`, `data/ContentValidator.kt` |
| Persistence | `data/PrefsProgressRepository.kt`, `data/PrefsSettingsRepository.kt` |
| TTS | `audio/` |
| Bundled content | `app/src/main/assets/content.json`, `app/src/main/assets/videos/` |
| Theme | `ui/theme/` |
| Media generator | `tools/render_media.swift`, `tools/make_audio.py`, `tools/build_media.sh` |
| CI | `.github/workflows/android.yml` |
| Decisions | `docs/03-architecture/adr/0001`–`0008` |

## 8. Recent additions (after baseline)

- Animation upgrade: Rive runtime + `garden_buddy.riv` mascot, and a bespoke Compose scene
  per activity in `ui/components/GameMotion.kt`.
- Three new games: Order, Number bonds, and Positions, with engines, ViewModels, screens,
  routes, and JVM tests.
- Character voice cast in `audio/Narrator.kt` (Pip, Willow, Bramble, Zippy) with
  phrase-level pitch/rate/pause acting and a 2×2 picker in Grown-ups.
- Lottie was removed; all art is Compose Canvas or Rive.
- Play hub now lists twelve activities.
- Sound cues via `SoundEffects` (soft ack/prompt tones) plus a Settings "Test voice" button.
- TTS now falls back to the device default locale when en-US is unavailable.
- Lesson videos re-encoded to H.264 Constrained Baseline with fast-start; poster frames added.
- Lesson player reports playback errors instead of hanging on black.

## 9. Working-tree notes

- The project was developed inside an OpenCode worktree. When this repository was created,
  the full tree (source, docs, media, wrapper) was copied without the host worktree's `.git`
  metadata, so history starts at a single baseline commit.
- `app/build/`, `.gradle/`, `.kotlin/`, `local.properties`, signing files, and captured
  screenshots are intentionally ignored.
- The release APK is unsigned by design in this snapshot; do not distribute it.
