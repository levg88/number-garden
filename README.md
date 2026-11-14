# Number Garden

**A calm, offline early-math app for autistic children aged 4–6.**

Number Garden is a small Android app for one child and their caregiver: watch a quiet
counting video, count apples by tapping them, compare two groups, explore numbers 1–10,
and grow a flower for every completed session. No timers. No scores. No ads. No internet.

This repository is also a complete, lightweight **SDLC example**: discovery notes, a PRD,
inclusive design system, architecture decision records, a working Android app, automated
tests, a QA script, CI, and a release checklist.

> **Status: MVP implemented and emulator-verified.** Kotlin + Jetpack Compose app, two
> generated lesson videos, bundled content pack, local progress, parental gate, and JVM tests.
> Unit tests, lint, debug/release builds, and the Compose smoke test pass. Every child screen
> has been exercised on an Android 16 (API 36) emulator at 100% and 200% font scale; the
> release APK is 1.8 MB and requests no system permissions. It is **not** clinically validated
> and is not a medical or diagnostic tool. Target-hardware checks (TalkBack traversal, TTS
> quality, OEM video playback, rotation, process death, signed-release install) are still
> pending. See [docs/HANDOFF.md](docs/HANDOFF.md) before continuing.

---

## What is in the app

| Area | Behaviour |
| --- | --- |
| **Watch** | Two short videos with timed captions and optional device text-to-speech narration. Play, pause, replay; never autoplays. |
| **Count** | Count 1–5 apples by tapping. Choose the matching numeral. After one miss, sequence hints appear; after two, the answer is shown gently. No fail state. |
| **Compare** | Choose the group with *more* or *fewer*. After a miss, counts are revealed; after two, the correct group is outlined. |
| **Numbers** | Picture cards for 1–10 with a ten-frame, numeral, word, and spoken narration. |
| **My garden** | One flower per completed counting/comparing session, capped at 12. It never wilts and never resets by itself. |
| **Grown-ups** | Parental gate, then voice/sound/motion/contrast controls, 3 or 5 rounds per session, reset progress, and a plain-language privacy note. |

The app requests **no Android system permissions** — including no `INTERNET` permission.
The merged manifest contains one app-private signature permission added by AndroidX to
protect dynamically registered broadcast receivers; it is not a runtime permission, is not
granted to other apps, and gives no access to user data.

---

## Quick start

Requirements: JDK 21, Android SDK platform 36 and build-tools 36.0.0.

```bash
# Point Gradle at your SDK (or set ANDROID_HOME)
echo "sdk.dir=$HOME/android-sdk" > local.properties

./gradlew testDebugUnitTest    # JVM tests, including content-pack validation
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew lintDebug            # Android lint
```

Install on a connected device:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The bundled videos are committed, so building the app does **not** require ffmpeg or
Xcode. To regenerate them, see `tools/README.md` and run `./tools/build_media.sh`
(macOS + ffmpeg + Swift).

---

## SDLC map

| Stage | Artifact | State |
| --- | --- | --- |
| 0. Discovery | [Vision, users, assumptions](docs/00-discovery/vision.md) | Draft, needs caregiver input |
| 1. Product | [PRD](docs/01-product/prd.md) · [User stories](docs/01-product/user-stories.md) · [Metrics](docs/01-product/metrics.md) | MVP scope frozen |
| 2. UX / design | [Principles](docs/02-design/design-principles.md) · [Flows](docs/02-design/ux-flows.md) · [Design system](docs/02-design/design-system.md) · [Accessibility](docs/02-design/accessibility.md) · [Content style](docs/02-design/content-style-guide.md) | Implemented |
| 3. Architecture | [Architecture](docs/03-architecture/architecture.md) · [Data model](docs/03-architecture/data-model.md) · [Content pipeline](docs/03-architecture/content-pipeline.md) · [Privacy/security](docs/03-architecture/security-privacy.md) · [ADRs](docs/03-architecture/adr/) | Implemented |
| 4. Build | This repository (`app/`, `tools/`, CI) | Builds and tests locally |
| 5. Verification | [Test plan](docs/04-qa/test-plan.md) · [Manual device script](docs/04-qa/manual-test-script.md) · [Handoff](docs/HANDOFF.md) | JVM tests, emulator smoke, and 100%/200% emulator pass; hardware pass pending |
| 6. Release | [Roadmap](docs/05-delivery/roadmap.md) · [Release checklist](docs/05-delivery/release-checklist.md) · [Store listing draft](docs/05-delivery/store-listing.md) | Not release-ready |
| 7. Learn | [Metrics and research plan](docs/01-product/metrics.md) | Blocked on consent-based pilot |

