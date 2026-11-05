[![Build](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/build.yml)
[![Test](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/test.yml/badge.svg?branch=master)](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/test.yml)
<a href="https://androidstudio.googleblog.com/">
  <img
    alt="Android Studio Panda 4"
    src="https://img.shields.io/badge/Android%20Studio%20Panda%204-2025.3.4-%233DDC84?style=square&logo=androidstudio&logoColor=white"
  />
</a>

# NY TIMES ARTICLES

By Siddhant Mehta (Android Developer)

An Android app that lists the New York Times **Most Viewed articles from the last 7 days** and opens a detail screen for each one.
It uses the [NYT Most Popular API](https://developer.nytimes.com/docs/most-popular-product/1/overview).

## Tech stack

| | |
|---|---|
| Language | Kotlin (built into AGP 9) |
| UI | Jetpack Compose + Material 3, edge-to-edge |
| Navigation | Navigation 3 (`NavDisplay`, `@Serializable` `NavKey`s) |
| Architecture | MVVM: Compose UI → ViewModel (`StateFlow`) → Repository → Retrofit |
| DI | Hilt (KSP) |
| Network | Retrofit 3 + OkHttp 5 + kotlinx.serialization |
| Concurrency | Coroutines / Flow |
| Testing | JUnit 4, Robolectric, Compose UI tests, Mockito + mockito-kotlin, OkHttp MockWebServer, Hilt (instrumented) |
| CI/CD | GitHub Actions (Build, Test), Dependabot |
| SDK | minSdk 24 · targetSdk / compileSdk 37 · JDK 21 |

Dependency versions are kept in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Setup

1. Get a free API key from the [NYT Developer portal](https://developer.nytimes.com/get-started) and enable the **Most Popular API**.
2. Add these two lines to `local.properties` in the project root. The file is git-ignored, so keep it that way:

   ```properties
   sdk.dir=/path/to/Android/sdk
   NYT_API_KEY=your_key_here
   ```

   CI reads the key from the `NYT_API_KEY` environment variable instead. Without a key the app still builds, but every request returns 401.
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

## Contributing / Dependabot

- Dependabot opens weekly PRs. Patch and minor bumps are auto-merged, and major bumps need a manual review. Auto-merge waits for CI only when `master` requires the `build` status checks.
- Open work is tracked in [REMAINING_WORK.md](REMAINING_WORK.md).
