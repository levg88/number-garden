# User stories and acceptance criteria

Format: **As a** … **I want** … **so that** …
Each story lists observable acceptance criteria. “Child” means the five-year-old primary
user; “caregiver” means the adult nearby.

## Epic A — Calm entry and exit

### A1. Predictable home (P0)

As a child, I want the same choices in the same place every time, so I know what I can do.

- Home shows Watch, Count, Compare, Numbers, My garden in a fixed order.
- No modal, video, or sound starts automatically.
- The top bar and background are identical across child screens.
- A house button appears on every child screen and returns home in one tap.

### A2. Leave at any time (P0)

As a child, I want to stop without being penalised, so I feel safe trying.

- No confirmation dialog blocks exit from a lesson or activity.
- Leaving mid-round does not reduce flowers, show a warning, or play a sound.
- System back behaves like the visible back/home control.

## Epic B — Watch

### B1. Choose a video (P0)

As a child, I want to pick a short video, so I can learn by watching.

- The list shows title, one-line summary, duration, and watched marker.
- Tapping a card opens the player for that lesson.
- Returning from the player leaves the list at the same scroll position where practical.

### B2. Control the video (P0)

As a child, I want to pause and replay, so I can go at my pace.

- Video starts paused with a large play control over the first frame.
- Play/pause and replay are always visible and ≥ 64 dp high.
- Replay restarts from 0 without autoplaying the next lesson.
- The video pauses when the app goes to the background.

### B3. Understand without sound (P0)

As a child who is sensitive to audio, I want captions, so I can watch silently.

- Captions are burned into the video and repeated in a large in-app caption panel.
- Voice and sound can be disabled independently.
- With both off, all learning information remains visible.

## Epic C — Count

### C1. Count apples (P0)

As a child, I want to touch each apple as I count, so counting is concrete.

- One to five apples are shown, large enough to tap.
- Tapping an uncounted apple labels it with the next ordinal.
- Tapping a counted apple does not double-count.
- The count resets for each new round.

### C2. Choose the numeral (P0)

As a child, I want to show the number I counted, so I connect quantity to symbol.

- Three numeral options are shown, including the answer, sorted ascending.
- A correct choice produces specific spoken/visual confirmation.
- Options lock after a correct choice or after the answer is revealed.

### C3. Get help without shame (P0)

As a child, I want help when I am stuck, so I do not feel wrong.

- First miss: sequence numbers appear under apples; a gentle “Let’s count together.” is shown
  and spoken.
- Second miss: correct numeral is outlined and the answer is stated; Next appears.
- No red X, buzzer, score reduction, or timeout exists.

### C4. Finish a session (P0)

As a child, I want a clear ending, so I know I am done.

- The final round’s button says “Finish”.
- Completion shows “You did it.”, a flower, Play again, and Back home.
- One flower is added exactly once per completed session.

## Epic D — Compare

### D1. Compare two groups (P0)

As a child, I want to choose the group with more or fewer, so I learn quantity comparison.

- Two separate, large group cards are shown.
- Prompt uses either “more” or “fewer”, not both at once.
- The child taps anywhere in a group card.
- Correct feedback names both quantities in a complete sentence.

### D2. Recover from a miss (P0)

As a child, I want a visual hint, so I can try again.

- First miss shows the count under each group and a “Match one to one.” hint.
- Second miss outlines and labels the correct group.
- No answer is marked with colour alone.

## Epic E — Numbers

### E1. Explore 1–10 (P0)

As a child, I want to see and hear each number, so I build numeral familiarity.

- Cards show numeral, word, and a ten-frame with the matching count.
- Narration plays when the card changes; “Say it again” replays it.
- Previous is disabled on 1; Next is disabled on 10.

## Epic F — Garden

### F1. See progress without pressure (P0)

As a child, I want to see what I grew, so I feel a sense of completion.

- Each completed counting/comparing session blooms one of 12 plots.
- Flowers never wilt, disappear, or reset automatically.
- A full garden says it is full and that play can continue.
- No streak, timer, daily goal, or leaderboard is shown.

## Epic G — Grown-ups

### G1. Pass the gate (P0)

As a caregiver, I want a quick gate, so my child does not wander into settings.

- Gate offers a 3-second press-and-hold and an accessible question alternative.
- A wrong answer shows “Not quite. Try again.” without locking the adult out.
- Back exits the gate.

### G2. Adjust for today (P0)

As a caregiver, I want to change sensory settings quickly.

- Voice, sound, reduce motion, and high contrast toggle with one tap.
- Rounds per session is 3 or 5.
- Changes persist and apply immediately; turning voice off stops speech.
- Reset progress requires confirmation and does not change settings.

### G3. Understand privacy (P0)

As a caregiver, I want a plain-language statement.

- Settings state offline operation, no ads, no accounts, no analytics, and local-only progress.
- The text explains that the device TTS engine is separate from the app.

## Epic H — Reliability

### H1. Open anywhere (P0)

As a caregiver, I want the app to work on a plane or in a waiting room.

- First launch works with no network.
- No permission prompt ever appears.
- All videos and images are bundled.

### H2. Recover from a bad install (P0)

As a caregiver, I want a clear message, not a crash.

- Invalid/missing content shows a plain error screen with Retry.
- Missing video shows a friendly message while the rest of the app remains usable.

### H3. Rotate and resume (P0/P1)

As a child, I want my place to survive the device moving.

- P0: no crash or lost progress on rotation; current activity can restart a round if needed.
- P1: video playback resumes near the last position after rotation.
