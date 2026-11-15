# Content and media pipeline

The app ships two short lesson videos, one JSON content pack, and one Rive mascot. The
videos and JSON are generated or authored in-repository; the Rive file is a MIT-licensed
sample credited in [content-license.md](../content-license.md). No build step downloads
media at build time.

## 1. What ships

```
app/src/main/assets/
├── content.json
└── videos/
    ├── count_to_3.mp4    26 s, 1280×720, 24 fps, H.264/AAC, ~310 KB
    └── more_and_less.mp4 28 s, 1280×720, 24 fps, H.264/AAC, ~327 KB
```

The MP4s are committed. A normal Android build does **not** need Swift, ffmpeg, or Xcode.

## 2. Source of truth

| Concern | Source |
| --- | --- |
| Lesson titles, summaries, captions, timings | `assets/content.json` |
| Video pixels and chime bed | `tools/render_media.swift` + `tools/make_audio.py` |
| Number cards 1–10 | `assets/content.json` |
| Counting values and compare rounds | `assets/content.json` |
| UI copy and accessibility labels | `res/values/strings.xml` |
| Visual tokens | `ui/theme/` |

Do not edit an MP4 by hand and leave the JSON stale. Change the generator or the JSON, then
regenerate and re-run tests.

## 3. Rebuilding the videos

Requirements: macOS, Xcode command line tools (`swift`), `ffmpeg`, `python3`.

```bash
./tools/build_media.sh
```

The script:

1. Creates `tools/out/`.
2. Runs `swift tools/render_media.swift tools/out` to draw frames with AppKit/CoreGraphics.
3. Runs `python3 tools/make_audio.py tools/out` to synthesize a quiet pentatonic chime bed.
4. Encodes each frame sequence and WAV with ffmpeg:
   - H.264 High profile, level 4.0
   - CRF 21, `yuv420p`
   - AAC 96 kbps
   - `-shortest`, `+faststart`
5. Writes MP4s to `app/src/main/assets/videos/`.
6. Prints duration and size with `ffprobe`.
7. Deletes `tools/out/`.

The previous build outputs in this repository were visually inspected at several frames.
Re-run the inspection after any generator change.

## 4. Generated video design

### 4.1 `count_to_3.mp4`

- 26 seconds.
- Scene 1: one apple with numeral 1 and caption "One apple."
- Scene 2: two apples with numeral 2 and caption "Two apples."
- Scene 3: three apples with numeral 3 and caption "Three apples."
- Closing: "Three apples in all." and a calm end card.
- Slow fades (~350 ms), no flashing, no rapid cuts.
- Quiet chime bed, no voice.

### 4.2 `more_and_less.mp4`

- 28 seconds.
- Opens with "Which group has more?"
- Compares three vs one, then one vs three, then four vs two.
- Uses outline and a spoken-style caption, not colour alone.
- Quiet chime bed, no voice.

Both videos are intentionally shorter than 30 seconds and loop-free. The app never
autoplays the next video.

## 5. Audio policy

- Spoken narration is **not baked into** the MP4s.
- The app reads caption text with the device TTS engine, respecting the voice setting.
- The MP4s contain only a low-volume synthesized chime bed.
- The player attenuates video volume to 35%; "Gentle sounds" off mutes it entirely.
- No music with a beat, applause, buzzer, or sudden transient.
- `tools/make_audio.py` synthesizes sine-based chimes; there are no sample libraries.

This keeps assets small, avoids shipping one fixed voice, and lets a caregiver turn voice
off without losing the visual lesson. It also means the app must keep captions in sync; the
player does this on a 120 ms loop.

## 6. Changing or adding a lesson

1. Add or edit the lesson object in `content.json`:
   - unique lowercase `id`
   - short title and summary
   - `skill` (`counting` or `comparing`)
   - `video` path matching `videos/<id>.mp4`
   - accurate `durationSec`
   - captions in ascending `atMs`, inside the duration
2. Produce the MP4:
   - 16:9, H.264/AAC, ≤ 40 s
   - burned-in text for every spoken line
   - quiet or silent audio
   - no flashing, fast cuts, or loud transients
3. Put it at `app/src/main/assets/videos/<id>.mp4`.
4. Run:

   ```bash
   ./gradlew :app:testDebugUnitTest
   ```

   The test validates the pack and asserts every referenced video exists and is > 10 KB.
5. Run the app and watch the lesson end to end, including captions and the end state.
6. Update `CHANGELOG.md` and, if the schema changed, `schemaVersion` and the model.

## 7. Validation

`ContentValidator` checks:

- `schemaVersion == 1`
- at least one lesson; unique non-blank IDs
- non-blank title and summary
- video path pattern
- `durationSec` in 5–300
- at least one caption; non-blank; ordered; inside the video
- exactly 10 number cards covering 1–10
- non-blank number words and narration
- non-empty counting values in 1–10
- compare rounds in 1–10 with unequal sides
- `maxSessionRounds` 1–10
- `optionCount` 2–4 and enough distinct values

`ContentRepository` additionally verifies each video asset opens before caching the pack.

`ContentPackTest` runs the validator against the real asset and checks file size. This is
the guard that prevents a broken pack from shipping.

## 8. Replacing the apple/flower art

The in-app art is Compose `Canvas` code in `ui/components/GardenComponents.kt`. The videos
draw their own apples in Swift so they can be encoded. If the app art changes
substantially, update `tools/render_media.swift` to match, regenerate, and re-inspect.

There are no bitmap art assets except the launcher foreground/background and the speaker
icon. The launcher is an adaptive icon defined in `res/drawable` and
`res/mipmap-anydpi-v26`.

## 9. Licensing

- Generated video, audio, and in-app artwork: CC BY-NC 4.0. See
  [content licence](../content-license.md).
- Code and generator scripts: MIT.
- No third-party fonts, music, images, or footage are included.

## 10. Checklist for media changes

- [ ] One concept per video.
- [ ] 20–40 seconds.
- [ ] 16:9, H.264/AAC, plays on API 26–36.
- [ ] Burned-in captions match the JSON captions.
- [ ] `durationSec` and `atMs` match the encoded file.
- [ ] No flashing, fast cuts, loud transients, applause, or buzzer.
- [ ] Quiet or silent audio; no voice baked in.
- [ ] Video asset exists and is > 10 KB.
- [ ] `./gradlew :app:testDebugUnitTest` passes.
- [ ] Manual playback check on the target device.
- [ ] `CHANGELOG.md` updated.
