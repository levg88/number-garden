# ADR 0008 — Errorless scaffolding and no scores

- **Status:** Accepted
- **Date:** November 2026
- **Deciders:** Product/engineering, caregiver

## Context

The primary user is a five-year-old autistic child who may be sensitive to failure, loud
rewards, time pressure, and unpredictable feedback. Many children's math apps use scores,
streaks, timers, loss states, and variable rewards. Those mechanics can create distress or
compulsive use and can overshadow the math.

## Decision

Use errorless scaffolding and remove competitive mechanics:

- First miss: show a hint (sequence numbers on apples; reveal group counts).
- Second miss: show the answer gently, label it, and offer Next.
- No scores, stars, streaks, timers, countdowns, lives, or "game over".
- No buzzers, red error states, or disappointed language.
- Correct feedback names the quantity or relationship.
- The child can leave at any time; leaving records nothing and loses nothing.
- The garden grows once per completed session, never wilts, and caps at 12.

## Alternatives considered

| Alternative | Why not |
| --- | --- |
| Points/stars | Shifts attention to rewards and creates a loss state |
| Timers | Adds pressure and penalises processing time |
| Streaks | Creates anxiety about missing a day and compulsive return |
| "Try again" loop until correct | Can trap a child in repeated failure |
| Immediate answer on first miss | Removes the chance to learn from a hint |
| Randomised praise | Variable reinforcement; can increase repetitive use |

## Consequences

**Positive**

- Predictable, low-stress feedback.
- The child can recover from a miss without shame.
- No mechanic encourages extended use or return pressure.
- Caregivers can hand over the device without supervising a reward loop.
- Easier to reason about and test: the state machine has three phases.

**Negative**

- Less "exciting" than mainstream apps; may not hold attention for all children.
- No difficulty signal for a caregiver from within the app.
- Some children may find the hint/reveal too easy or too slow.
- No data to show progress beyond session counts and watched lessons.

## Follow-ups

- Observe whether tap-to-count invites repetitive tapping.
- Ask the caregiver whether 3 rounds should be the default.
- Consider a caregiver-visible, non-competitive session summary in a future version.
- Validate the approach with an educator/OT/SLP; do not claim clinical benefit.
