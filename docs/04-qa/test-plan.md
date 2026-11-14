# Test plan

| | |
| --- | --- |
| **Product** | Number Garden MVP 0.1 |
| **Scope** | Android app, bundled content pack, media assets, local state |
| **Test owner** | Caregiver/developer |
| **Status** | JVM tests, lint, builds, and emulator pass complete; target-hardware pass pending |
| **Last updated** | November 2026 |

## 1. Objectives

1. Verify the child can complete counting and comparing rounds without a fail state.
2. Verify the app is fully usable offline and requests no system permissions.
3. Verify content and media are valid and cannot produce an empty or wrong activity.
4. Verify settings, parental gate, progress cap, and reset behave as specified.
5. Verify accessibility accommodations at 200% font scale, with TalkBack, high contrast, and
   reduced motion.
6. Verify predictable behaviour across rotation, background/resume, process death, and
   missing assets.

## 2. Scope

### In scope

- All ten screens: Home, Watch, Lesson player, Counting, Comparing, Numbers, Garden,
  Parental gate, Settings, content error/loading.
- Bundled `content.json` and both MP4s.
- Session engines, validators, number words, progress cap.
- TTS and video playback integration.
- Settings persistence, progress persistence, reset.
- Offline behaviour, permissions, backup configuration.
- Accessibility and sensory settings.
- Build, lint, debug APK, release APK.

### Out of scope

- Clinical efficacy, diagnostic validity, or therapeutic outcomes.
- Multi-user profiles, accounts, cloud sync.
- Store publishing mechanics, signing key custody, legal review.
- Localisation beyond English.
- Accessibility beyond the checks in this plan (e.g. full WCAG audit, switch access).
- Performance under low-memory conditions on every OEM device.

## 3. Test levels

| Level | What | Tool | Status |
| --- | --- | --- | --- |
| Unit | Session engines, validator, number words, progress cap | JUnit 4 on JVM | Passing |
| Asset validation | Shipped JSON + MP4 presence/size | JUnit 4 | Passing |
| Compose UI smoke | Home renders five child choices | `connectedDebugAndroidTest` | Passing on API 36 emulator |
| Static analysis | Android lint | `lintDebug` | Passing locally |
| Build | Debug and release assemble | Gradle | Passing locally |
| Manual device | Playback, TTS, insets, font scale, TalkBack, rotation, gate, reset | Human | Emulator pass complete; hardware pending |
| Release | Signed build, install, store checks | Human | Pending (release APK currently unsigned) |

## 4. Environments

| Environment | Details |
| --- | --- |
| Local build | macOS (arm64), JDK 21, Android SDK platform 36, build-tools 36.0.0, Gradle 8.14.3 |
| CI | GitHub Actions, Ubuntu, Temurin JDK 21, Android SDK 36, Gradle wrapper |
| Target device | Android phone/tablet, API 26–36, 360 dp+ width; exact model TBD |
| Offline | Airplane mode / Wi-Fi and mobile data off |
| Font scale | 100%, 150%, 200% |
| Display | Default and largest system font/display size |
| Audio | Speaker, headphones, voice on/off, sound on/off |

## 5. Entry and exit criteria

### Entry

- `./gradlew :app:testDebugUnitTest` passes.
- `./gradlew :app:lintDebug` passes with no errors.
- `./gradlew :app:assembleDebug` succeeds.
- Content pack validates and both videos exist.
- A test device or emulator is available for manual checks.

### Exit for caregiver install

- All P0 unit and static checks pass.
- Manual device script passes on the target device, or every failure is documented with a
  workaround.
- Caregiver has reviewed the privacy note and sensory settings.
- Educator/OT/SLP has reviewed the counting/comparing sequence.
- No open P0 defect.

### Exit for public release

- All caregiver-install criteria.
- Release build signed, installed, and smoke-tested.
- Store listing and data-safety form match the shipped build.
- Release checklist complete.

## 6. Test cases

### 6.1 Automated unit tests

