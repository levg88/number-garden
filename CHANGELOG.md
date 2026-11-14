# Changelog

All notable changes to Number Garden are recorded here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project intends to use
[Semantic Versioning](https://semver.org/) once there is a public release.

## [0.1.0] — 2026-11-14

First MVP, implemented in this repository. Not yet device-verified and not release-ready.

### Added

- Android app in Kotlin + Jetpack Compose, package `com.numbergarden.app`.
- Home screen with five child choices (Watch, Count, Compare, Numbers, My garden) and a
  quieter Grown-ups entry.
- Watch list and lesson player with two generated videos:
  - `count_to_3.mp4` (26 s)
  - `more_and_less.mp4` (28 s)
- Timed captions, optional platform TTS narration, play/pause/replay, no autoplay, pause on
  background.
- Counting activity for 1–5 with tap-to-count, numeral choices, hint after one miss, answer
  after two, and a non-competitive completion.
- Comparing activity with more/fewer, count reveal, and correct-group outline.
- Number cards 1–10 with ten-frame, numeral, word, and narration.
- Garden with 12 flowers, one per completed session, capped and never decaying.
- Parental gate with 3-second hold and an arithmetic alternative.
- Settings: voice, gentle sounds, reduce motion, high contrast, 3/5 rounds per session, reset
  progress, privacy and about text.
- Strict, versioned JSON content pack with load-time validation and JVM tests.
- Local progress and settings in private `SharedPreferences`.
- No Android system permissions, no network, no ads, no accounts, no analytics, no third-party SDKs.
- Backups and device-to-device transfer disabled, including Android 12+ extraction rules.
- Calm light theme, high-contrast variant, custom typography, and code-drawn apple/flower art.
- Unit tests for counting/comparing sessions, content validation, asset presence, number
  words, and the garden cap.
- Compose home smoke test for device/emulator.
- GitHub Actions CI for tests, lint, and debug assemble.
- Media generator: `tools/render_media.swift`, `tools/make_audio.py`, `tools/build_media.sh`.
- SDLC documentation: discovery, PRD, stories, metrics, design principles, UX flows, design
  system, accessibility, content style guide, architecture, data model, content pipeline,
  privacy/security, eight ADRs, test plan, manual test script, roadmap, release checklist,
  store listing draft.
- MIT code licence and CC BY-NC 4.0 content licence.

### Added

- Six calm games in a new Play area: Match, Tap the number, Patterns, Add, Shapes, and
  Missing number.
- Gentle motion: completion flower bloom, button pulse on correct answers, respecting
  reduce-motion and system animation settings.
- Play hub on Home with game cards.
- Baseline-profile, fast-start lesson videos for broader device compatibility, plus poster
  frames so the player is never a black box before playback.
- A "Test voice" control in Settings and a clearer empty state for unavailable TTS.
- Extra progress tracking for the new games (garden still caps at 12).

### Fixed

- Compare teaching sentence now names the larger quantity first with correct capitalisation
  and pluralisation, e.g. "This group. Two is fewer than three." instead of the previous
  "Yes. one is fewer than Two." Also corrected the TalkBack group description ("1 apple"
  vs "2 apples").
- Home falls back to one full-width action tile per row at 150%+ font scale, so "Compare"
  and "Numbers" no longer break mid-word.
- Watch lesson cards let the "Watched" badge wrap to its own line instead of breaking the
  word ("Watche/d") at 200% font scale.
- Numbers navigation stacks Previous/Next full-width at 150%+ font scale instead of
  clipping "Previous".
- Parental-gate hold target grows at 150%+ font scale so "Press and hold for 3 seconds."
  stays inside the circle.
- Lesson player now reports playback errors instead of hanging on a black surface and
  shows a poster frame while loading.
- Re-encoded both videos to H.264 Constrained Baseline with fast-start for devices that
  do not decode High profile.

### Verification

- `:app:testDebugUnitTest`, `:app:lintDebug`, `:app:assembleDebug`, and
  `:app:assembleRelease` pass.
- `:app:connectedDebugAndroidTest` passes on a Pixel 6 / Android 16 (API 36) emulator.
- Manual emulator pass covers Home, Watch, lesson playback completion, Count, Compare,
  Numbers, Garden, parental gate, Settings, high contrast, and 100%/200% font scale.
- Emulator-only evidence and outstanding hardware checks are recorded in
  [`docs/HANDOFF.md`](docs/HANDOFF.md).

### Known limitations

- Manual target-hardware testing is still pending; emulator tests cannot prove TTS voice
  quality, OEM video playback, TalkBack traversal, or true device insets.
- The release APK is unsigned; no release keystore is configured.
- Video playback, TTS quality, insets, intrinsic sizing, TalkBack, 200% font scale, and
  rotation are implemented and emulator-checked, but not verified on hardware.
- English (US) only; TTS language is fixed to `Locale.US`.
- No switch access, AAC input, or audio-first activity mode.
- The parental gate is friction, not security.
- Content and sequence have not been reviewed by an educator/OT/SLP.
- Not a medical device; no clinical or diagnostic claims.

### Security

- The manifest requests no system permissions (one app-private signature permission is added by AndroidX for internal dynamic receivers).
- No network libraries or remote content.
- Local-only state; backups and transfer excluded.

## Unreleased

### Planned

- Run and pass the manual device script on the target device.
- Fix any S1/S2 issues found on hardware.
- Add a compare target/values consistency check to `ContentValidator`.
- Review content and language with an educator/OT/SLP.
- Decide whether 3 rounds should be the default session length.
- Consider screenshot tests once the visual design stabilises.

[0.1.0]: https://example.invalid/number-garden/releases/tag/v0.1.0
