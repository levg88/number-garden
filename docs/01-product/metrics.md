# Metrics and research plan

## Principles

1. **Measure the child's experience, not the child.** No profiles, no behavioural scoring,
   no diagnosis, no inference about disability.
2. **Offline by default.** The MVP contains no analytics SDK and no network permission.
3. **Consent is explicit and revocable.** Research notes live outside the app. No recording
   of a child without separate, informed caregiver consent.
4. **Small N, high signal.** One family observed well is more useful at this stage than a
   thousand anonymous sessions.

## North-star statement

> The child returns voluntarily, stays calm, and can do a little more independently over
> time.

This is deliberately qualitative. A high “engagement” number can mean compulsion or
distress; it is not automatically good.

## MVP measures (collected manually, not by the app)

| Measure | Source | Target for MVP acceptance | Guardrail |
| --- | --- | --- | --- |
| Independent start | Observation: time from home to first activity tap, no adult prompt | ≤ 10 s after one demonstration | If the child wanders or taps randomly, simplify home |
| Round completion | Observation: rounds finished / started | ≥ 1 round with no adult help in session 1 | A missed round is not a failure; note the barrier |
| Calm engagement | Caregiver rating 1–5 after session | ≥ 4, no visible distress | Any distress overrides engagement targets |
| Voluntary return | Caregiver diary over 2 weeks | Child asks/returns at least twice without prompting | No target that encourages compulsive use |
| Exit transition | Observation | Child can leave or accepts “The end.” within 60 s | Add a stronger routine if distress |
| Caregiver setup | Task: change voice + rounds | < 60 s without documentation | If slower, improve labels |
| TalkBack usability | Manual script | Home → Count → complete a round using TalkBack | Block release if unusable |
| Silent usability | Manual script | Complete a round with voice and sound off | Block release if information is audio-only |
| Stability | Manual + CI | No crash in 30-minute script; tests/lint green | Block release on crash |

## What we will not measure in the MVP

- Time-on-app as a success metric.
- Streaks, daily active use, retention curves, or “engagement loops”.
- Accuracy as a child-facing score.
- Any sensor, microphone, camera, or keyboard data.
- Device identifiers, advertising IDs, or location.

## Research protocol (pilot, 1–3 families)

### Before

- Caregiver call: goals, sensory triggers, calming strategies, communication mode, device.
- Explain that the app is a prototype and that stopping is always allowed.
- Obtain written consent for observation and separate consent for any recording. Default is
  no recording.

### During (2 sessions, 15–20 minutes)

Observe and timestamp:

- transition from home to activity;
- first independent tap;
- prompts required;
- signs of regulation/dysregulation (caregiver-defined);
- requests to continue or stop;
- response to “The end.”;
- whether settings were needed mid-session.

Do not coach unless the child is distressed or stuck for more than 30 seconds.

### After

- Caregiver rating and open interview: “What felt calm?”, “What felt confusing?”,
  “What would you remove?”
- Review notes with the caregiver; correct misinterpretations.
- Choose at most three changes for the next cycle.

## Decision rules

- **Ship to the child's device** if all P0 acceptance criteria pass and the caregiver is
  comfortable.
- **Iterate before public release** if any session shows distress linked to sound, motion,
  pacing, or feedback.
- **Stop and redesign** if the child repeatedly avoids the app or the caregiver cannot
  operate the settings.
- **Never** use accuracy or speed to rank the child, and never expose a “score” in the UI.

## Future telemetry policy (not implemented)

If opt-in aggregate telemetry is ever added, it must:

- be off by default and separate from app use;
- collect only coarse events (e.g. “counting session completed”) without child identity;
- document every event in the store data-safety form and privacy policy;
- be deletable by the caregiver;
- pass a privacy review with an external reviewer.
