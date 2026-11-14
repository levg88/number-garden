# Accessibility and sensory accommodations

Number Garden is designed for one child with specific sensory and motor needs, not for a
general audience. This document records what the app does, what it deliberately does not do,
and what still needs testing with the child, caregiver, and treating professionals.

> **Important:** these are design intentions and accessibility accommodations. The app is
> not a medical device and makes no therapeutic claims.

## 1. Access needs this design assumes

| Need | Design response |
| --- | --- |
| Predictable layout | Every screen uses the same cream frame, top bar, and back/home positions |
| Low sensory load | Muted palette, no flashing, no confetti, no buzzers, quiet audio |
| Auditory processing differences | Every spoken line also appears as text; voice can be switched off |
| Motor differences | Large targets, single taps only, no drag/swipe/timing; tap-to-count is optional |
| Attention differences | One prompt, one task, 3 or 5 rounds, no timers or streaks |
| Need to stop | Pause, replay, back, and home are always available; nothing auto-advances |
| Error sensitivity | Errorless scaffolding: hint first, answer second, never a loss state |
| Visual sensitivity | No dark-mode inversion, no animated backgrounds; reduce-motion setting |
| Low vision | High-contrast caregiver setting, large type, system font scaling |
| Screen-reader use | TalkBack labels, headings, live-region feedback, switch/radio semantics |

## 2. Implemented accommodations

### 2.1 Text and scaling

- All text uses `sp` and the app theme's typography, so Android font scaling applies.
- Every content area is inside `verticalScroll`; no child screen relies on a fixed height
  that would clip at 200% font scale.
- At 150%+ font scale the home actions switch from a two-column grid to one full-width tile
  per row, the Numbers Previous/Next buttons stack, and the parental-gate hold circle grows
  so labels keep their word shape. This was verified at 200% on the emulator.
- Top-bar titles are limited to two lines with ellipsis; the full lesson title remains in the
  content pack and is read by the screen reader.
- Sentence case throughout. No all-caps labels.
- Text is never placed over illustration; captions sit on a solid surface panel.

### 2.2 Touch targets and input

- Primary buttons: minimum 64 dp tall (`GardenButton`).
- Top-bar icons: 60 dp with a 30 dp icon.
- Numeral buttons: minimum 88 dp tall.
- Apples: 44–64 dp, spaced ≥ 8 dp; tapping is optional and no apple is required to answer.
- Group cards: minimum 132 dp tall.
- All interactions are single taps. There are no swipes, long presses (except the optional
  adult gate), drags, pinches, or time limits in child activities.
- The adult gate deliberately uses a 3-second press **and** offers an arithmetic
  alternative for users who cannot press and hold.

### 2.3 Colour and contrast

- Normal theme targets WCAG 2.2 AA for text and control boundaries.
- High-contrast mode is an explicit caregiver setting, not an automatic dark-mode response.
- Colour is never the only signal:
  - A correct numeral is outlined **and** followed by a sentence.
  - A counted apple gets a numeral badge **and** an ordinal content description.
  - The correct compare group is outlined **and** labelled "This group."
- Child-facing feedback never uses red. Error colour appears only in adult settings.

### 2.4 Motion

- Navigation uses instant cuts, not slides or shared-element transitions.
- The only child-visible animation is a short, gentle flower growth on completion and the
  videos' own fades.
- `rememberMotionAllowed()` requires both the caregiver "Reduce motion" setting and the
  system animator scale to allow motion. If either is off, the app skips the animation but
  keeps the same information on screen.
- No pulsing, bouncing, parallax, looping decoration, confetti, or particle effects.
- No content flashes. The videos were generated with slow cuts and no strobe.

### 2.5 Sound and voice

- Voice narration and gentle sounds are independent switches.
- Both can be off; the app remains fully usable through text and captions.
- TTS rate is 0.88 and pitch 1.0 for a slower, flatter delivery.
- Video audio is attenuated to 35% and muted entirely when sounds are off.
- There are no error buzzers, applause, or sudden transients.
- Turning voice off stops current speech immediately.
- TTS failure is silent and does not block any task.

### 2.6 Screen reader and semantics

- Each screen has a heading for its title or prompt.
- Decorative art is excluded from the accessibility tree or given a meaningful description
  only when it is also the tap target.
- Feedback text uses a polite live region so TalkBack announces it without interrupting.
- Progress dots are hidden from the accessibility tree; the adjacent "3 of 5" text carries
  the same information.
- Settings rows use `Role.Switch` and toggle the whole row; the visual `Switch` is hidden to
  avoid a duplicate focus stop.
