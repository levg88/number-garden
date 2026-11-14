# Privacy and security

Number Garden is built for a young child, so the default is **collect nothing, send
nothing, ask for nothing**. This document states what that means, where the boundaries are,
and what a caregiver or reviewer should verify.

> The app is not a medical device and does not provide diagnosis, treatment, or clinical
> assessment.

## 1. Summary

| Question | Answer |
| --- | --- |
| Permissions declared | **No system/runtime permissions.** One app-private signature permission is added by AndroidX for internal dynamic receivers; it grants no user-data access |
| Internet access | No `INTERNET` permission; no network libraries |
| Accounts | None |
| Ads | None |
| Analytics / crash reporting | None |
| Third-party SDKs | None |
| Child personal data collected | None |
| Data stored | Two local `SharedPreferences` files: progress and settings |
| Data transmitted | None by the app |
| Backups / device transfer | Disabled |
| Parental gate | Friction, not authentication |
| Content updates | Bundled only; no remote config or downloads |

## 2. Data inventory

| Data | Where | Why | Retention |
| --- | --- | --- | --- |
| Completed lesson IDs | `number_garden_progress` | Show "Watched" | Until reset/uninstall |
| Counting session count | `number_garden_progress` | Grow the garden | Until reset/uninstall |
| Comparing session count | `number_garden_progress` | Grow the garden | Until reset/uninstall |
| Voice on/off | `number_garden_settings` | Accessibility | Until uninstall |
| Sound on/off | `number_garden_settings` | Sensory control | Until uninstall |
| Reduce motion | `number_garden_settings` | Sensory control | Until uninstall |
| High contrast | `number_garden_settings` | Visual access | Until uninstall |
| Rounds per session | `number_garden_settings` | Session length | Until uninstall |

Not collected, not requested, not inferred:

- name, age, birthday, gender, photo, voice recording, camera, location
- contacts, accounts, advertising ID, device identifiers
- free-text input, behaviour traces, per-round timings, error history
- crash logs, diagnostics, install attribution

The only stored values are counters and booleans. There is no free-text field anywhere in
the app.

## 3. Network and permissions

`AndroidManifest.xml` requests no system permissions. In particular:

- no `INTERNET`
- no `ACCESS_NETWORK_STATE`
- no camera, microphone, location, contacts, storage, or notification permission
- no advertising ID permission

