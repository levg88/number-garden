# ADR 0006 — Manual dependency injection

- **Status:** Accepted
- **Date:** November 2026
- **Deciders:** Product/engineering

## Context

The app has three repositories, one narrator, two ViewModels, and one activity. There are no
feature modules, scopes, or background workers. Build speed and auditability matter.

## Decision

Use a hand-written `AppContainer` created in `NumberGardenApplication`, passed to the
navigation host, and used by screen-level ViewModel factories. No DI framework, no service
locator, no annotation processing.

```kotlin
class AppContainer(context: Context) {
    val contentRepository = ContentRepository(context.applicationContext)
    val progressRepository: ProgressRepository = PrefsProgressRepository(context.applicationContext)
    val settingsRepository: SettingsRepository = PrefsSettingsRepository(context.applicationContext)
}
```

## Alternatives considered

| Alternative | Why not |
| --- | --- |
| Hilt | Adds KSP, generated code, build time, and concepts for three singletons |
| Koin | Runtime service locator; failures move from compile time to runtime |
| Dagger | Same overhead as Hilt without Android integration benefits |
| Global singletons | Hidden dependencies and harder tests |
| Compose `CompositionLocal` for repositories | Makes data flow less explicit; reserved here for speech only |

## Consequences

**Positive**

- Dependencies are visible in one file.
- No annotation processing or generated code.
- Faster clean builds.
- Easy to replace a repository in a test or preview.
- Fewer concepts for a caregiver-developer to audit.

**Negative**

- Manual wiring grows unwieldy with many features.
- No automatic scoping or lifecycle management.
- ViewModel factories are written by hand.
- Easy to accidentally create multiple instances if not careful.

## Follow-ups

- Keep `AppContainer` as the only composition root.
- If the app grows beyond a handful of features, evaluate Hilt with a new ADR.
- Use constructor injection in ViewModels and pure classes.