| ID | Test | Expected |
| --- | --- | --- |
| U-01 | Build counting rounds with 3/5 rounds | Correct count, 3 options, answer included, options sorted and distinct |
| U-02 | Same seed produces same rounds | Deterministic output |
| U-03 | Correct counting choice | Phase `CORRECT`, selected answer, 0 misses |
| U-04 | One wrong counting choice | Phase `ASK`, 1 miss, hint level 1, round still open |
| U-05 | Two wrong counting choices | Phase `TEACH`, answer selected, hint level 2, further taps ignored |
| U-06 | Counting next | Advances; completes after last; idempotent when complete |
| U-07 | Empty counting values | `IllegalArgumentException` |
| U-08 | Longer session repeats content | Requested count returned |
| U-09 | Build compare rounds | Correct count, unequal 1–10 groups |
| U-10 | More/fewer correct side | Derived correctly for both targets |
| U-11 | Correct compare choice | Phase `CORRECT` |
| U-12 | Two compare misses | Phase `TEACH`, correct side selected |
| U-13 | Compare next | Completes after last; idempotent |
| U-14 | Bundled content validates | No errors |
| U-15 | Every lesson video exists | File exists and > 10 KB |
| U-16 | Number cards 1–10 in order | Exact sequence |
| U-17 | Captions inside duration | All `atMs` in range |
| U-18 | Validator rejects duplicate lesson IDs | Error mentions unique |
| U-19 | Validator rejects equal compare groups | Error mentions equal groups |
| U-20 | Validator rejects incomplete number cards | Non-empty errors |
| U-21 | Garden cap | 0, 5, and 12-cap cases |
| U-22 | Number words | 1–10 correct, capitalisation behaviour |

`NumberWordsTest.kt` covers number-word spellings and capitalisation.

### 6.2 Compose smoke test

| ID | Test | Expected |
| --- | --- | --- |
| UI-01 | Launch MainActivity | "Let's play." and the four learning choices are displayed |

This is intentionally shallow. It proves the app composes and the home screen is reachable.
Deeper UI automation is deferred until the visual design stabilises.

### 6.3 Manual device tests

The full script is in [manual-test-script.md](manual-test-script.md). Summary:

| Area | Checks |
| --- | --- |
| Install/launch | Fresh install, first launch, no permission prompt, cold start |
| Home | Five choices, fixed order, garden count, adult entry |
| Watch | List, durations, watched marker, no autoplay |
| Player | Play/pause/replay, captions, voice, sound, end state, background pause |
| Counting | Tap to count, correct, hint, reveal, next/finish, completion, flower |
| Comparing | More/fewer, hint, reveal, completion, flower |
| Numbers | 1–10, ten-frame, narration, previous/next disabled at ends |
| Garden | Empty, partial, full, never resets |
| Gate | Hold 3 s, arithmetic alternative, back exits |
| Settings | Voice, sound, motion, contrast, 3/5 rounds, reset, persistence |
| Accessibility | TalkBack, 200% font, high contrast, reduce motion, target sizes |
| Resilience | Airplane mode, rotation, background/resume, process death, missing video |
| Privacy | No system permissions, no network, no child data in logcat, backups off |

## 7. Traceability

| Requirement | Automated | Manual |
| --- | --- | --- |
| FR-01 Home | UI-01 | M-HOME |
| FR-02 Watch | — | M-WATCH |
| FR-03 Player | — | M-PLAYER |
| FR-04 Counting | U-01–U-08 | M-COUNT |
| FR-05 Compare | U-09–U-13 | M-COMPARE |
| FR-06 Numbers | U-16, U-22 | M-NUMBERS |
| FR-07 Garden | U-21 | M-GARDEN |
| FR-08 Gate | — | M-GATE |
| FR-09 Settings | — | M-SETTINGS |
| FR-10 Persistence | — | M-PERSIST |
| FR-11 Offline/errors | U-14–U-20 | M-OFFLINE/M-ERROR |
| FR-12 Narration | — | M-VOICE |
| FR-13 Validation | U-14–U-20 | — |
| NFR-01 Targets | — | M-A11Y |
| NFR-02 Text scaling | — | M-A11Y |
| NFR-03 Contrast | — | M-A11Y |
| NFR-04 Motion | — | M-A11Y |
| NFR-05 Audio | — | M-VOICE |
| NFR-06 Startup | — | M-LAUNCH |
| NFR-07 Size | Build check | M-LAUNCH |
| NFR-08 Reliability | — | M-ERROR/M-PLAYER |
| NFR-09 Privacy | Manifest check | M-PRIVACY |
| NFR-10 Compatibility | — | Device matrix |
| NFR-11 Localisation | Static review | — |
| NFR-12 Maintainability | Unit tests | Code review |

