# Product requirements document (PRD)

| | |
| --- | --- |
| **Product** | Number Garden |
| **Version** | MVP 0.1 |
| **Owner** | Caregiver/product lead |
| **Status** | Implemented in this repository; pre-release |
| **Last updated** | November 2026 |
| **Platform** | Android (minSdk 26, targetSdk 36), phone and tablet |
| **Price model** | Free, no ads, no purchases |
| **Clinical status** | Not a medical device; not diagnostic or therapeutic claims |

## 1. Summary

Number Garden is a calm, offline Android app that helps a five-year-old autistic child
practise early number concepts: counting quantities to five, recognising numerals, and
comparing groups. It pairs two short, quiet videos with hands-on activities and a
non-competitive flower garden that grows with completed sessions.

The design prioritises predictability, low sensory load, child control, and errorless
practice over gamification. The app collects no data and requests no Android system
permissions.

## 2. Problem and opportunity

Many children's math apps rely on speed, streaks, loud rewards, and variable reinforcement.
For a child with sensory sensitivities, motor differences, or a need for routine, these
mechanics can create distress or compulsive use rather than learning.

Opportunity: a small, focused app that a caregiver can hand over without supervision
concerns, that a child can operate with one-finger taps, and that can be adapted to the
child's current sensory state.

## 3. Goals

- G1: The child can independently complete one counting or comparing round after a single
  demonstration.
- G2: The child can choose an activity from home and leave at any time without penalty.
- G3: A caregiver can adjust voice, sound, motion, contrast, and session length in under one
  minute, behind a gate.
- G4: The app is fully usable offline and requests no system permissions.
- G5: Content can be reviewed and replaced without changing UI code.

## 4. Non-goals

- Teaching addition/subtraction equations or multiplication.
- Assessing, diagnosing, or treating autism or any learning difference.
- Adaptive difficulty or machine learning in the MVP.
- Accounts, cloud sync, leaderboards, social features, or notifications.
- Ads, in-app purchases, external links, or data sharing.
- Multiple child profiles.
- Replacing a teacher, therapist, or caregiver's judgement.

## 5. Users and jobs to be done

**Child (primary).** “Let me do one small number thing that feels safe, and let me stop
when I need to.”

**Caregiver (secondary).** “Give me a calm five-minute option I can trust, and let me tune
the sensory settings for today.”

**Educator/therapist (reviewer).** “Show me that the sequence is developmentally sensible
and that feedback is neutral and consistent.”

## 6. MVP scope

| Capability | In MVP | Notes |
| --- | --- | --- |
| Count quantities 1–5 | Yes | Tap-to-count, numeral choice, hint then reveal |
| Compare *more* / *fewer* | Yes | Two groups, sizes 1–5 |
| Number cards 1–10 | Yes | Ten-frame, numeral, word, narration |
| Video lessons | Yes | Two 26–28 s generated videos |
| Captions | Yes | Burned in + timed in-app text |
| Voice narration | Yes | Platform TTS; optional |
| Progress garden | Yes | 12 flowers; no decay |
| Parental gate | Yes | 3 s hold or arithmetic question |
| Settings | Yes | Voice, sound, motion, contrast, rounds, reset |
| Offline | Yes | No network permission |
| Accounts/cloud/ads/analytics | No | Explicitly excluded |
| Localization | No | English only; strings externalised |
| Switch access/AAC | No | Roadmap |

## 7. Functional requirements

Priority: **P0** = required for MVP; **P1** = should ship before public beta.

### FR-01 — Home

- P0. Show exactly five child choices with fixed positions: Watch, Count, Compare, Numbers,
  My garden. Show a visually quieter Grown-ups entry.
- P0. Each choice has an icon/illustration, a short label, and a supporting line.
- P0. The home screen never auto-navigates, auto-plays audio, or shows a modal.
- P0. A visible home control is present on every child screen.

**Acceptance:** from any child screen, one tap on the top-left house returns home; home
choices remain in the same order across launches.

### FR-02 — Watch list

- P0. List all lessons from the content pack in authored order.
- P0. Show title, one-line summary, duration, and a “Watched” marker.
- P0. Tapping a lesson opens the player; returning preserves the list state.

### FR-03 — Lesson player

- P0. Video starts paused. The child must tap Play.
- P0. Play/pause, replay, back-to-videos, and home controls are always visible.
- P0. Timed captions update in sync with video position and are also spoken when voice is on.
- P0. The video never autoplays the next lesson and never loops.
- P0. On completion, show “The end.” and mark the lesson watched.
- P0. Pause on app background/lifecycle stop.
- P1. Resume at the last position after rotation.

**Acceptance:** with voice off and sound off, the lesson is still understandable through
burned-in captions and in-app captions. With sound off, audio is muted; with voice off, TTS
does not run.

### FR-04 — Counting

- P0. Present 1–5 apples and the prompt “How many apples?”.
- P0. Tapping an apple marks it counted, labels it with its ordinal, and may speak the count.
- P0. Offer three numeral options including the correct answer, sorted ascending.
- P0. A correct answer shows a specific confirmation (“Yes. three apples.”) and a Next/Finish
  button.
- P0. The first miss shows a one-to-one sequence hint; the second miss reveals the answer
  and the Next button. There is no loss, buzzer, or score.
- P0. The session length follows the caregiver setting (3 or 5 rounds).
- P0. A completed session adds one flower to the garden, once.

### FR-05 — Compare

- P0. Present two groups of 1–5 apples and ask “Which group has more/fewer?”.
- P0. Both group cards are large tap targets; the answer is not colour-only.
- P0. First miss reveals group counts; second miss outlines the correct group and labels it.
- P0. Correct feedback names both quantities (“Yes. Three is more than one.”).
- P0. A completed session adds one flower, once.

