[![Build](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/build.yml)
[![Test](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/test.yml/badge.svg?branch=master)](https://github.com/Siddhant23/NY-Times-Popular-Articles/actions/workflows/test.yml)

# NY Times Popular Articles

By Siddhant Mehta (Android Developer)

An Android app that lists the New York Times "Most Viewed" articles of the last 7 days and shows each article's details.

## Tech stack

| Area | Choice |
|---|---|
| Language | Kotlin (JDK 21 toolchain) |
| SDK | minSdk 24 · target/compile 37 |
| UI | Jetpack Compose, Material 3, edge-to-edge |
| Navigation | Navigation 3 |
| Architecture | MVVM (ViewModel + StateFlow → Repository → Retrofit) |
| DI | Hilt (KSP) |
| Network | Retrofit 3, OkHttp 5, kotlinx.serialization |
| Testing | JUnit 4, Robolectric, Compose UI test, Mockito + mockito-kotlin, MockWebServer |
| CI/CD | GitHub Actions + Dependabot |

Exact versions live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Setup

1. Get an API key at <https://developer.nytimes.com>.
2. Add the key to `local.properties`, which is git-ignored:
   ```properties
   NYT_API_KEY=your-key
   ```
   CI reads `NYT_API_KEY` from the environment instead.
3. Build with JDK 21 (Android Studio's bundled JBR works):
   ```bash
   ./gradlew testDebugUnitTest lintDebug assembleDebug
   ```

Data provided by The New York Times.
