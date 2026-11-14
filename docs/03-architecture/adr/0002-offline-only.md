# ADR 0002 — Offline-only, no accounts or telemetry

- **Status:** Accepted
- **Date:** November 2026
- **Deciders:** Product/engineering

## Context

The primary user is a five-year-old autistic child. Families and reviewers need to trust
that handing over the device does not create privacy, advertising, or compulsive-use risks.
The app's content is small and static.

## Decision

The app is fully offline:

- No `INTERNET` permission and no network libraries.
- No accounts, cloud sync, ads, analytics, crash reporting, or attribution.
- All content is bundled in `assets/`.
- All state is local `SharedPreferences`.
- Backups and device-to-device transfer are disabled.
- Product learning comes from consented caregiver interviews and observed sessions, not
  telemetry.

## Alternatives considered

| Alternative | Why not |
| --- | --- |
| Anonymous analytics | Still creates a behavioural record about a child and a data-safety obligation; not needed for a five-screen app |
| Crash reporting | Convenient, but adds an SDK and network permission; local testing and a manual script are sufficient for MVP |
| Remote content updates | Adds a backend, update safety, and network risk; content is small enough to ship in the APK |
| Accounts/cloud sync | No multi-device requirement; creates personal data and retention duties |
| Ads/freemium | Incompatible with the product's sensory and trust goals |

## Consequences

**Positive**

- Strong, simple privacy story that a caregiver can verify.
- Works in airplane mode and on first launch.
- No server cost, no auth, no data breach surface.
- No permission prompts interrupt play.
- Small attack surface and dependency graph.

**Negative**

- Content changes require an app update.
- No crash visibility from the field.
- No usage data to prioritise features.
- TTS may still use a network voice outside the app; this must be documented.
- Bug reports depend on caregivers describing issues.

## Follow-ups

- Keep the privacy note in Settings accurate.
- Review any new dependency for network behaviour.
- If telemetry is ever proposed, require a new ADR, opt-in consent, aggregate data only, and
  a data-safety review.