### FR-06 — Numbers

- P0. Show cards 1–10 with numeral, word, ten-frame, and narration.
- P0. Previous/Next are disabled at the ends; no wrap-around.
- P0. Narration plays when the card changes and on a “Say it again” button.

### FR-07 — Garden

- P0. Show 12 plots; bloomed plots equal completed sessions, capped at 12.
- P0. Never remove flowers, never show a streak, timer, or “come back tomorrow” pressure.
- P0. When full, say the garden is full and that play can continue.

### FR-08 — Parental gate

- P0. Settings are reachable only through a gate: press and hold for 3 seconds, **or** use
  an accessible alternative that asks an adult arithmetic question.
- P0. The gate is described as friction, not security; no purchases or external links exist
  behind it anyway.
- P0. Back always exits the gate.

### FR-09 — Settings

- P0. Voice narration on/off, gentle sounds on/off, reduce motion on/off, high contrast
  on/off, rounds per session (3/5), reset progress with confirmation.
- P0. Changes persist immediately and apply without restart.
- P0. Turning voice off stops current speech immediately.
- P0. Privacy and About text is available in plain language.

### FR-10 — Progress and settings persistence

- P0. Persist completed lesson IDs, counting sessions, comparing sessions, and settings
  locally.
- P0. Reset clears all progress but leaves settings unchanged.
- P0. Backups and device-to-device transfer are disabled.

### FR-11 — Offline content and errors

- P0. All content is bundled. The app must work in airplane mode and on first launch.
- P0. Missing/invalid content shows a plain error screen with retry, not a crash or blank
  screen.
- P0. Missing video shows “This video is not ready.” and the rest of the app still works.

### FR-12 — Narration

- P0. Narration is optional and respects the voice setting.
- P0. The app is fully usable with narration off.
- P0. TTS failure is silent and non-blocking.
- P0. Speech rate is slower than default (0.88) and language is en-US in the MVP.

### FR-13 — Content validation

- P0. Validate schema version, unique lesson IDs, video paths, caption ordering/timings,
  number-card coverage, counting values, compare rounds, and option count at load time.
- P0. The same validation runs in JVM tests against the shipped assets.

## 8. Non-functional requirements

| ID | Requirement | Target |
| --- | --- | --- |
| NFR-01 | Touch targets | ≥ 56 dp for apples; ≥ 64 dp for primary buttons; ≥ 60 dp for top-bar icons |
| NFR-02 | Text scaling | Usable at 200% font scale; all content areas scroll |
| NFR-03 | Contrast | WCAG 2.2 AA for text and interactive boundaries; colour never the only signal |
| NFR-04 | Motion | No flashing; fades ≤ 350 ms; app-level reduce-motion and system animation scale respected |
| NFR-05 | Audio | No sudden loud sounds; independent voice/sound switches; quiet default bed |
| NFR-06 | Startup | Cold start < 2 s on a mid-range 2021 device (target, to be measured) |
| NFR-07 | APK size | < 40 MB debug/release |
| NFR-08 | Reliability | No crash paths for missing content; no dead ends; back/home always work |
| NFR-09 | Privacy | Zero system permissions, zero network calls, zero third-party SDKs, backups off |
| NFR-10 | Compatibility | API 26–36; 360 dp phone and 600+ dp tablet; portrait and landscape |
| NFR-11 | Localisation readiness | All UI copy in resources; content text in versioned pack |
| NFR-12 | Maintainability | Pure session logic testable on JVM; content replaceable without code |

## 9. Content requirements

- Two lessons: counting to three, and more/fewer.
- All numbers 1–10 represented in the card set.
- Captions present for every spoken line; no audio-only information.
- Sentence length ≤ 6 words where possible.
- No idioms, sarcasm, baby talk, or generic “good job” as the only feedback.
- No flashing, rapid cuts, applause, or reward explosions.
- Content must pass `ContentValidator` and be reviewed by an educator/OT/SLP before release.

## 10. Analytics and privacy

The MVP has **no analytics**. Product learning comes from consented caregiver interviews and
observed sessions, not from device telemetry. If telemetry is ever added, it must be opt-in,
aggregate, documented in the data-safety form, and must never collect child speech, images,
names, or precise behaviour. See [metrics](metrics.md) and
[privacy/security](../03-architecture/security-privacy.md).

## 11. Dependencies and constraints

- Android platform `VideoView` for local MP4 playback.
- Device text-to-speech engine for narration. Some voices may require a network download;
  the app documents choosing an offline voice.
- Generated media assets are committed; rebuilding them needs macOS/ffmpeg/Swift.
- No backend, no accounts, no service dependencies.

## 12. Release criteria

MVP may be installed on the child's device when:

1. JVM tests, lint, and `assembleDebug` pass.
2. The [manual device script](../04-qa/manual-test-script.md) passes on the target device.
3. Caregiver has reviewed the privacy note and sensory settings.
4. Educator/OT/SLP has reviewed the counting/comparison sequence.
5. No P0 requirement is unmet.

Public store release adds the items in the
[release checklist](../05-delivery/release-checklist.md).

## 13. Open questions

- Is numeral recognition appropriate now, or should options be dot patterns first?
- Does tap-to-count invite repetitive tapping for this child?
- Is “fewer” the right word, or does the family use “less”?
- Is a flower the most meaningful progress symbol, or should it be a preferred interest?
- Does the child need a visual “finished” routine beyond “The end.”?
- Should session length be 3 rounds max initially?
