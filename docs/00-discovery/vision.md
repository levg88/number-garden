# Discovery: vision, users, and assumptions

**Product:** Number Garden
**Status:** discovery draft for the MVP, written before implementation
**Date:** November 2026

## The problem

A five-year-old autistic child is learning early math. Off-the-shelf math apps often add
unnecessary load:

- timers, streaks, scores, and “you lost” states;
- flashing rewards, loud sounds, and surprise animations;
- several instructions on one screen, or instructions that disappear;
- small drag targets and gestures that demand motor precision;
- ads, purchases, accounts, and data collection;
- unpredictable navigation, so the child cannot tell what will happen next.

The caregiver needs something calm enough for their child on a difficult day, predictable
enough to build independence, and simple enough to hand over for five minutes without
monitoring every tap.

## Who it is for

Primary user: a five-year-old autistic child who is learning to count quantities to five,
recognise numerals, and compare groups. Communication, sensory, and motor profiles vary
widely; this MVP assumes:

- the child can see large pictures and numerals on a phone or tablet;
- the child can tap a target of roughly 56–88 dp;
- spoken English is helpful but not required;
- an adult is nearby for the first sessions and for settings.

Secondary user: a caregiver (parent, grandparent, support worker) who wants to set sensory
preferences, understand what the child did, and trust the app.

Tertiary reviewer: educator, occupational therapist, or speech-language pathologist who
reviews content and interaction design. They are not users of the app itself.

## Vision

> A calm place where numbers feel safe, predictable, and worth returning to.

The child should be able to start, do one small thing, stop, and come back without a
penalty. The caregiver should be able to hand over the device without worrying about ads,
purchases, or data.

## Product hypotheses

We believe that:

1. A **fixed visual frame** and predictable sequence reduce anxiety and increase independent
   starts.
2. **Low-arousal feedback** (spoken confirmation, a slow outline, a flower) is more usable
   than confetti or prize animations for this child.
3. **Errorless scaffolding** (hint, then reveal) keeps practice going without shame or
   escalation.
4. **Short video + hands-on practice** supports transfer from watching to doing.
5. **A finite garden** is a satisfying, non-competitive progress signal; it will not create
   pressure to “grind” sessions.
6. **Caregiver-controlled audio/motion/contrast** makes the same app usable across
   sensory states and environments.

These are hypotheses, not established clinical outcomes. They require observation with the
child and review by professionals.

## MVP scope

In:

- counting 1–5 with tap-to-count and numeral choice;
- comparing groups of 1–5 using *more* and *fewer*;
- number cards 1–10 with ten-frames;
- two short videos with captions and optional narration;
- a 12-flower progress garden;
- parental gate and sensory settings;
- fully offline, no system permissions, no accounts.

Out:

- addition/subtraction symbols and equations;
- adaptive difficulty or machine learning;
- multiple child profiles;
- cloud sync, accounts, social features;
- data dashboards, export, or clinical reports;
- localization beyond English;
- switch access, AAC integration, or eye gaze;
- camera, microphone, or any sensor use.

## Assumptions to test with the caregiver

| # | Assumption | How to test | Risk if wrong |
| --- | --- | --- | --- |
| A1 | The child reads/tolerates numerals 1–10 | Caregiver interview + first-session observation | Counting options must become dot patterns |
| A2 | Tapping each apple is understandable and not repetitive/stimming | Observe 2 sessions | Replace tap-to-count with a single “count with me” |
| A3 | 3–5 rounds is the right session length | Offer 3 vs 5; observe fatigue | Sessions feel too long or too short |
| A4 | Spoken narration is preferred over silence | Toggle voice; observe | Voice may distract or compete with other AAC |
| A5 | A flower is a meaningful reward | Observe garden visit | Reward may be ignored or create rigidity |
| A6 | The child can transition away at the end without distress | Observe endings | Need a stronger “finished” ritual or timer warning |
| A7 | The parental gate is usable by the caregiver and not by the child | Test both | Gate too weak or too frustrating |

## Risks and mitigations

| Risk | Impact | Mitigation |
| --- | --- | --- |
| App increases sensory load | Child distress, abandonment | Muted visuals, no flashing, independent sound/voice/motion, manual device testing |
| Reward becomes compulsive | Unhealthy use | No streaks, no variable rewards, finite garden, no notifications |
| Content is developmentally wrong | Frustration or boredom | Caregiver + educator review, short sessions, visible scaffolding |
| Caregiver cannot customize enough | App unusable for this child | Voice/sound/motion/contrast/round count; roadmap for more |
| TTS voice is unpleasant or mispronounces | Distraction | Voice off; captions; offline voice choice documented |
| Privacy claim is overstated | Loss of trust | No internet permission, no SDKs, manifest check in release checklist |
| Parental gate treated as security | Unsafe assumption | Explicitly documented as friction; no purchases or external links exist anyway |

## Research plan (before public release)

1. **Caregiver interview (45 min).** Current math interests, sensory triggers, calming
   strategies, communication supports, device, typical session length.
2. **Two observed sessions** in the child's usual environment. No video recording without
   separate consent. Notes focus on: latency to first tap, independent completion, signs of
   distress, requests to continue, and transitions away.
3. **Educator/OT/SLP review** of the counting sequence, comparison language, and numeral
   presentation.
4. **Iterate** on at most three issues per cycle. Re-test with the same child only if the
   caregiver agrees.

No data is collected by the app. Research notes stay outside the app and follow the
caregiver's consent preferences.

## Definition of success for the MVP

- The child can start a session and complete at least one round with no adult prompting
  after the first demonstration.
- The caregiver can find and change voice/sound settings without documentation.
- No moment in a 15-minute observed session produces visible distress attributable to the
  app (caregiver judgement).
- The app works in airplane mode, after rotation, after backgrounding, and with TalkBack
  and 200% font scale.
