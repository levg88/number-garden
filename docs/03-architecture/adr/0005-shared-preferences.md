# ADR 0005 — SharedPreferences over Room

- **Status:** Accepted
- **Date:** November 2026
- **Deciders:** Product/engineering

## Context

The app persists five settings and three progress counters. It does not need queries,
relationships, per-round history, or migrations. The data is read at startup and written on
user action.

## Decision

Use two private `SharedPreferences` files:

- `number_garden_progress`: completed lesson IDs, counting sessions, comparing sessions.
- `number_garden_settings`: voice, sound, reduce motion, high contrast, rounds per session.

Repositories expose `StateFlow` and update memory before writing with `apply()`.

## Alternatives considered

| Alternative | Why not |
| --- | --- |
| Room | Adds a database, schema, DAO, and migration surface for eight scalars |
| DataStore | Modern and coroutine-friendly, but a new dependency and type-safe schema for a tiny payload; `SharedPreferences` is sufficient |
| Files (JSON) | Manual parsing and atomicity concerns; no benefit |
| In-memory only | Progress and settings would reset on process death |
| Encrypted preferences | No sensitive data; key management would add complexity without a threat to mitigate |

## Consequences

**Positive**

- No dependency, no schema, no migration.
- Simple, synchronous reads at startup.
- Easy to inspect and reset.
- Works offline and survives process death.

**Negative**

- `SharedPreferences` is not type-safe; keys are strings.
- No transactional multi-key updates.
- No built-in flow; repositories wrap it in `MutableStateFlow`.
- Renaming keys needs a manual migration.
- Not suitable if session history is added later.

## Follow-ups

- Keep keys in private companion objects.
- Coerce stored values on load (negative counts, out-of-range rounds).
- Revisit DataStore if the data grows or multi-process access is needed.
- Never store child-identifying data here; the current fields are counters and booleans.
