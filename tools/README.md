# Content media pipeline

The app ships with two short, quiet lesson videos. They are generated from code so
the repository has no third-party media licensing obligations.

## Rebuild

```bash
./tools/build_media.sh
```

Requirements: macOS with Xcode command line tools (`swift`), `ffmpeg`, `python3`.
The generated `app/src/main/assets/videos/*.mp4` files are committed, so an
ordinary Android build needs none of these tools.

## What is generated

- `count_to_3.mp4` — 26 s, 1280×720, 24 fps. One, two, then three apples with
  numerals and captions. Gentle fade between scenes; no flashing.
- `more_and_less.mp4` — 28 s, 1280×720, 24 fps. Asks “Which group has more?”,
  then narrates two comparisons with a calm highlight.

Audio is a synthesized pentatonic chime bed at low volume. Spoken narration is
**not** baked into the videos: the app uses the device's text-to-speech voice for
captions, respecting the “Voice narration” setting. This keeps asset size down,
lets narration follow the child's voice settings, and avoids shipping a single
fixed voice.

## Replacing the videos

1. Produce H.264/AAC MP4s with the same filenames, 16:9, under 30 seconds.
2. Keep audio optional and quiet; no sudden loud sounds or flashing.
3. Update `durationSec` and caption timings in `app/src/main/assets/content.json`.
4. Run `./gradlew :app:testDebugUnitTest` — the content validator checks timings
   and that every referenced video exists.

Design constraints for any replacement content:

- One concept per video.
- No rapid cuts, zoom punches, applause, or reward animations.
- Captions burned in **and** available as timed text.
- Ends on a calm, predictable “The end.” card.
