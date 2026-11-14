# Manual device test script

Run this on the target device before handing Number Garden to the child. Automated tests
cannot judge sensory response, voice quality, TalkBack order, or real video playback.

Record: build version, commit, device model, Android version, date, tester, font scale,
theme, audio setup. Mark each step pass/fail. For a failure, write expected vs actual and a
severity (S1–S4) from the [test plan](test-plan.md).

> Stop if the child shows distress. The app is optional; the child's comfort outranks
> completing this script.

## 0. Setup

- [ ] Install the debug or release APK: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- [ ] Connect headphones or confirm speaker volume is low.
- [ ] Set font scale to 100%, then repeat section 10 at 200%.
- [ ] Confirm the device has an offline TTS voice installed (Settings → System → Languages &
      input → Text-to-speech output).
- [ ] Note the starting garden count (fresh install should be 0).

## 1. M-LAUNCH — Install and first launch

- [ ] Launch from the launcher icon.
- [ ] No permission dialog appears.
- [ ] Home appears within ~2 seconds.
- [ ] No splash ad, login, onboarding modal, or notification prompt.
- [ ] The app does not request network access.
- [ ] Rotate to landscape and back; home remains usable and does not crash.
- [ ] Press Home, reopen from recents; state is preserved.
- [ ] Force-stop and relaunch; progress and settings persist.

## 2. M-HOME — Home

- [ ] Greeting "Let's play." and subtitle "Choose one thing." are visible.
- [ ] Five child choices appear in this order: Watch, Count, Compare, Numbers, My garden.
- [ ] Each has a large illustration, label, and short hint.
- [ ] My garden shows the current flower count or "Your garden is waiting."
- [ ] Grown-ups entry is visually quieter and below the child choices.
- [ ] Tap each choice; it opens the expected screen.
- [ ] From each child screen, the top-left home control returns to home in one tap.
- [ ] No auto-navigation, auto-play, or modal appears on home.

## 3. M-WATCH — Watch list

- [ ] Both lessons appear in authored order: "Count to 3", "More and less".
- [ ] Each shows a summary, duration (26 sec / 28 sec), and a play affordance.
- [ ] No lesson shows "Watched" on a fresh install.
- [ ] Back/home return without changing the list.
- [ ] Nothing autoplays.

## 4. M-PLAYER — Lesson player

Run for both lessons.

- [ ] Opening a lesson shows a black 16:9 video area and a Play control; video is paused.
- [ ] Tap Play; video starts within ~1 second and the control changes to Pause.
- [ ] Pause stops playback and keeps the position.
- [ ] Play resumes from the same position.
- [ ] Replay restarts from the beginning.
- [ ] Captions update in sync with the video (within ~1 second) and match the spoken lines.
- [ ] With voice on, each caption is spoken once; lines do not overlap or repeat.
- [ ] With sound on, the chime bed is quiet; with sound off, it is silent.
- [ ] At the end, playback stops, "The end." appears, and the lesson is marked Watched.
- [ ] The next lesson does not autoplay and the video does not loop.
- [ ] Back to videos returns to the list; the Watched marker is present.
- [ ] Press Home during playback; audio stops. Reopen; video is paused.
- [ ] Rotate during playback; position is approximately preserved and playback does not
      restart from zero.
- [ ] Turn the screen off and on during playback; audio does not continue in the background.
- [ ] The screen does not sleep while a video is playing.
- [ ] No controls are clipped at 200% font scale.

## 5. M-COUNT — Counting

- [ ] The prompt "How many apples?" is visible and spoken once.
- [ ] 1–5 apples appear, large enough to tap.
- [ ] Tapping an apple marks it and shows an ordinal badge; tapping again does nothing.
- [ ] Tapping apples is optional; the numeral options work without counting first.
- [ ] Three numeral options appear, sorted ascending, including the answer.
- [ ] Choose the correct numeral: a specific sentence appears ("Yes. Three apples.") and is
      spoken; a Next/Finish button appears.
- [ ] Tap Next; the next round loads with no stale selection or hint.
- [ ] On a fresh round, choose a wrong numeral once: phase stays open, a hint appears
      ("Let's count together.") and numbered apples are shown.
- [ ] Choose wrong again: the answer is revealed, "This is three." appears, and Next appears.
- [ ] Further taps on numerals do nothing after the reveal.
- [ ] No buzzer, red flash, score, timer, or "wrong" text appears.
- [ ] Complete a 3-round session; the completion screen shows a flower, "You did it.", and
      the garden grew a flower.
