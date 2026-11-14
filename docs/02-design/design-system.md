# Design system

The design system is intentionally tiny. It exists so every new screen feels like the same
quiet place.

## Colour

All UI colours come from `ui/theme/Color.kt` and the Material 3 scheme. The app stays in a
calm light theme even when the device is dark, because a sudden dark inversion is a sensory
change the child did not ask for. High contrast is an explicit caregiver setting.

| Token | Hex | Use | Contrast (on cream `#FAF5E9`) |
| --- | --- | --- | --- |
| Background | `#FAF5E9` | Paper, page background | — |
| Surface | `#FFFFFF` | Cards, panels, buttons | — |
| Ink | `#22302A` | Primary text | 12.66:1 |
| Muted ink | `#4A5A50` | Supporting text | 6.73:1 |
| Primary green | `#2F5D46` | Primary actions, prompts, highlights | 6.96:1 |
| Outline | `#6B7468` | Control boundaries | 4.46:1 |
| Soft outline | `#D8CEBB` | Decorative separators only | — |
| Amber (accent) | `#B8741F` | Large numerals/sparing accent | 3.47:1 — large/graphical use only |
| Apple red | `#C9564B` | Illustration only | 3.93:1 — never text |
| Correct soft | `#DCEBDD` | Positive feedback container | Ink on it: 10.12:1 |
| Error | `#9B3F36` | Adult error text | 6.11:1 |
| High-contrast ink | `#000000` on `#FFFFFF` | High contrast mode | 21:1 |

Rules:

- Never use colour as the only signal. Correct answers get an outline **and** a sentence;
  counted apples get a numeral badge; highlighted groups get an outline **and** the words
  “This group.”
- Do not put body text in amber. Amber is for large decorative accents and the flower
  centre.
- Error colour is for adult-facing settings messages only. Child feedback never turns red.

## Typography

System sans (Roboto on Android), no decorative or handwriting fonts. Larger than typical
Material defaults:

| Style | Size / line height | Use |
| --- | --- | --- |
| Display large | 84 / 92 sp | Number-card numeral |
| Display medium | 64 / 72 sp | Counting numeral options |
| Headline large | 34 / 42 sp | Screen prompt, completion |
| Headline medium | 28 / 36 sp | Top-bar title, section |
| Title large | 25 / 32 sp | Card title, captions |
| Title medium | 21 / 28 sp | Settings labels |
| Body large | 21 / 30 sp | Instructions |
| Body medium | 18 / 26 sp | Supporting text |
| Label large | 21 / 28 sp | Buttons |

- All text uses `sp`, so system font scaling applies.
- No text is placed over a busy illustration. Captions sit on a solid panel.
- Sentence case, never all caps.
- Maximum two lines in a top-bar title; ellipsis if needed.

## Spacing and shape

- Base unit: 4 dp. Common gaps: 8, 12, 14, 16, 18, 20, 24.
- Screen horizontal padding: 20 dp; vertical: 12–16 dp.
- Card radius: 28 dp (large), 20 dp (medium), 14 dp (small).
- Border: 1 dp soft outline; 2–3 dp when selected/revealed or high contrast.
- Elevation: none or 1 dp. Shadows are not used to communicate state.

## Components

| Component | Spec | Notes |
| --- | --- | --- |
| `GardenScaffold` | Safe insets, cream background, fixed top bar | Every screen uses it |
| Top-bar icon button | 60 dp, 30 dp icon, white circular container | Home/back always present |
| `BigActionTile` | Min 164 dp tall, 28 dp radius, art + title + hint | Home only; 2-column |
| `NumberButton` | Min 88 dp tall, 64 sp numeral, 3 dp border when emphasised | Counting and adult gate |
| `GardenButton` | Min 64 dp tall, filled or outlined | Next, Finish, replay, etc. |
| Apple art | 44–64 dp, red fruit with leaf and stem | Tappable in Count; decorative elsewhere |
| Ten-frame | 2×5 cells, 46 dp each, filled cells contain an apple | Numbers screen |
| Flower art | 82 dp in garden plots; 132 dp on completion | Varied muted petal colours |
| Feedback area | Min 56 dp tall, polite live region | Never shifts the layout |
| Settings row | Min 76 dp, whole row toggles | Switch is visual, row owns semantics |

## Motion

- Screen navigation is an instant cut (no slide/fade). This is predictable and cheap.
- Scene fades in videos are 350 ms.
- No bouncing, pulsing, parallax, shaking, or looping decorative animation.
- No confetti, particles, or reward explosions.
- Reduce motion respects both the app setting and the system animation scale. Meaning is
  never carried by motion.
- Loading uses a standard circular progress indicator; it is not decorated.

## Sound

- One optional voice: platform TTS at 0.88 rate, en-US. Specific, calm feedback.
- One quiet chime bed in videos, synthesized, no sudden transients.
- Video volume is attenuated to 35% and muted when “Gentle sounds” is off.
- Voice and sound are independent switches. Both off is a fully supported mode.
- No background music, no applause, no error buzzers.

## Iconography and illustration

- Illustration is code-drawn: apples, flowers, a simple play rectangle, a speaker icon.
- No emoji in UI (rendering varies by device and can look chaotic).
- Icons are filled, 24–46 dp, always paired with a label or content description.
- Artwork is decorative unless it is the tap target; the tap target always has a
  content description and a visible state.

## Touch and layout

- Primary targets ≥ 64 dp; numeral buttons 88 dp; apples 44–64 dp with 8 dp gaps.
- One-finger taps only. No drag, swipe, pinch, or timing.
- All content areas scroll. Never rely on a fixed height that clips at 200% font scale.
- Safe drawing insets on every root screen.

## Content density

- One prompt, one visual set, one response control, one feedback line.
- No badges, banners, carousels, or promotional surfaces.
- No more than five choices on home; no more than three response options in an activity.

## Do / don’t

| Do | Don’t |
| --- | --- |
| “Yes. Three apples.” | “Awesome!!! You’re a math star!” |
| Outline the correct group and say it | Flash it green three times |
| Let the child replay the same video | Offer a “new!” badge or count down |
| Keep the house button in the same place | Hide navigation in a gesture |
| Use a slow fade | Bounce, spin, or confetti |
