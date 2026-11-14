# ADR 0001 — Native Kotlin + Jetpack Compose

- **Status:** Accepted
- **Date:** November 2026
- **Deciders:** Product/engineering (single caregiver-developer)

## Context

The app needs large touch targets, a calm custom visual system, text scaling, screen-reader
semantics, and precise control over motion and colour. The target is Android only. The team
is one developer, and the app must be easy to audit for child safety.

## Decision

Build a native Android app in Kotlin with Jetpack Compose, a single Gradle module, Material 3
theming, and Navigation Compose.

## Alternatives considered

| Alternative | Why not |
| --- | --- |
| Web app / PWA | Needs a browser, weaker offline install story, harder to guarantee no network, poorer TTS/video integration |
| Flutter | Good cross-platform fit, but adds a large runtime, a second language/toolchain, and platform channels for video/TTS; no iOS requirement |
| React Native | Same cross-platform overhead; JavaScript dependency surface is harder to audit for a child app |
| Unity | Heavy runtime, overkill for 2D UI, larger APK, more complex accessibility |
| Native Views + XML | Viable, but Compose gives faster iteration, better state handling, and simpler custom drawing for apples/flowers |

## Consequences

**Positive**

- One language and one UI toolkit.
- Direct access to `VideoView`, `TextToSpeech`, and `SharedPreferences`.
- Easy to keep the dependency graph small.
- Compose semantics map cleanly to TalkBack.
- Fast builds for a single module.

**Negative**

- Android only; no iOS without a rewrite.
- Compose tooling and BOM versions must be kept current.
- Some platform interop (`AndroidView` for `VideoView`) is needed.
- No shared code with a future web/iOS client.

## Follow-ups

- Keep the module small; split only when a second app or shared library appears.
- Pin versions in `gradle/libs.versions.toml`.
- Revisit if iOS becomes a requirement.
