# ADR 0007 — TTS narration instead of baked voice

- **Status:** Accepted
- **Date:** November 2026
- **Deciders:** Product/engineering

## Context

The app needs optional spoken narration for instructions, feedback, number cards, and video
captions. Shipping recorded voice would require casting, editing, and licensing, and would
fix one voice for every family. Some children prefer no voice; some devices have poor
speakers; some families use a specific system voice.

## Decision

Use the platform `TextToSpeech` engine through a small `Speech` interface:

- Rate 0.88, pitch 1.0, `Locale.US` in the MVP.
- Respect the caregiver voice setting on every utterance.
- Speak caption text in sync with video position.
- Fail silently; no task depends on speech.
- Do not bake voice into the MP4s; they contain only a quiet chime bed.

## Alternatives considered

| Alternative | Why not |
| --- | --- |
| Recorded voice tracks | Fixed voice, larger assets, licensing, and re-recording for every copy change |
| Baked voice in video | Cannot be turned off independently, forces one voice, and duplicates captions |
| Cloud TTS | Requires network and sends text off-device; contradicts offline-only |
| No narration at all | Excludes children who benefit from audio and makes captions the only channel |
| Bundled neural TTS | Large assets and complex licensing/inference |

## Consequences

**Positive**

- No voice assets; small APK.
- Caregiver can turn voice off instantly.
- Works with the family's preferred system voice.
- Captions stay in sync because text is the source.
- App remains usable in silence.

**Negative**

- Voice quality and availability vary by device.
- Some voices require a network download by the system TTS app.
- Pronunciation of numerals/words may vary.
- No precise word-level timing or SSML in this implementation.
- TTS initialisation is asynchronous; early lines may be dropped or queued.

## Follow-ups

- Test on the target device with at least two system voices.
- Document how to install/choose an offline voice.
- Consider SSML or a slower rate for multisyllabic words if needed.
- If a recorded voice is ever added, keep TTS as the fallback and make it opt-in.
