[![Build](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/build.yml)
[![Test](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/test.yml/badge.svg?branch=master)](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/test.yml)
<a href="https://androidstudio.googleblog.com/">
  <img
    alt="Android Studio Rabbit 2 2026.2.2 Patch 1"
    src="https://img.shields.io/badge/Android%20Studio%20Rabbit%202-2026.2.2%20Patch%201-%233DDC84?style=square&logo=androidstudio&logoColor=white"
  />
</a>

# NY TIMES ARTICLES

By Siddhant Mehta (Android Developer)

An Android app that lists the New York Times **Most Viewed articles from the last 7 days** and opens a detail screen for each one.
It uses the [NYT Most Popular API](https://developer.nytimes.com/docs/most-popular-product/1/overview). Data provided by The New York Times.

## Features

- A list of the most viewed articles, with a detail screen showing the title, byline, date and abstract.
- Clear error screens, each with a Retry button: offline, invalid API key (401/403), rate limited (429), server error (5xx).
- Light and dark theme. In dark mode there's no white flash at launch.
- Right-to-left layouts: the icons are auto-mirrored.
- Edge-to-edge UI and predictive back.

## Tech stack

| | |
|---|---|
| Language | Kotlin (built into AGP 9) |
| UI | Jetpack Compose + Material 3, edge-to-edge, vector drawable icons |
| Navigation | Navigation 3 (`NavDisplay`, `@Serializable` `NavKey`s) |
| Architecture | MVVM: Compose UI → ViewModel (`StateFlow`) → Repository → Retrofit |
| DI | Hilt (KSP) |
| Network | Retrofit 3 + OkHttp 5 + kotlinx.serialization; typed API errors; `api-key` redacted from debug logs |
| Concurrency | Coroutines / Flow |
| Testing | JUnit 4, Robolectric, Compose UI tests, Mockito + mockito-kotlin, OkHttp MockWebServer, Espresso + Hilt (instrumented) |
| Build | Gradle version catalog, configuration cache, `dependencyResolutionManagement` |
| CI/CD | GitHub Actions (Build, Test), Dependabot |
| SDK | minSdk 26 · targetSdk / compileSdk 37 · JDK 21 |

Dependency versions are kept in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Setup

1. Get a free API key from the [NYT Developer portal](https://developer.nytimes.com/get-started) and enable the **Most Popular API**.
2. Add these two lines to `local.properties` in the project root. The file is git-ignored, so keep it that way:

   ```properties
   sdk.dir=/path/to/Android/sdk
   NYT_API_KEY=your_key_here
   ```

   CI reads the key from the `NYT_API_KEY` environment variable or repo secret instead. Without a key the app still builds, with a warning, but every request returns 401.
3. Use **JDK 21**. Android Studio's bundled JBR works:

   ```bash
   export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
   ```

## Build and test

```bash
./gradlew assembleDebug               # debug APK
./gradlew testDebugUnitTest           # JVM + Robolectric unit and Compose tests
./gradlew lintDebug                   # Android lint
./gradlew connectedDebugAndroidTest   # instrumented tests (needs an emulator or device)
```

## CI/CD

| Workflow | Triggers | What it does |
|---|---|---|
| **Build** (`build.yml`) | Push to `master` or `workflow-github` (except Markdown-only changes); pull requests targeting `master` | Builds the unsigned release APK with R8. `NYT_API_KEY` is optional for building. |
| **Test** (`test.yml`) | Push to `master` or `workflow-github` (except Markdown-only changes); pull requests targeting `master` | Runs unit tests and lint once, then instrumented tests on API 29 (required check `test (API 29)`) and API 37 (`test (API 37)`, currently non-blocking). |
| **Dependabot Auto Merge** | Dependabot pull requests | Auto-merges patch and minor bumps. Major bumps need a manual review. |
| **Delete old workflow runs** | Weekly, or manually | Keeps the last 30 days of CI history. |

The badges above show the latest run for each workflow on `master`. A documentation-only **push** (for example, a README-only commit) does not start either workflow because their push triggers ignore `*.md`; the badge therefore won't update for that push. Pull requests targeting `master` are not filtered by file path, so they still run for documentation-only changes. If a badge says **No status**, check the workflow's Actions page and confirm that it has at least one eligible run on `master`; the badge link takes you to that page. Changes pushed to other branches will not trigger these workflows unless the branch is added to their configuration.

Third-party actions are pinned to commit SHAs, and Dependabot keeps them updated. Auto-merge waits for CI only when `master` requires the `build` and `test (API 29)` checks in its branch protection.

## Contributing

- Open work is tracked in [REMAINING_WORK.md](REMAINING_WORK.md).
- Notes for AI coding agents are in [CLAUDE.md](CLAUDE.md).