- Rounds-per-session options use `Role.RadioButton` with a selected state.
- Apples expose "Apple" or "Apple 3" and the field groups them logically.
- Number cards expose "3. three." as one merged description.
- Compare groups expose "Group 1. 3 apples." and, after teaching, "This group."
- Video play/pause/replay icons have explicit content descriptions.
- The adult gate's press-and-hold target has a button role, a description, and a progress
  announcement while held.

### 2.7 Cognitive and emotional access

- The same five child choices appear in the same order on home.
- One instruction at a time; no nested menus in child activities.
- The child cannot "lose". After one miss a hint appears; after two, the answer is shown and
  the Next button appears.
- No scores, stars, timers, streaks, countdowns, or "you missed" language.
- The garden never wilts, never resets by itself, and stays full once complete.
- Leaving mid-session is always allowed and is never punished.
- The completion screen is a short, predictable ritual: flower, "You did it.", replay/home.

### 2.8 Offline and failure access

- No permission prompt ever interrupts play.
- No network dependency, so behaviour is identical in airplane mode.
- A broken content pack shows a plain adult-readable error with retry and home, not a crash
  or blank screen.
- A missing video shows "This video is not ready." and the rest of the app still works.

## 3. Deliberate exclusions in the MVP

| Not included | Why | Where it is considered |
| --- | --- | --- |
| Switch access / scanning | Needs a different interaction model and user testing | Roadmap |
| AAC symbol input | Out of scope; would need SLT collaboration | Roadmap |
| Screen-reader-first activity flow | Activities are visual-first; TalkBack can operate controls but the math task still needs sighted support | Roadmap |
| Caption customisation (size, background) | Burned-in captions are fixed; in-app captions follow theme | Roadmap |
| Colour-blind palettes | Palette avoids red/green as the only signal; not separately tested | QA backlog |
| Haptic feedback | Can be startling; not used | Not planned |
| Voice control | Platform voice access can operate labelled controls; not explicitly tested | QA backlog |
| Localisation / RTL | English only in MVP; strings are externalised | Roadmap |
| Dynamic type beyond 200% | Layout is scrollable but not verified above 200% | Manual test |

## 4. Verification status

Automated coverage is limited to JVM logic and a Compose smoke test. Accessibility claims in
this document are **design intentions until the manual script is run on the target device**.

An Android 16 (API 36) emulator pass on 14 November 2026 exercised every child screen at
100% and 200% font scale, including the 200%-scale layout fallbacks above. TalkBack, rotation,
true insets, and OEM playback remain unverified on hardware. See
[HANDOFF.md](../HANDOFF.md) for the evidence and open items.

The [manual device test script](../04-qa/manual-test-script.md) includes checks for:

- TalkBack traversal order and duplicate focus stops
- 200% font scale on every child screen
- high-contrast mode
- reduce motion (app setting and system animator scale)
- voice off / sound off / both off
- airplane mode
- rotation and background/resume
- adult gate with press-and-hold and with the arithmetic alternative
- reset progress and return to an empty garden

## 5. Known risks and open questions

1. **Tap-to-count may invite repetitive tapping.** Counting is optional, but the ordinal
   badges may still reinforce repetition. Observe and consider removing them if so.
2. **"Fewer" may not be the family's word.** The content pack can be edited, but the UI string
   is fixed in this build.
3. **TTS voice quality varies by device.** A poor voice may be more distracting than silence.
   The caregiver can turn voice off.
4. **Video is the highest sensory load in the app.** It is opt-in, pausable, and short; the
   caregiver should preview both videos first.
5. **Screen-reader math tasks remain hard.** TalkBack can operate the controls, but comparing
   groups still depends on the visual array. A future audio-first mode needs design work.
6. **The adult gate is friction, not security.** It is designed to be operable by an adult
   with motor differences via the arithmetic alternative, which also makes it easier for a
   determined older child. Nothing harmful sits behind it.
7. **No clinical validation.** An educator/OT/SLP should review the sequence, language, and
   sensory assumptions before the app is used as more than a calm practice activity.

## 6. Review checklist for caregivers and professionals

- [ ] Watch both videos end to end and note any sensory discomfort.
- [ ] Try the app with voice off and with sound off.
- [ ] Try 200% font scale and high contrast.
- [ ] Confirm the child can stop at any point without distress.
- [ ] Confirm the hint/reveal sequence matches the child's current support level.
- [ ] Confirm the words "more" and "fewer" match home and classroom language.
- [ ] Confirm the flower is meaningful, or replace the progress symbol in a future build.
- [ ] Run the manual device script and record results.
