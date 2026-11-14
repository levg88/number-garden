# Content style guide

This guide covers all words and media a child sees or hears. It exists so new lessons,
captions, prompts, and feedback feel like the same calm place. It is a writing guide, not a
clinical protocol.

## 1. Voice

- **Calm, literal, and brief.** Say exactly what is happening.
- **One idea per line.** If a sentence has two instructions, split it.
- **Warm without praise inflation.** "You did it." is enough. Avoid "Awesome!",
  "Superstar!", "You're so smart!", and generic "Good job!" as the only feedback.
- **No baby talk, idioms, sarcasm, or exclamation stacks.** One exclamation mark maximum,
  and only in a short celebration.
- **No urgency.** Never "Hurry!", "Quick!", "Only 3 left!", or countdown language.
- **No blame.** A miss is "Let's look again.", never "Wrong" or "Try harder".
- **Present tense and second person when speaking to the child.** "Tap each apple." not
  "The child should tap each apple."

## 2. Reading level and length

| Element | Target |
| --- | --- |
| Prompt | ≤ 5 words |
| Feedback sentence | ≤ 7 words |
| Caption | ≤ 6 words where possible |
| Button label | 1–3 words |
| Screen subtitle | ≤ 5 words |
| Lesson summary | ≤ 8 words |

- Use common words a five-year-old is likely to hear at home or in early years settings.
- Introduce one new math word at a time: *more*, *fewer*, *count*, *number*.
- Do not use "less" and "fewer" interchangeably in the same build. The MVP uses **fewer**
  for countable groups.
- Numerals are shown as digits; the spoken form uses the word ("three").

## 3. Punctuation and capitalisation

- Sentence case for titles, buttons, and labels: "My garden", not "My Garden".
- Full stops for sentences, including feedback lines in the UI.
- Use the serial comma only when it prevents ambiguity.
- Avoid parentheses and semicolons in child-facing text.
- Use an ellipsis only for a genuine pause ("Keep holding…"), never for suspense.
- Curly apostrophes are acceptable in prose; Android string resources escape them as `\'`.
  Keep resource text consistent with the existing file.

## 4. Feedback language

The app uses three levels of support. Each level has a fixed script so the child can predict
it.

| Level | When | Counting copy | Compare copy |
| --- | --- | --- | --- |
| 0 | First attempt | "How many apples?" | "Which group has more?" / "…fewer?" |
| 1 | After one miss | "Let's count together." + numbered apples | "Let's look again. Match one to one." + group counts |
| 2 | After two misses | "This is three." + answer outlined | "This group. Three is more than one." + group outlined |
| Correct | Any correct tap | "Yes. Three apples." | "Yes. Three is more than one." |

Rules:

- Always name the quantity or the relationship. "Yes!" alone is not enough.
- Never say "No", "Wrong", "Oops", or "Try again" with a disappointed tone. The only
  retry line is "Let's look again."
- Do not stack praise. One sentence, then the Next/Finish button.
- Feedback never disappears on a timer; it stays until the child acts.
- The spoken line and the visible line must match. If they differ, the visible line wins.

## 5. Video and caption rules

- Duration: 20–40 seconds. The MVP lessons are 26 s and 28 s.
- Structure: a calm title card or first visual, 2–4 teaching beats, a quiet ending card.
- One concept per video. "Count to 3" does not also teach comparison.
- Captions are short, appear for at least 2.5 seconds, and are also available as in-app text.
- Every spoken word has a caption. No audio-only information.
- No fast cuts, flashing, zoom punches, or camera shake.
- No background music with a strong beat. The chime bed is optional and quiet.
- Leave 1–2 seconds of quiet at the start and end so the video does not begin abruptly.
- Use the same apple and flower artwork as the app. Do not mix illustration styles.
- Show quantities as countable arrays, not dice pips or abstract tally marks, for this age.

## 6. Number language

- 1–5 are the focus for counting and comparing in the MVP.
- 1–10 appear as number cards with a ten-frame, numeral, and word.
- Use "apples" for countable objects in the MVP; the content pack can replace them later.
- When reading a numeral, say the word: "Three", not "Number 3".
- For comparison, always state both quantities: "Three is more than one."
- Avoid "bigger number" and "smaller number" as synonyms for more/fewer in the MVP; they
  describe numerals, not groups.

## 7. Adult-facing copy

- Plain language, no marketing adjectives.
- State limits honestly: "The parental gate is friction, not security."
- State privacy behaviour precisely: "No internet permission, no ads, no accounts, no
  analytics."
- Do not claim clinical benefit, diagnosis, or treatment.
- Use "caregiver" rather than "parent" where possible; families vary.
- Settings hints explain the effect, not the mechanism: "Turns off growing and fading
  animations." not "Sets animator duration scale to 0."

## 8. Naming

| Thing | Name | Notes |
| --- | --- | --- |
| App | Number Garden | Two words, both capitalised |
| Child activities | Watch, Count, Compare, Numbers, My garden | Fixed labels |
| Adult area | Grown-ups | Hyphenated, sentence case in body copy |
| Progress symbol | flower | Lowercase in sentences |
| Session | session | Avoid "level", "round", or "test" in child-facing copy |
| Mistake | miss | Internal only; never shown to the child |

## 9. Content pack fields

Each lesson in `app/src/main/assets/content.json` has:

| Field | Rule |
| --- | --- |
| `id` | Lowercase snake_case, unique, stable |
| `title` | ≤ 4 words, sentence case |
| `skill` | `counting` or `comparing` in the MVP |
| `video` | `videos/<id>.mp4`, lowercase snake_case |
| `summary` | ≤ 8 words, one line |
| `durationSec` | 5–300; must match the actual video |
| `captions` | At least one; ordered by `atMs`; inside the video duration |

Number cards must cover 1–10 exactly. Compare rounds must use 1–10 apples and must not have
equal groups. Counting values must be 1–10. The validator enforces these rules at load time
and in JVM tests.

## 10. Localisation notes

- All UI copy lives in `res/values/strings.xml`; do not hard-code child-facing strings in
  Kotlin.
- Content copy lives in the JSON pack, not in code.
- Keep sentences short and avoid wordplay; both help translation later.
- Plurals use Android `<plurals>` (see `garden_flowers`).
- The MVP is English (en-US) only. TTS uses `Locale.US`.
- When adding a language, review number words and comparison language with a native
  speaker and an educator; direct translation may change the math vocabulary.

## 11. Review checklist

- [ ] Every spoken line has a visible caption or on-screen equivalent.
- [ ] No line asks for two actions at once.
- [ ] Feedback names the quantity or relationship.
- [ ] No blame, urgency, score, streak, or loss language.
- [ ] No more than one exclamation mark in the whole content pack.
- [ ] Video has no flashing, fast cuts, or loud transients.
- [ ] New words are introduced one at a time.
- [ ] Content passes `ContentValidator`.
- [ ] An educator/OT/SLP has reviewed the sequence and language.
