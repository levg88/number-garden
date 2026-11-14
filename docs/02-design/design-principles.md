# Design principles

These principles are the tie-breakers for product and engineering decisions. When two
options are both “fine”, choose the one that is more predictable, quieter, and easier to
leave.

## 1. Predictability over surprise

The same cream frame, the same top bar, the same ending, the same order of choices.
Surprise is not a feature here. No random rewards, no “mystery” taps, no rotating tips.

**In practice:** fixed home tiles, fixed position for the house control, no autoplay, no
modals, linear activity structure, same scene-change rhythm in every video.

## 2. One thing at a time

A screen asks one question, offers one primary action, and shows one feedback line. If a
parent needs to read three sentences to understand a screen, a child cannot use it.

**In practice:** the counting screen has a prompt, apples, three numerals, one feedback
area, and one Next button. Settings are separated from play.

## 3. Low arousal by default

Muted colours, generous space, no flashing, no zoom punches, no confetti, no sudden sounds.
Excitement is not the same as engagement.

**In practice:** cream paper, forest green, soft red apples, 200–350 ms fades, quiet
pentatonic chimes, no sound above a gentle level.

## 4. Two channels, audio optional

Every instruction is visible; narration is a convenience. A child who is non-speaking,
wearing headphones, in a quiet waiting room, or sound-sensitive must still be able to play.

**In practice:** burned-in video captions + in-app caption panel, spoken prompts, on-screen
text that mirrors feedback, independent voice and sound switches.

## 5. The child controls the pace

Pause, replay, and leave are always available. Nothing advances on a timer. Nothing is
lost by stopping.

**In practice:** video starts paused; no auto-advance; Next appears only after an answer;
mid-round exit is harmless; no confirmation dialogs for leaving.

## 6. Errorless learning

A mistake is information, not failure. First hint, then reveal. The child always reaches a
successful ending.

**In practice:** first miss adds sequence numbers; second miss outlines the answer and says
it; no red X, buzzer, score, or timeout. Next is available after the reveal.

## 7. No scores, timers, streaks, or loss

Numbers are for thinking, not for ranking. The garden grows but never wilts. There is no
“daily goal” and no variable-ratio reward loop.

**In practice:** no counters on the home screen, no accuracy percentage, no notifications,
12 flowers max, “full” is a calm ending rather than a reason to grind.

## 8. Motor simplicity

One-finger taps on large targets. No drag, pinch, long-press (except the adult gate),
double-tap, or time-critical gesture.

**In practice:** apples 44–64 dp with 8 dp gaps; numeral buttons ≥ 88 dp; primary actions
≥ 64 dp; all content scrolls rather than clips.

## 9. Repetition is welcome, but content is finite

Children may want the same video ten times. That is allowed. The app does not push novelty
or punish repetition.

**In practice:** replay is prominent; lesson list is short; garden fills and stays; no
“new content” badge.

## 10. Grown-ups own the settings; children own the play

Sensory needs change day to day. A caregiver needs fast control, but the child should not
fall into settings or lose progress accidentally.

**In practice:** gate before settings; one-tap switches; explicit confirm before reset;
plain-language privacy note; settings persist immediately.

## Anti-patterns explicitly rejected

- Confetti, coins, stars, loot boxes, prize wheels.
- “You lost!” or sad-face feedback.
- Countdown timers, speed bonuses, streak flames.
- Background music that cannot be turned off independently.
- Ads, upsells, “rate us” prompts, external links.
- Autoplay next video.
- Randomised UI positions.
- Collecting names, ages, photos, voice, or behaviour without explicit consent.

## Review checklist for any new screen

1. Can a child understand the primary action without reading?
2. Is there exactly one primary action?
3. Is every instruction available visually and (optionally) spoken?
4. Can the child leave in one tap, with nothing lost?
5. Is any animation or sound load-bearing? If yes, add a non-motion/non-audio equivalent.
6. Does anything imply failure, loss, urgency, or competition?
7. Does it work at 200% font scale, with TalkBack, and in silence?
8. Would this still feel calm on a difficult day?