The merged manifest contains one app-defined, `signature`-protected permission,
`com.numbergarden.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, added automatically by
AndroidX Core. It is used to protect dynamically registered broadcast receivers on older
Android versions. It is not a runtime permission, does not appear as a user-grantable
permission, cannot be held by another app, and gives no access to user data. This is why the
release check looks for **system** permissions rather than the literal absence of any
`<uses-permission>` element.

The merged manifest also contains AndroidX's profile-installer receiver, protected by the
system `android.permission.DUMP` permission, and an AndroidX startup provider that is not
exported. Neither collects data.

`android:usesCleartextTraffic="false"` is set as defence in depth. There are no HTTP
clients, no WebView, no remote images, and no deep links.

Potential indirect network use: the device's **text-to-speech engine is a separate system
app**. If the selected voice is network-based, the TTS engine may download voice data. The
Number Garden app itself does not send anything. Caregivers who want a fully offline
experience should choose an offline system voice in Android settings; the app says this in
its privacy note.

## 4. Storage and backup

- `SharedPreferences` files use `Context.MODE_PRIVATE`, readable only by the app's UID.
- `android:allowBackup="false"` and `android:fullBackupContent="false"`.
- `res/xml/data_extraction_rules.xml` excludes all domains from both cloud backup and
  device-to-device transfer on Android 12+.
- Videos are copied to `cacheDir/videos/` for playback. They are read-only app assets and
  contain no user data. The system may evict the cache; the app copies again when needed.
- No files are written to external storage.
- No database, no logs containing child data, no exported files.

## 5. Parental gate

The gate exists to stop a young child from changing sensory settings or resetting progress.
It is **not** a security boundary.

- Press and hold for 3 seconds, with visible progress.
- An arithmetic alternative (e.g. "What is 7 + 4?") for adults who cannot press and hold.
- Back always exits.
- Nothing harmful is behind the gate: no purchases, links, accounts, or data deletion beyond
  local progress.

Because the arithmetic alternative is intentionally simple, an older child could pass it.
That is an accepted trade-off for accessibility. There is nothing sensitive to protect.

## 6. Threat model

| Threat | Mitigation | Residual risk |
| --- | --- | --- |
| Child accidentally changes settings | Gate + confirmation for reset | Older child may pass gate |
| Data leaves device | No network permission, no SDKs, backups off | Network TTS voice may download data outside the app |
| Third-party ad/tracker SDK | None included | None in this build |
| Malicious content update | Content is bundled and validated; no remote updates | A compromised build could ship bad content |
| Path traversal in video cache | `require(!assetPath.contains(".."))`, filename only, `.mp4` required | Low |
| Sensitive data in logs | No child data logged; no analytics | Low |
| Shoulder-surfing / shared device | Only counters and booleans stored | Low |
| Device theft | Standard Android device encryption | Outside app scope |
| Supply-chain compromise | Small dependency set, pinned versions, CI | Standard ecosystem risk |

The app assumes the device OS and the installed TTS engine are trusted. It does not attempt
to defend against a rooted device or a malicious system app.

## 7. Supply chain

- Dependencies are pinned in `gradle/libs.versions.toml`.
- No analytics, ads, crash, network, image-loading, or database libraries.
- Compose BOM and AndroidX versions are fixed per build.
- CI runs tests, lint, and a debug assemble.
- Release builds enable R8 shrinking and resource shrinking.
- The repository includes the Gradle wrapper; CI uses the wrapper.
- There is no dynamic code loading, reflection-based plugin system, or remote script.

Before a public release:

- [ ] Generate a dependency report and review new transitive dependencies.
- [ ] Confirm no permission was added by a dependency merge.
- [ ] Review the merged manifest, not only the source manifest.
- [ ] Sign with a protected release key and keep it out of the repository.
- [ ] Re-run the privacy checklist after any SDK addition.

## 8. Store data-safety draft

If published to Google Play, the Data safety section should state:

- **Does your app collect or share any of the required user data types?** No.
- **Is all user data encrypted in transit?** Not applicable; no data is transmitted.
- **Can users request data deletion?** No account or server data exists. Local progress can
  be cleared in the app or by uninstalling.
- **Data collected:** none.
- **Data shared:** none.
- **Security practices:** data is stored locally; backups and transfer are disabled.

Review this against the final build. Store forms are legal declarations; do not copy this
draft without verifying the shipped artifact.

## 9. Children's privacy

- The app is designed for use by a child with a caregiver nearby.
- It does not ask for age, name, email, or any identifier.
- It does not contain ads, purchases, social features, or external links.
- It does not include user-generated content or chat.
- It does not use persuasive design to extend use.
- It does not collect behavioural data for product improvement.

The product-learning plan relies on consented caregiver interviews and observed sessions,
not on device telemetry. See [metrics](../01-product/metrics.md).

## 10. Incident response

Because there is no server and no collected data, the blast radius of a bug is local.

If a privacy or security issue is found:

1. Stop distribution of the affected build.
2. Reproduce and identify whether any data left the device.
3. If a release is affected, publish a fixed build and a plain-language note.
4. Update this document and the changelog.
5. If store listing claims are affected, correct the Data safety form.

There is no breach-notification pipeline for child data because no child data is collected.
If that ever changes, this section must be replaced with a real process.

## 11. What would force a privacy review

Any of the following requires a new ADR and a full review before merge:

- adding `INTERNET` or any permission
- adding analytics, crash reporting, ads, or attribution
- adding accounts, cloud sync, or a backend
- adding remote content or over-the-air updates
- adding image/voice capture
- adding external links or purchases
- enabling backup or device transfer
- adding a third-party SDK that phones home

## 12. Verification checklist

- [ ] `aapt dump permissions app-debug.apk` shows no **system** permissions; the only entry is the app-private `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`.
- [ ] Merged manifest contains no `INTERNET` or other system `<uses-permission>`.
- [ ] No network libraries in the dependency graph.
- [ ] `allowBackup=false`, `fullBackupContent=false`, data extraction rules present.
- [ ] Settings privacy text matches the shipped behaviour.
- [ ] TTS caveat is present and accurate.
- [ ] No child data in logcat during a manual session.
- [ ] Airplane-mode run matches online run.
- [ ] Reset removes progress and leaves settings.
- [ ] Uninstall removes all local data.
