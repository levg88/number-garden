# Roadmap and release gates

Number Garden is intentionally small. The roadmap is ordered by risk reduction, not feature
count. Nothing below is a commitment to a date.

## 1. Where the project is

| Area | State |
| --- | --- |
| Product scope | MVP frozen |
| UX and design system | Implemented |
| Architecture | Implemented, documented |
| App screens | Implemented |
| Content pack + 2 videos | Implemented and validated |
| Unit tests | Passing |
| Lint / debug build | Passing locally |
| Compose smoke test | Written, not run (no device/emulator) |
| Manual device script | Written, not run |
| Release build | Not signed or installed |
| Store listing | Draft only |

## 2. Phase 0 — Device validation (next)

**Goal:** prove the app works on the actual device and is comfortable for the child.

- [ ] Run the [manual device test script](../04-qa/manual-test-script.md) on the target
      device.
- [ ] Fix S1/S2 issues; re-run affected sections.
- [ ] Check video playback on at least one additional OEM device.
- [ ] Check TTS with the family's preferred voice and with voice off.
- [ ] Check 200% font scale, TalkBack, high contrast, reduce motion.
- [ ] Have the caregiver review the privacy note and sensory settings.
- [ ] Have an educator/OT/SLP review the counting/comparing sequence and language.
- [ ] Record whether tap-to-count invites repetitive tapping.
- [ ] Decide whether 3 rounds should be the default.

**Gate:** no S1/S2 defect; caregiver and professional review complete. Then the MVP may be
installed for home use.

## 3. Phase 1 — Caregiver feedback (2–4 weeks of use)

**Goal:** learn from real use without telemetry.

- [ ] Short, consented caregiver interview after the first week.
- [ ] Observe one session if the family is comfortable.
- [ ] Note which activities the child chooses, how they stop, and any distress.
- [ ] Review the garden as a progress symbol.
- [ ] Check whether "fewer" matches family language.
- [ ] Collect no device data; notes stay with the caregiver.

**Gate:** a written summary of what to keep, change, or remove. No feature is added without a
problem statement from this phase.

## 4. Phase 2 — Public beta readiness

Only if the family wants to share the app beyond their home.

### Product and content

- [ ] Replace or extend content based on Phase 1.
- [ ] Add a target/values consistency check for compare rounds.
- [ ] Consider a visual "finished" routine if the child needs one.
- [ ] Review all copy with an educator/SLP.
- [ ] Decide whether to support "less" as an alternative to "fewer".

### Accessibility

- [ ] Fix any TalkBack duplicate-focus or ordering issues.
- [ ] Verify 200% font on the smallest supported width.
- [ ] Add screenshot tests for key screens if the design is stable.
- [ ] Document switch-access and AAC limitations clearly.
- [ ] Consider caption size/background options.

### Engineering

- [ ] Add a CI job that runs `lintDebug` and `assembleRelease` (unsigned) on every PR.
- [ ] Add dependency review and a permission check on the merged manifest.
- [ ] Add a content duration check using `ffprobe` when available.
- [ ] Add a release signing process with a protected key.
- [ ] Add baseline profiles only if startup measurement shows a need.
- [ ] Decide whether to keep `VideoView` or move to Media3 after device data.

### Release

- [ ] Complete the [release checklist](release-checklist.md).
- [ ] Prepare store listing, screenshots, and data-safety form.
- [ ] Run a closed test with 2–5 families.
- [ ] Provide a plain-language privacy note and support contact.

**Gate:** closed test feedback shows no S1/S2 issues and caregivers can operate the gate and
settings without help.

## 5. Later / exploratory

These are not commitments. Each needs a problem statement, a design pass, and a new ADR if it
changes architecture or privacy.

| Idea | Why it might help | Risk |
| --- | --- | --- |
| Switch-access scanning | Access for children who cannot tap | New interaction model, significant design work |
| AAC symbol input | Meet children where they communicate | Requires SLT collaboration |
| Audio-first activity mode | Access without relying on sight | Hard to make math meaningful by audio alone |
| Localisation | Reach non-English families | Translation can change math vocabulary |
| Caregiver session summary | Show non-competitive progress | Could become surveillance or pressure |
| More lessons (6–10) | Extend the sequence | Content review and sensory load |
| Preferred-interest themes | Motivation | May narrow generalisation; needs caregiver input |
| Android tablet layout tuning | Larger targets and spacing | Design and test effort |
| Wear/companion | Unlikely | Out of scope; adds surfaces |

## 6. Explicit non-goals

- Streaks, leaderboards, scores, timers, or loss states.
- Ads, purchases, accounts, cloud sync, or social features.
- Remote content or over-the-air updates.
- Analytics or behavioural telemetry.
- Clinical claims, diagnosis, or treatment.
- Multiple child profiles in the near term.
- iOS unless the product direction changes.

## 7. Release gates (summary)

| Gate | Criteria |
| --- | --- |
| G0 — Code | Unit tests, lint, debug build pass |
| G1 — Device | Manual script passes; no S1/S2 |
| G2 — Caregiver | Privacy and sensory review complete |
| G3 — Professional | Educator/OT/SLP content review complete |
| G4 — Beta | Closed test complete; store assets and data-safety form ready |
| G5 — Public | Signed release installed and spot-checked; checklist complete |

## 8. Risks to the roadmap

| Risk | Mitigation |
| --- | --- |
| No device available for testing | Borrow/emulate; do not ship before manual pass |
| Child finds the app aversive | Stop; treat as a failed hypothesis, not a child failure |
| Content feels too easy or too slow | Caregiver-tunable rounds; observe before adding difficulty |
| Scope creep toward a general math app | Re-read the PRD non-goals; require evidence |
| Store review or data-safety mismatch | Verify the shipped artifact, not the draft |
| Single maintainer | Keep docs, tests, and generators current; avoid clever code |

## 9. Decision log

Architecture decisions live in [ADRs](../03-architecture/adr/). Product decisions and
changes are recorded in [CHANGELOG.md](../../CHANGELOG.md). Add a new ADR for any change to
offline-only, no-telemetry, errorless feedback, or the content schema.