Also see [CHANGELOG.md](CHANGELOG.md) and the [docs index](docs/README.md).

---

## Design guardrails (why it looks and behaves like this)

1. **Predictability over surprise.** Same cream frame, same top bar, same ending ritual.
2. **Low sensory load.** Muted palette, no flashing, no confetti, no shock sounds. The two
   generated videos include gentle fades and a quiet pentatonic chime bed.
3. **One instruction at a time.** Short spoken lines, large visuals, no decorative text.
4. **The child controls the pace.** Pause, replay, and leave are always available. Nothing
   moves on its own except the video the child started.
5. **Errorless learning.** A mistake produces a hint, then the answer — never a loss,
   score penalty, or buzzer.
6. **Repetition is allowed.** Content is short, replayable, and finite. The garden fills
   but never resets or decays.
7. **Audio is optional.** Captions are visible; voice and sound can be turned off
   independently; the app is fully usable in silence.
8. **Grown-ups own the settings; children own the play.** Settings sit behind a parental
   gate. There are no purchases, links out, ads, or data collection.

These are design hypotheses, not clinical claims. They should be validated with the
child, caregiver, and treating professionals.

---

## Architecture at a glance

```
Compose UI (screens + reusable calm components)
        │  StateFlow, immutable UI state
ViewModels (Counting, Comparing)  ── pure session engines in domain/
        │
Repositories ────────────────┐
  ContentRepository (assets/content.json, strict validation)
  PrefsProgressRepository (SharedPreferences)
  PrefsSettingsRepository (SharedPreferences + StateFlow)
        │
Bundled assets: videos/*.mp4, content.json   |   Platform VideoView + TextToSpeech
```

- Single Gradle module, manual dependency container, no Hilt/Room/Media3.
- Bundled JSON content is validated on load **and** in JVM tests.
- Videos are copied once to cache for reliable playback; no network permission exists.
- Narration uses the platform TTS engine, so no voice files are shipped and every
  caption stays in sync without baking audio into video.
- See [architecture](docs/03-architecture/architecture.md) and the
  [ADRs](docs/03-architecture/adr/) for trade-offs.

---

## Tests

```bash
./gradlew testDebugUnitTest
```

Covers round generation, grading/scaffolding, completion, prompt ordering, content
validation, asset presence, number words, and the garden cap. The Compose smoke test
requires a device/emulator:

```bash
./gradlew connectedDebugAndroidTest
```

The [manual device script](docs/04-qa/manual-test-script.md) covers the things automated
tests cannot: sensory response, TalkBack, 200% font scale, offline behaviour, rotation,
background/resume, gate friction, and the reset flow.

---

## Privacy

- No network permission, no ads, no accounts, no analytics, no third-party SDKs.
- Progress is stored only in app-private `SharedPreferences`; backups are disabled.
- No child names, birthdays, photos, audio, or location are ever requested.
- The device's TTS engine is external to the app. A network-based voice may download
  data; choose an offline voice in system settings for a fully offline experience.
- The parental gate is friction, not a security boundary.
- Data safety and store claims are drafted in
  [security-privacy.md](docs/03-architecture/security-privacy.md).

---

## Repository layout

```
app/                     Android app (Kotlin, Compose)
  src/main/assets/       content.json + generated lesson videos
  src/main/java/...      model / data / domain / audio / ui
  src/test/              JVM tests
  src/androidTest/       Compose smoke test
docs/                    SDLC artifacts (discovery → delivery)
tools/                   Video/audio generation + content pipeline notes
gradle/                  Version catalog and wrapper
.github/workflows/       CI: tests, lint, debug APK
```

---

## License

- **Code:** MIT — see [LICENSE](LICENSE).
- **Artwork, videos, and written content:** CC BY-NC 4.0 — see
  [docs/content-license.md](docs/content-license.md). They may be adapted for
  non-commercial use with attribution.

Built with the belief that “small, calm, and predictable” is a feature, not a limitation.