- [ ] Return home; the garden count increased by exactly 1.
- [ ] Play again starts a fresh session; leaving early does not add a flower.
- [ ] Switch rounds per session to 3 and 5; the dot row and "1 of N" match.

## 6. M-COMPARE — Comparing

- [ ] The prompt "Which group has more?" or "…fewer?" is visible and spoken once.
- [ ] Two large group cards appear with countable apples.
- [ ] Tap the correct group: a sentence naming both quantities appears ("Yes. Three is more
      than one.") and is spoken.
- [ ] Tap the wrong group once: counts are revealed and "Let's look again. Match one to one."
      appears.
- [ ] Tap wrong again: the correct group is outlined, labelled "This group.", and the
      sentence is shown.
- [ ] Colour is not the only signal: an outline and words accompany the highlight.
- [ ] Complete a session; the flower is added once.
- [ ] No score, timer, streak, or loss state.
- [ ] The four compare rounds in view are answerable and the correct side matches the
      values (test both more and fewer).

## 7. M-NUMBERS — Number cards

- [ ] The first card shows 1, "one", a ten-frame with one apple, and a speak control.
- [ ] Next moves 1 → 10 without wrapping.
- [ ] Previous moves back; Previous is disabled at 1 and Next is disabled at 10.
- [ ] Each card shows the correct numeral, word, and ten-frame count.
- [ ] Narration plays when the card changes and when the speak control is tapped.
- [ ] With voice off, no narration plays; the card is still usable.
- [ ] Card text is readable at 200% font scale; the ten-frame is not clipped.
- [ ] TalkBack reads each card as one item (e.g. "3. three.").

## 8. M-GARDEN — Garden

- [ ] Fresh install: 12 plots, none bloomed, "Your garden is waiting."
- [ ] After one session: exactly one flower, count "1 of 12".
- [ ] After several sessions: flowers match the session count.
- [ ] At 12 sessions: all plots bloomed, "Your garden is full. You can still play."
- [ ] Complete another session while full: no 13th flower, no error, play continues.
- [ ] Close and reopen: flowers persist.
- [ ] Wait a day or change the device date: flowers do not wilt or reset.
- [ ] No streak, countdown, or "come back tomorrow" copy.

## 9. M-GATE — Parental gate

- [ ] Tapping Grown-ups opens a screen that says it is for a grown-up.
- [ ] Press and hold the control for 3 seconds; progress is visible and settings open.
- [ ] Release early; the gate does not open.
- [ ] Back exits the gate and returns home.
- [ ] Use the arithmetic alternative ("Use the grown-up question"); a simple sum appears.
- [ ] Answer correctly; settings open.
- [ ] Answer incorrectly; "Not quite. Try again." appears and the gate stays closed.
- [ ] Settings Back does not return to a solved gate.
- [ ] The gate is described as friction, not security, in the docs/copy.

## 10. M-SETTINGS — Settings

- [ ] Voice narration toggles on/off; changes persist after relaunch.
- [ ] Turning voice off stops current speech immediately.
- [ ] Gentle sounds toggles video/chime audio; changes persist.
- [ ] Reduce motion toggles flower growth/fades; meaning is unchanged.
- [ ] High contrast changes colours/outlines immediately; text remains readable.
- [ ] Rounds per session offers 3 and 5; the next session uses the choice.
- [ ] Reset progress shows a confirmation; Cancel leaves data intact.
- [ ] Confirm Reset: garden empties, watched markers clear, settings remain unchanged.
- [ ] Privacy text says: offline, no internet permission, no ads, no accounts, no analytics,
      progress stays on device, TTS caveat.
- [ ] About text says MVP, not a medical/diagnostic tool, and licence summary.
- [ ] Every switch row toggles by tapping anywhere on the row, not only the switch.
- [ ] No setting requires a restart to take effect.

## 11. M-VOICE — Audio modes

- [ ] Voice on, sound on: narration plus quiet chime bed.
- [ ] Voice on, sound off: narration only; video is silent.
- [ ] Voice off, sound on: chime bed only; no TTS.
- [ ] Voice off, sound off: fully usable in silence; captions and text carry all information.
- [ ] Headphones: no unexpected loud transient; volume is comfortable.
- [ ] Change the system TTS voice/rate; the app still speaks and does not crash.
- [ ] Disable/uninstall the system TTS engine; the app still works silently.

## 12. M-A11Y — Accessibility and sensory

### TalkBack

- [ ] Enable TalkBack; traverse home. Each choice is one focus stop with label + hint.
- [ ] Top-bar home/back buttons have meaningful labels, not "button" or "unlabelled".
- [ ] Counting: apples read as "Apple" / "Apple 3"; numeral buttons read "Choose 3".
- [ ] Feedback is announced politely; it does not interrupt or repeat endlessly.
- [ ] Compare: each group reads "Group 1. 3 apples."; the revealed group adds "This group."
- [ ] Numbers: each card reads as one merged item.
- [ ] Settings: each row is one switch stop; the visual switch is not a duplicate stop.
- [ ] No focus trap; Back and Home remain reachable.

### Font scale

- [ ] Set font scale to 200%; repeat Home, Watch, Player, Count, Compare, Numbers, Garden,
      Gate, Settings.
- [ ] No button, numeral, caption, or feedback line is clipped or overlaps.
- [ ] All content can be reached by scrolling.
- [ ] Top-bar titles ellipsise rather than pushing controls off-screen.

### Display and contrast

- [ ] High contrast on: text and control boundaries are clearly distinguishable.
- [ ] High contrast off: normal theme remains readable.
- [ ] Device dark mode on/off: the app stays in the calm light theme (no sudden inversion).
- [ ] Colour is never the only signal for correct/revealed state.

### Motion

- [ ] Reduce motion on: completion flower appears without growth animation; no information
      is lost.
- [ ] System "Remove animations" / animator scale 0: same result.
- [ ] No flashing, pulsing, looping decoration, confetti, or parallax anywhere.

## 13. M-OFFLINE — Offline behaviour

- [ ] Enable airplane mode; force-stop and relaunch.
- [ ] Home, both videos, counting, comparing, numbers, garden, gate, settings all work.
- [ ] No error, spinner that never ends, or "check your connection" message.
- [ ] Disable airplane mode; behaviour is identical.
- [ ] There is no network indicator, sync, or download UI.

## 14. M-ERROR — Failure handling

- [ ] (Build variant) Remove/rename `content.json`; launch. A plain error screen appears
      with retry and home; no crash or blank screen.
- [ ] (Build variant) Remove one video; the lesson shows "This video is not ready." and the
      rest of the app still works.
- [ ] (Build variant) Corrupt a JSON field; the error screen names the problem in adult
      language.
- [ ] Restore the assets; retry works or relaunch recovers.
- [ ] During a video, simulate a decode failure if possible; controls remain and Back/Home
      work.

## 15. M-PERSIST — Persistence and reset

- [ ] Complete a session, watch a lesson, change all settings.
- [ ] Force-stop; relaunch: all state is present.
- [ ] Reboot the device; relaunch: all state is present.
- [ ] Rotate on every screen; no state loss or crash.
- [ ] Background for 10+ minutes; return: app is responsive and video is paused.
- [ ] Reset progress: progress cleared, settings intact.
- [ ] Uninstall/reinstall: all local data gone, fresh state.

## 16. M-PRIVACY — Privacy verification

- [ ] `adb shell dumpsys package com.numbergarden.app | grep -i permission` shows no
      **system/runtime** permissions. The only entry should be the app-private
      `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`.
- [ ] `aapt dump permissions app-debug.apk` shows that app-private permission and no
      `INTERNET`, camera, microphone, location, storage, or notification permission.
- [ ] With airplane mode on, no feature is degraded.
- [ ] During a full session, `adb logcat` contains no child data, no network calls, and no
      analytics events.
- [ ] Settings privacy text matches observed behaviour.
- [ ] Android backup is disabled for the app (Settings → Apps → Number Garden → no backup,
      or verify the manifest).
- [ ] Device-to-device transfer does not include app data (where testable).

## 17. Release build spot-check

- [ ] Build and install the release APK.
- [ ] Launch, home, one video, one counting session, one compare session, numbers, garden,
      gate, settings.
- [ ] R8 has not removed serialization or resources; content loads and videos play.
- [ ] APK size is within the 40 MB target.
- [ ] No debug banner, logging, or test-only screen is visible.
- [ ] Version name/code match the release checklist.

## 18. Sign-off

| Item | Result | Notes |
| --- | --- | --- |
| All S1/S2 cases pass | | |
| S3/S4 documented | | |
| Caregiver reviewed privacy + sensory settings | | |
| Educator/OT/SLP reviewed sequence | | |
| Device model / Android version | | |
| Build / commit | | |
| Tester / date | | |

If any S1 or S2 case fails, do not hand the app to the child. Fix, rebuild, and rerun the
affected sections plus section 1.
