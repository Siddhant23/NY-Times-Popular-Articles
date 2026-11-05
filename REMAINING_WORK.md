# Remaining work

This list was made on 2026-11-06 from `master` at `88292ad` (after #379 and #380).
It is ordered by priority. Tick items off as they land, and delete the file when it's empty.

**Already done:** master builds again (minSdk 24, #380), the `api-key` is redacted from debug logs, Dependabot auto-merges only patch and minor bumps, and the README is updated.

---

## P0: Protect `master` (repo settings, ~5 min)

- [ ] **Confirm CI is green on `88292ad`.** Run `gh run list --branch master --limit 3`. Both **Build** and **Test** must pass. Test also runs the emulator tests, which were not run locally.
- [ ] **Add a branch ruleset or protection rule for `master`.** Settings → Rules → Rulesets, or edit the existing "Copilot review for default branch" ruleset.
  - Turn on **Require status checks to pass** and add `build` (Build workflow) and `build (29, default, x86)` (Test workflow).
  - Leave **Require approvals at 0**. GitHub never lets you approve your own PR, so as the only developer you would block yourself. If you ever turn approvals on, add yourself under "Bypass list".
  - Without this, `gh pr merge --auto` in `dependabot-auto-merge.yml` merges right away, even when CI is red. That is how #371–#378 broke master.
- [ ] Check it works: on the next Dependabot PR, the merge box should say "waiting for status checks".

## P1: Correctness and robustness

- [ ] **`CustomInterceptor` loses the HTTP status and leaks the response** ([CustomInterceptor.kt](app/src/main/java/com/test/android/siddhant/model/api/CustomInterceptor.kt))
  - On a non-2xx response it throws without calling `response.close()`, so the connection leaks.
  - Every failure becomes `UnknownException(response.message)`. On HTTP/2, `message` is usually empty, so the user sees a blank snackbar. 401 (bad or missing key), 429 (rate limit) and 5xx all look the same.
  - The `try/catch` around `response` can never catch anything useful, so remove it.
  - Fix: close the response, then map the codes to typed errors (`InvalidApiKey`, `RateLimited`, `ServerError(code)`). Show a matching string for each.
- [ ] **Connectivity pre-check is incomplete** ([Util.kt](app/src/main/java/com/test/android/siddhant/utils/Util.kt))
  - It only accepts cellular, Wi-Fi and ethernet, so VPN-only and other transports count as "offline".
  - Use `hasCapability(NET_CAPABILITY_INTERNET)`, or drop the pre-check and map the `IOException` that OkHttp already throws.
  - Also remove the meaningless `@ApplicationContext` on the function parameter.
- [ ] **Error UI shows two messages** ([PopularListScreen.kt](app/src/main/java/com/test/android/siddhant/ui/screens/PopularListScreen.kt)): the snackbar and the full-screen error appear together. Keep one, and show the mapped message from the item above.
- [ ] **Hardcoded UI strings**: "Most Popular", "Article", "Back", "Retry" and "Unable to load articles" should move to `strings.xml`.
- [ ] **Theme flags clash with `enableEdgeToEdge()`** ([styles.xml](app/src/main/res/values/styles.xml)): `windowTranslucentStatus` and `windowTranslucentNavigation` are deprecated and redundant now. Remove them, then check the system bars on API 24, 29 and 35+.

## P2: Dependency hygiene (`gradle/libs.versions.toml`)

- [ ] **Remove the Navigation 2 dependency.** `hilt-navigation-compose` 1.4.0 pulls in `navigation-compose` 2.9.0 even though the app uses Navigation 3. `MainActivity` already imports `hiltViewModel` from `androidx.hilt.lifecycle.viewmodel.compose`, so depend on `androidx.hilt:hilt-lifecycle-viewmodel-compose` directly.
- [ ] **Replace the deprecated test libraries.**
  - `com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0` → `org.mockito.kotlin:mockito-kotlin` (update the imports in `PopularVMTest`).
  - Drop `mockito-inline`. Since Mockito 5, `mockito-core` is inline by default.
  - Drop `androidx.arch.core:core-testing` and `espresso-intents`. Nothing imports them.
- [ ] **Use the official Retrofit converter.** Replace `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0` with `com.squareup.retrofit2:converter-kotlinx-serialization` (same version as Retrofit 3).
- [ ] **Use the plain lifecycle artifact.** `lifecycle-viewmodel-ktx` → `lifecycle-viewmodel` (the -ktx artifacts have been empty since 2.8).
- [ ] **Tidy the catalog.**
  - Merge the duplicate version keys: `kotlinSerialization` = `kotlinGradlePlugin`, `hilt` = `hiltGradlePlugin`, `lifecycleCompose` = `lifecycleViewModel`.
  - Move the inline versions (`kotlinx-serialization-json`, `ksp`, the converter) into `[versions]`.
- [ ] **Drop the parcelize plugin.** `ResultsItem` and `PopularArticleResponse` are `@Parcelize`, but nothing uses them as `Parcelable`, since nav keys are `@Serializable`.
- [ ] **Review `gradle.properties`.** `kotlin.stdlib.default.dependency=false` is unusual, so check whether it is still needed. `ksp.useKSP2=true` is now the default. Try enabling the configuration cache, which Gradle suggests on every build.
- [ ] Pending bumps reported by lint: AGP 9.5.1 and navigation3 1.2.1. Let Dependabot open these PRs, and merge them only when CI is green.

## P3: Lint warnings (13 now, 0 errors)

- [ ] **UnusedResources (7):** `colorPrimary`, `colorPrimaryDark` and `colorAccent` in `colors.xml`; `mipmap-hdpi/ic_arrow_right.png`; `mipmap-hdpi/ic_calendar.png`; `drawable/ic_circle_shape.xml`; and the `titleName` string. Delete them.
- [ ] **MonochromeLauncherIcon (2):** add a `<monochrome>` layer to `mipmap-anydpi-v26/ic_launcher*.xml` so the themed icon works on Android 13+.
- [ ] **UnusedAttribute (1):** add `tools:targetApi="33"` to `android:enableOnBackInvokedCallback` in the manifest.
- [ ] **GradleDependency / AndroidGradlePluginVersion (3):** see the pending bumps above.
- [ ] Small cleanup: `AppConstant` should be an `object` rather than a class with a `companion object`.

## P4: CI workflows (`.github/workflows/`)

- [ ] **Run lint in CI.** Add `./gradlew lintDebug` to Test or Build and upload the report.
- [ ] **Fix the Test timeout.** In `test.yml`, `timeout-minutes: 15` sits under `env:`, so it does nothing. Move it to the job level.
- [ ] **Make the two workflows consistent.** Build uses `zulu` and Test uses `temurin`, and only Test uses `gradle/actions/setup-gradle`. Pick one setup for both.
- [ ] **Keep CI history longer.** `delete.yml` deletes runs older than 7 days on every push to master. That removed the history that would have shown when master broke. Run it on a weekly schedule and/or keep runs for 30 days.
- [ ] Optional: run the emulator tests on API 24 (the new minSdk) as well as API 29.
- [ ] Optional: `assembleRelease` in CI has no `NYT_API_KEY` and no signing config, so it only proves the build compiles. Add release signing from secrets only if you plan to publish.

## P5: Test coverage

- [ ] Add MockWebServer tests (`mockwebserver3`) for `CustomInterceptor`. Cover adding the `api-key`, the error mapping from P1, and closing the response.
- [ ] Add a unit test for `PopularRepo` that checks `results` is `null` → empty list and that it runs on the injected dispatcher.
- [ ] Add a test that the debug logger redacts `api-key` (`NetworkModule.providesOkHttpClient`).

## Known limitation (no action unless the app ships)

The NYT key is compiled into `BuildConfig.API_KEY`, so anyone can extract it from the APK. That is fine for a demo app. A real fix needs a small backend proxy that holds the key.

## Feature ideas (optional, after the above)

- Open the full article (the `url` field, through Custom Tabs) from the detail screen.
- Thumbnails from the `media` field.
- Pull-to-refresh, and a 1, 7 or 30-day period selector.
- A list-detail layout on tablets and foldables using Navigation 3 scenes.
- An offline cache (Room or DataStore) for the last successful list.
