# ADR 0004 — Platform video player

- **Status:** Accepted
- **Date:** November 2026
- **Deciders:** Product/engineering

## Context

The app plays two short, local, 16:9 MP4s with a quiet audio bed. It needs reliable playback
on API 26–36, play/pause/replay, position tracking for captions, and no network. The APK
should stay small.

## Decision

Use the platform `VideoView` (backed by `MediaPlayer`) inside a Compose `AndroidView`.
Copy the asset to `cacheDir/videos/` once, then play from a `file://` URI.

## Alternatives considered

| Alternative | Why not |
| --- | --- |
| Media3 / ExoPlayer | Robust and feature-rich, but a large dependency for two local files; more API surface to audit |
| `MediaPlayer` directly with a `SurfaceView` | More control, more lifecycle code; no benefit at this scale |
| WebView + HTML5 video | Adds a browser engine and a network-capable component; contradicts the privacy posture |
| External player intent | Leaves the app, breaks predictability, may show unrelated UI/ads |
| Animated drawables instead of video | Would avoid codec issues but loses the authored timing and chime bed; more work to author |

## Consequences

**Positive**

- No extra dependency; small APK.
- Native codec support and hardware decoding.
- Simple play/pause/seek API.
- Easy to keep the video area inside the calm frame.

**Negative**

- `VideoView` is a legacy view; some OEMs have codec quirks.
- Caption sync needs a polling loop (120 ms) because there is no frame callback.
- Limited control over audio focus, tracks, and subtitle rendering.
- `file:///android_asset` is unreliable, hence the cache copy.
- No built-in "playback error" callback granularity; the app shows a generic message.

## Follow-ups

- Test both videos on the target device and at least one other OEM device.
- If captions, multiple audio tracks, or streaming are added, revisit Media3.
- Keep the cache copy path traversal guard and `.mp4` restriction.
