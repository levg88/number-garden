# Release checklist

Copy this into the release record for each build. Do not mark an item done without checking
the shipped artifact.

> **Current snapshot (15 Nov 2026):** unit tests, lint, debug/release builds, and the Compose
> smoke test pass. Every screen was exercised on an API 36 emulator at 100% and 200% font
> scale. A signed release APK has been produced locally and verified with `apksigner`.
> Target-hardware checks, TalkBack, rotation, process death, and the signed-release install
> are still open. See [HANDOFF.md](../HANDOFF.md) for the exact evidence.

## 1. Scope and versioning

- [ ] `versionCode` incremented in `app/build.gradle.kts`.
- [ ] `versionName` matches the release (`0.8.0` for this build).
- [ ] `CHANGELOG.md` has an entry for this version.
- [ ] No P0 requirement is unmet (see [PRD](../01-product/prd.md)).
- [ ] No S1/S2 defect is open.
- [ ] Any S3/S4 defect is documented with a caregiver-accepted workaround.
- [ ] Release notes are written in plain language, with no clinical claims.

## 2. Code and content freeze

- [ ] Working tree is clean; release build is from the tagged commit.
- [ ] Tag created (e.g. `v0.8.0`) and pushed.
- [ ] Content pack frozen; no uncommitted JSON or MP4 changes.
- [ ] Both videos play on the target device.
- [ ] No debug-only screen, log, or placeholder is reachable.
- [ ] No TODO/FIXME in release-critical paths without an issue reference.

## 3. Automated checks

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export ANDROID_HOME="$HOME/android-sdk"
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

- [ ] Unit tests pass.
- [ ] Lint passes with no errors. Warnings are reviewed; new warnings are explained.
- [ ] Debug APK builds.
- [ ] Release APK builds (R8 + resource shrinking).
- [ ] CI workflow is green on the release commit.
- [ ] No new dependency without review.

## 4. Manifest and permissions

```bash
"$ANDROID_HOME/build-tools/36.0.0/aapt" dump permissions app/build/outputs/apk/release/app-release.apk
```

- [ ] Output shows the app-private `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` and **no system permissions** (no `INTERNET`, camera, microphone, location, storage, or notification).
- [ ] Merged manifest contains no system `<uses-permission>`.
- [ ] `android:allowBackup="false"`.
- [ ] `android:fullBackupContent="false"`.
- [ ] `android:dataExtractionRules` points to the exclusion file.
- [ ] `android:usesCleartextTraffic="false"`.
- [ ] Only `MainActivity` is an exported activity; the only other exported component is AndroidX's `DUMP`-protected profile-installer receiver.
- [ ] No unexpected components, services, receivers, or providers.
- [ ] No network library in the dependency graph.

## 5. Privacy and data safety

- [ ] Settings privacy text matches the shipped behaviour.
- [ ] Data-safety draft matches the shipped build (no data collected/shared).
- [ ] No child name, age, photo, voice, or location is requested or stored.
- [ ] Logcat during a full session contains no child data or network calls.
- [ ] Airplane-mode run passes.
- [ ] TTS caveat is accurate (system voice may download data).
- [ ] Backup/transfer exclusion verified where testable.
- [ ] Reset progress clears progress and leaves settings.
- [ ] Uninstall removes all local data.

## 6. Device verification

Run the [manual device test script](../04-qa/manual-test-script.md).

- [ ] Fresh install and first launch.
- [ ] Home, Watch, both lessons, Count, Compare, Numbers, Garden, Gate, Settings.
- [ ] Voice on/off, sound on/off, both off.
- [ ] 200% font scale on every screen.
- [ ] TalkBack traversal and labels.
- [ ] High contrast and reduce motion.
- [ ] Airplane mode.
- [ ] Rotation and background/resume.
- [ ] Process death and relaunch.
- [ ] Gate: hold and arithmetic alternative.
- [ ] Reset progress.
- [ ] Release APK installed and spot-checked (not only debug).
- [ ] Cold start measured; target < 2 s on a mid-range 2021 device.
- [ ] APK size measured; target < 40 MB.

## 7. Signing and artifact

- [ ] Release keystore exists outside the repository.
- [ ] Keystore path/password supplied via environment or `~/.gradle/gradle.properties`, never
      committed.
- [ ] Release build signed with the release key.
- [ ] `apksigner verify --print-certs` shows the expected certificate.
- [ ] APK is aligned (`zipalign -c -v 4`).
- [ ] Artifact checksum recorded.
- [ ] ProGuard/R8 mapping saved with the release record.
- [ ] No debug signing key used for a public release.

## 8. Store listing (if publishing)

- [ ] Title, short description, and full description match the app.
- [ ] Screenshots show the calm UI and contain no child's face or identifying information.
- [ ] Feature graphic and icon meet store specs.
- [ ] Content rating questionnaire completed accurately.
- [ ] Data-safety form completed and matches the build.
- [ ] Target audience and "Designed for Families" answers reviewed.
- [ ] Privacy policy URL, if required, matches the app behaviour.
- [ ] No clinical, diagnostic, or therapeutic claims.
- [ ] Support contact is monitored.
- [ ] Store listing mentions offline/no ads/no accounts if accurate.

## 9. Legal and licensing

- [ ] `LICENSE` (MIT) is present for code.
- [ ] `docs/content-license.md` (CC BY-NC 4.0) is present for media/content.
- [ ] No third-party media, font, or code without compatible licence.
- [ ] Generated media is original and documented in `tools/`.
- [ ] Store listing credits match the licences.
- [ ] "Not a medical device" disclaimer is present in About and the listing.

## 10. Rollback and support

- [ ] Previous release artifact is retained.
- [ ] Rollback steps are documented (install previous APK).
- [ ] Caregiver knows how to reset progress and uninstall.
- [ ] A support contact or issue tracker exists.
- [ ] If a privacy issue is found, the incident steps in
      [security-privacy.md](../03-architecture/security-privacy.md) are followed.

## 11. Final sign-off

| Item | Name | Date | Result |
| --- | --- | --- | --- |
| Engineering | | | |
| Caregiver product review | | | |
| Accessibility review | | | |
| Content/educator review | | | |
| Privacy review | | | |
| Release approval | | | |

Release is approved only when every box above is checked or explicitly waived with a reason.
