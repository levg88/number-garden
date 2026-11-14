# ADR 0003 — Validated JSON content pack

- **Status:** Accepted
- **Date:** November 2026
- **Deciders:** Product/engineering

## Context

Lessons, captions, number cards, counting values, and compare rounds need to be editable
without touching UI code. The content is small, read-only, and must never present a broken
or empty activity to a child.

## Decision

Ship one `assets/content.json` decoded with `kotlinx.serialization` and validated by a pure
Kotlin `ContentValidator` at load time and in JVM tests.

The pack has a `schemaVersion`, strict keys (`ignoreUnknownKeys = false`), and explicit
rules for lesson IDs, video paths, caption order/timing, 1–10 number cards, counting values,
compare rounds, session length, and option count.

## Alternatives considered

| Alternative | Why not |
| --- | --- |
| Hard-coded Kotlin content | Safe but requires a rebuild for every copy change; mixes content and UI |
| Room database seeded at first run | Adds a database and migration surface for static read-only data |
| Remote CMS | Contradicts offline-only and adds a backend |
| CSV/XML | Weaker nested structure and less natural for captions/rounds |
| Unvalidated JSON | A typo could produce an empty screen or wrong answer for a child |

## Consequences

**Positive**

- Content is reviewable as one file and diffable in Git.
- An educator can review copy without reading Kotlin.
- Validation fails fast with a readable message.
- The same validator runs in tests against the shipped asset.
- No code change for new lessons or number cards.

**Negative**

- Two sources to keep in sync: JSON timings and the actual MP4 duration.
- Strict decoding rejects unknown fields, so the model and pack must change together.
- No hot reload or remote editing.
- The validator cannot inspect media content, only paths and timings.

## Follow-ups

- Add a target/values consistency check for compare rounds.
- Add a CI check that `durationSec` is within a tolerance of `ffprobe` output when ffmpeg is
  available.
- Version the schema and document migrations.
