# Screen map and flows

## Screen map

```mermaid
flowchart TD
    Home[Home]
    Watch[Watch list]
    Player[Lesson player]
    Count[Count activity]
    Compare[Compare activity]
    Numbers[Number cards 1–10]
    Garden[My garden]
    Gate[Grown-ups gate]
    Settings[Settings]

    Home --> Watch
    Home --> Count
    Home --> Compare
    Home --> Numbers
    Home --> Garden
    Home --> Gate
    Watch --> Player
    Gate -->|hold 3s or adult question| Settings
    Player -->|back| Watch
    Settings -->|home| Home
    Gate -->|back| Home
```

All child screens share the same top bar. The left control is Home (or Back on the lesson
list), and it is never hidden. The nav host uses no screen transitions, so navigation is an
instant, predictable cut.

## First launch

```mermaid
sequenceDiagram
    participant C as Child
    participant App
    C->>App: Tap icon
    App->>App: Load bundled content, validate, check videos
    alt Content valid
        App-->>C: Home (cream, five choices, no sound)
    else Content invalid
        App-->>C: Plain error + Retry (no crash)
    end
```

No onboarding wizard. No permission prompt. No account. The caregiver is expected to review
settings first, but the child can start immediately.

## Counting loop

```mermaid
stateDiagram-v2
    [*] --> Ask
    Ask --> Ask: Wrong choice (miss 1)<br/>sequence hints appear
    Ask --> Teach: Wrong choice (miss 2)<br/>answer revealed
    Ask --> Correct: Correct choice
    Correct --> NextRound: Next
    Teach --> NextRound: Next
    NextRound --> Ask: more rounds
    NextRound --> Complete: last round
    Complete --> [*]: flower + Play again / Home
```

- One flower is recorded when the final Next/Finish is tapped, not on the last correct tap.
- The session is deterministic per launch but shuffled between launches; the visual frame
  never changes.
- Leaving at any state discards the in-progress session without penalty.

## Compare loop

```mermaid
stateDiagram-v2
    [*] --> Ask
    Ask --> Ask: Wrong group (miss 1)<br/>counts revealed
    Ask --> Teach: Wrong group (miss 2)<br/>correct group outlined
    Ask --> Correct: Correct group
    Correct --> NextRound: Next
    Teach --> NextRound: Next
    NextRound --> Ask: more rounds
    NextRound --> Complete: last round
    Complete --> [*]
```

Prompt alternates *more* and *fewer* according to the authored content pack, not randomly
within a round. Equal groups are rejected by the content validator.

## Video lesson

```mermaid
sequenceDiagram
    participant C as Child
    participant P as Player
    participant T as TTS
    P-->>C: First frame + large Play (paused)
    C->>P: Play
    loop every 120 ms
        P->>P: Read current position, update caption
    end
    P->>T: Speak caption when voice is on
    C->>P: Pause / Replay / Back / Home
    P->>P: On completion, mark watched
    P-->>C: "The end." + Replay / Back to videos
```

- Video is copied to cache on first play to avoid asset-path quirks in some players.
- Sound off mutes the video; voice off suppresses TTS. Captions remain.
- Backgrounding pauses playback and stops speech.
- No autoplay, no looping, no next-lesson chain.

## Garden

Home → My garden. Plots fill left-to-right, top-to-bottom, one per completed session. There
is no animation that draws attention repeatedly. At 12 flowers the heading becomes “Your
garden is full. You can still play.”

## Parental gate

```mermaid
stateDiagram-v2
    [*] --> Hold
    Hold --> Question: 3 s hold complete
    Hold --> Question: accessible alternative
    Question --> Settings: correct answer
    Question --> Question: wrong answer + new question
    Hold --> [*]: Back
    Question --> [*]: Back
```

The gate is friction, not security. There are no purchases or external links behind it. An
adult using TalkBack can choose the question alternative with a normal double-tap.

## Settings

Settings → toggle voice / sound / reduce motion / high contrast; choose 3 or 5 rounds;
reset progress (confirm); read privacy/about. Changes save immediately. Home returns to the
start.

## Error, empty, and edge states

| State | Behaviour |
| --- | --- |
| Content missing/invalid | Full-screen plain message + Retry + Home; no crash |
| Individual video missing | “This video is not ready.” inside player; other screens still work |
| No lessons | “No videos yet.” in Watch |
| Garden empty | “Your garden is waiting.” |
| Garden full | “Your garden is full. You can still play.” |
| Voice/TTS unavailable | Silent fallback; all captions remain |
| Rotation/process death | No crash; in-progress activity may restart; progress already saved survives |
| App backgrounded during video | Video pauses, speech stops, screen-on flag cleared |
| Back during a round | Returns to previous screen; no confirmation, no loss beyond the round |
| Rapid repeated taps | Ignored after a choice locks; no double flower |

## Layout adaptations

- Phone portrait: single column, all screens scroll.
- Phone landscape/tablet: same single column capped at readable width; content scrolls
  rather than stretching. No side-by-side layout in the MVP.
- 200% font scale: text wraps; rows that hold numerals use three equal columns; settings
  and cards grow vertically.
- Cutouts and gesture bars: root screens use safe-drawing insets.
