# Store listing draft

This is a draft for an internal/closed test or a future store listing. It must be checked
against the shipped build before publishing. Do not add clinical, diagnostic, or therapeutic
claims.

## 1. Basics

| Field | Draft |
| --- | --- |
| App name | Number Garden |
| Package | `com.numbergarden.app` |
| Category | Education / Kids |
| Price | Free |
| Ads | None |
| In-app purchases | None |
| Accounts | None |
| Internet | Not required; no permission |
| Age framing | Designed for ages 4–6 with caregiver support |
| Languages | English (US) |
| Platform | Android 8.0+ (API 26+) |

## 2. Short description (≤ 80 characters)

> A calm, offline early-math app for autistic children. No ads, scores, or timers.

Alternative:

> Quiet counting and comparing for ages 4–6. Offline, no ads, no accounts.

## 3. Full description

```
Number Garden is a small, calm place to practise early numbers.

Watch a short counting video. Count apples by tapping them. Choose which group has
more or fewer. Explore number cards from 1 to 10. Every finished activity grows one
flower in a garden that never wilts.

Designed for a five-year-old autistic child, with a caregiver nearby:

• Predictable screens and the same choices in the same place
• One instruction at a time, with large pictures and large tap targets
• A hint after a miss, then the answer — never a score, timer, buzzer, or loss
• Voice narration you can turn off; captions always visible
• Gentle sounds you can turn off; the app works in silence
• Reduce-motion and high-contrast settings
• Settings behind a grown-up gate
• Fully offline: no internet permission, no ads, no accounts, no analytics
• Progress and settings stay on the device

Number Garden is a practice app, not a medical device. It does not diagnose, treat, or
assess autism or any learning difference. It is not a substitute for a teacher,
therapist, or caregiver's judgement.

Made to be small on purpose.
```

Character count is within Google Play's 4,000-character limit. Re-check after edits.

## 4. What makes it different

Use these as bullets, not as medical claims:

- No failure state. A miss produces a hint, then the answer.
- No timers, streaks, scores, or leaderboards.
- No ads, accounts, analytics, or network permission.
- Voice and sound are independent and optional.
- The garden is finite and never decays.
- The same calm layout on every screen.

Do **not** say: "treats autism", "improves math skills", "clinically proven", "therapeutic",
"reduces anxiety", "diagnoses", or "recommended by therapists" unless there is documented,
specific evidence and permission.

## 5. Screenshot plan

Capture on a clean install with a representative garden state. Do not include a child's
face, name, or identifying background.

| # | Screen | Caption |
| --- | --- | --- |
| 1 | Home | "Five calm choices. Same place every time." |
| 2 | Counting | "Count apples. A hint, not a fail state." |
| 3 | Comparing | "Which group has more?" |
| 4 | Numbers | "Numbers 1 to 10, with a ten-frame." |
| 5 | Lesson player | "Short videos. Captions. No autoplay." |
| 6 | Garden | "One flower for each finished activity." |
| 7 | Settings | "Voice, sound, motion, contrast — behind a grown-up gate." |

Requirements:

- Use the app's real UI, not mockups.
- Show the calm palette; avoid flashing or busy compositions.
- Use 16:9 or 9:16 frames as required by the store.
- Keep text overlays minimal and high contrast.
- Do not show the adult gate's answer or any personal data.
- Do not imply a child is unsupervised.

## 6. Feature graphic / icon

- Icon: existing adaptive icon (cream background, green sprout/flower).
- Feature graphic: cream background, app name, one apple and one flower, no gradients or
  lens flares. Keep text large and minimal.
- Avoid photos of children, hands, or devices.

## 7. Data safety form (draft)

| Question | Answer |
| --- | --- |
| Collects or shares required user data? | No |
| Data types collected | None |
| Data shared | None |
| Data encrypted in transit | Not applicable; no transmission |
| Data deletion request | No account/server data; local progress can be reset or removed by uninstalling |
| Committed to Play Families policy | Yes, if publishing to Families; verify |

Verify against the final artifact. This is not legal advice.

## 8. Content rating questionnaire notes

- No violence, scary content, sexual content, profanity, gambling, or drugs.
- No user-generated content, chat, or social features.
- No ads or purchases.
- No location, camera, microphone, or contacts access.
- The app is educational and aimed at young children.
- Answer the "Designed for Families" questions accurately; the app has no ads or data
  collection, which simplifies the declaration.

## 9. Keywords / tags (use sparingly, no stuffing)

`early math`, `counting`, `numbers`, `autism`, `sensory-friendly`, `calm`, `offline`,
`no ads`, `preschool`, `special education`, `comparing`, `ten frame`.

Some stores restrict terms like "autism" or "therapy". Use plain descriptions and avoid
implying clinical benefit. The phrase "designed for autistic children" should be reviewed
for the target store's policy and for community feedback before publishing.

## 10. Support and privacy

- Support contact: add a monitored email or issue tracker before publishing.
- Privacy policy: required by some stores. If publishing, host a page that states:
  no data collection, no network permission, local-only progress, TTS caveat, contact.
- Do not put a child's information in store reviews or support replies.

## 11. Release notes template

```
Number Garden 0.1.0

First MVP release.

• Watch two short counting and comparing videos
• Count apples 1–5 and choose the numeral
• Compare groups with more/fewer
• Explore number cards 1–10
• Grow a flower for each finished activity
• Voice, sound, motion, contrast, and session-length settings
• Fully offline; no ads, accounts, or analytics

This is a practice app, not a medical device.
```

## 12. Pre-publish checklist

- [ ] Listing matches the shipped APK and version.
- [ ] No unsupported clinical or educational claims.
- [ ] Screenshots show the current UI.
- [ ] Data safety form matches the manifest and behaviour.
- [ ] Content rating complete.
- [ ] Privacy policy live, if required.
- [ ] Support contact monitored.
- [ ] Store listing tested on a small screen and at 200% font scale where applicable.
- [ ] Closed test completed before production rollout.