## 8. Defect severity

| Severity | Definition | Example |
| --- | --- | --- |
| S1 | Child cannot use the app or data leaves device | Crash on launch, network permission, wrong answer always marked correct |
| S2 | Core activity broken or inaccessible | Video never plays, TTS blocks UI, 200% font clips Next |
| S3 | Noticeable but recoverable | Caption off by > 2 s, settings not persisted, gate hard to pass |
| S4 | Cosmetic or minor | Icon alignment, awkward wrap, typo in adult copy |

An S1 or S2 blocks caregiver install. S3/S4 may ship with documented workarounds if the
caregiver accepts them.

## 9. Risks and mitigations

| Risk | Likelihood | Impact | Mitigation |
| --- | --- | --- | --- |
| No emulator/device during development | High | Device-only bugs found late | Manual script ready; run before hand-off |
| `VideoView` OEM quirks | Medium | Video fails on some devices | Test at least two devices; generic error state; app remains usable |
| TTS voice unavailable/online | Medium | No narration | Voice off is fully supported; document offline voice |
| 200% font clips layout | Medium | Child cannot reach buttons | Scrollable roots; manual check; fix before install |
| TalkBack duplicate focus stops | Medium | Confusing traversal | Row-level semantics; manual audit |
| TTS timing drifts from captions | Low | Audio/text mismatch | Captions are the source; speech is optional |
| Content/JSON drift | Low | Wrong duration/timing | Validator + asset test; manual playback check |
| Release build R8 issue | Low | Crash only in release | Assemble and install release build before hand-off |

## 10. Test data

- Shipped content pack and videos.
- Seeded `Random` values in unit tests (42, 7, 3).
- Progress states: empty, 1 session, 5 sessions, 12+ sessions.
- Settings combinations: defaults; voice off; sound off; both off; high contrast; reduce
  motion; 3 rounds.
- Font scales: 100%, 150%, 200%.
- Locales: en-US (MVP).

No real child data is used in testing. Do not record the child during sessions without
explicit consent.

## 11. Reporting

For each run, record:

- build (`versionName`, commit hash), device model, Android version
- date, tester, font scale, theme, audio configuration
- pass/fail per case, with screenshots or a short description for failures
- severity, reproduction steps, expected vs actual
- whether the issue blocks caregiver install

Store results with the release checklist, not in the repository if they contain caregiver
or child information.

## 12. Current status

| Check | Result |
| --- | --- |
| `:app:testDebugUnitTest` | Passing locally |
| `:app:lintDebug` | Passing locally |
| `:app:assembleDebug` | Passing locally (13 MB debug APK) |
| `:app:assembleRelease` | Passing locally (1.8 MB unsigned release APK) |
| `:app:connectedDebugAndroidTest` | Passing on Pixel 6 / Android 16 (API 36) emulator |
| Manual emulator script | Home, Watch, lesson end state, Count, Compare, Numbers, Garden, Gate, Settings, high contrast, 100% and 200% font scale |
| Release build install | Not run (unsigned) |
| Signed release | Not produced |

Emulator-only checks still leave real-device risk: TTS voice quality, OEM `VideoView`
behaviour, true insets/cutouts, TalkBack traversal, rotation, process death, and release
R8/signing. The exact emulator evidence and the remaining checklist are recorded in
[HANDOFF.md](../HANDOFF.md). Do not hand the app to the child until the hardware pass and
caregiver/content review are complete.
