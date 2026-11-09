# Remaining work

This list was made on 2026-11-06 from `master` at `88292ad` (after #379 and #380), and last updated on 2026-11-09.
It is ordered by priority. Tick items off as they land, and delete the file when it's empty.
This is the single tracking file. The old `docs/REVIEW-2026-11.md` from the first version of #382 was merged into it.

**Already done:**
- **#380:** master builds again (minSdk 24), the `api-key` is redacted from debug logs, and Dependabot auto-merges only patch and minor bumps.
- **#381:** README refreshed, and P1, P2, P3 and P5 below are done.
- **#383:** list keys can no longer crash on zero or duplicate IDs; minSdk is now 26.
- **#382 (rebuilt on master):** the follow-ups in P6 below.

**Still open:** P0 (repo settings and key revocation), P4 (CI workflows), and P7 (later decisions).

---

## P0: Protect `master` (repo settings, ~5 min) — OPEN

- [ ] **Confirm CI is green on master.** Run `gh run list --branch master --limit 3`. Both **Build** and **Test** must pass. Test also runs the emulator tests, which are not run locally.
- [ ] **Add a branch ruleset or protection rule for `master`.** Settings → Rules → Rulesets, or edit the existing "Copilot review for default branch" ruleset.
  - Turn on **Require status checks to pass** and add `build` (Build workflow) and `build (29, default, x86)` (Test workflow).
  - Leave **Require approvals at 0**. GitHub never lets you approve your own PR, so as the only developer you would block yourself. If you ever turn approvals on, add yourself under "Bypass list".
  - Without this, `gh pr merge --auto` in `dependabot-auto-merge.yml` merges right away, even when CI is red. That is how #371–#378 broke master.
- [ ] Check it works: on the next Dependabot PR, the merge box should say "waiting for status checks".
- [ ] **Revoke the leaked NYT API keys.** Three old keys (`y9m4…`, `VI0s…`, `cpf0…`) are in the public git history. Revoke them on developer.nytimes.com, create a new key, and update `local.properties` and the `NYT_API_KEY` repo secret. Rewriting history doesn't help, because the keys are already public.

## P1: Correctness and robustness — DONE

- [x] **`CustomInterceptor`:**
  - Non-2xx responses are now closed before throwing, so the connection no longer leaks.
  - Status codes map to typed `ApiException`s in `utils/Exceptions.kt`: 401/403 → `InvalidApiKeyException`, 429 → `RateLimitedException`, 5xx → `ServerException`, anything else → `HttpStatusException`.
  - The interceptor now takes `isOnline` and `apiKey` as constructor arguments, so tests can supply their own.
- [x] **Connectivity check:** `Util.isNetworkAvailable` now uses `NET_CAPABILITY_INTERNET`, so VPN and every other transport count as online. The stray `@ApplicationContext` is gone.
- [x] **Error UI:**
  - The snackbar is gone. The error screen now shows one title, a specific message and a Retry button.
  - `Resource.Error` carries a `@StringRes messageRes`, built from `Throwable.toMessageRes()`.
  - The ViewModel rethrows `CancellationException` instead of swallowing it.
- [x] **Hardcoded UI strings** moved to `strings.xml`, with new error messages for an invalid API key, rate limiting and server errors.
- [x] **Theme:** removed the `windowTranslucentStatus` and `windowTranslucentNavigation` flags. `enableEdgeToEdge()` handles the system bars.

## P2: Dependency hygiene — DONE

- [x] Replaced `hilt-navigation-compose` with `hilt-lifecycle-viewmodel-compose`. Navigation 2 (`navigation-compose` 2.9.0) is no longer on the classpath.
- [x] Updated the test libraries:
  - `com.nhaarman.mockitokotlin2:mockito-kotlin` 2.2.0 → `org.mockito.kotlin:mockito-kotlin` 6.4.0.
  - Removed `mockito-inline`, `androidx.arch.core:core-testing` and `espresso-intents`.
- [x] Replaced the JakeWharton converter with Retrofit's official `converter-kotlinx-serialization`.
- [x] `lifecycle-viewmodel-ktx` → `lifecycle-viewmodel`.
- [x] Tidied the catalog:
  - One version key each for `kotlin`, `hilt` and `lifecycle`.
  - Inline versions (`ksp`, `kotlinx-serialization-json`) moved into `[versions]`.
- [x] Removed the parcelize plugin and `@Parcelize` from the models. Nav keys are already `@Serializable`.
- [x] `gradle.properties`:
  - Removed `kotlin.stdlib.default.dependency=false` and `ksp.useKSP2=true` (the default now).
  - Enabled the **configuration cache**. This needed the Mockito agent argument provider in `app/build.gradle.kts` to stop capturing the build script.
- [x] Bumped AGP to 9.5.1 and navigation3 to 1.2.1.

## P3: Lint warnings — DONE (13 → 0)

- [x] Deleted the unused resources: `colors.xml`, `ic_arrow_right.png`, `ic_calendar.png`, `ic_circle_shape.xml` and the `titleName` string.
- [x] Added a `<monochrome>` layer to the adaptive icons, using a new shadow-free drawable, `ic_launcher_monochrome.xml`.
- [x] Added `tools:targetApi="33"` to `enableOnBackInvokedCallback`.
- [x] The AGP and navigation3 bumps cleared the GradleDependency warnings.
- [x] `AppConstant` is now an `object`.

## P4: CI workflows (`.github/workflows/`) — OPEN

- [ ] **Run lint in CI.** Add `./gradlew lintDebug` to Test or Build and upload the report.
- [ ] **Fix the Test timeout.** In `test.yml`, `timeout-minutes: 15` sits under `env:`, so it does nothing. Move it to the job level.
- [ ] **Make the two workflows consistent.** Build uses `zulu` and Test uses `temurin`, and only Test uses `gradle/actions/setup-gradle`. Pick one setup for both.
- [ ] **Keep CI history longer.** `delete.yml` deletes runs older than 7 days on every push to master. That removed the history that would have shown when master broke. Run it on a weekly schedule and/or keep runs for 30 days.
- [ ] Run the emulator tests on API 37 (targetSdk) as well as API 29. CI on API 29 alone missed the espresso 3.5.0 crash that only shows on API 37. Optionally add API 26 (minSdk) too.
- [x] Third-party actions are pinned to commit SHAs (done in #382, see P6).
- [ ] Optional: `assembleRelease` in CI has no `NYT_API_KEY` and no signing config, so it only proves the build compiles. Add release signing from secrets only if you plan to publish.

## P5: Test coverage — DONE (10 → 26 unit tests)

- [x] `CustomInterceptorTest` (MockWebServer): adding the `api-key`, failing fast when offline, mapping 401/403/429/5xx/other, and closing the response body.
- [x] `PopularRepoTest`: returning `results`, `null` → empty list, and running on the injected IO dispatcher.
- [x] `NetworkModuleTest`: the debug logger never prints the `api-key` value.
- [x] `ExceptionsTest`: each error maps to the right message. `UtilTest` (Robolectric): Wi-Fi, VPN-only, no internet capability, no active network.
- [x] `PopularVMTest`: now checks the error message resource, including the invalid-key case.

## P6: Follow-ups from the November review — DONE (#382)

- [x] **Accessibility:** the error block is a polite live region, so TalkBack announces a failed load. That announcement was lost when the snackbar was removed.
- [x] **Retry guard:** `retry()` is ignored while a fetch is already running, so double taps no longer start parallel requests.
- [x] **`ArticleCard`:**
  - uses `Card(onClick)`, so the ripple is clipped to the card and the card gets button semantics
  - the chevron tint comes from the theme instead of `Color.Gray`
- [x] **Icons:** `material-icons-extended` is gone. Its two icons are now auto-mirrored vector drawables.
- [x] **Dark launch theme:** added `values-night/styles.xml`, so there's no white flash at launch in dark mode.
- [x] **Gradle:**
  - repositories moved into `dependencyResolutionManagement` (`FAIL_ON_PROJECT_REPOS`)
  - removed three `gradle.properties` flags that are now defaults
  - removed the unused androidTest deps (`androidx.test:core`, junit-ktx)
  - `espresso-core` 3.7.0 stays explicit. Without it, Compose ui-test pulls in 3.5.0, which crashes instrumented tests on API 37.
  - a configuration-time warning when `NYT_API_KEY` is missing
- [x] `TestCoroutineRule` restores `Dispatchers.Main` in a `finally` block.
- [x] **CI:** the third-party actions are pinned to commit SHAs: delete-workflow-runs, fetch-metadata, setup-gradle, and android-emulator-runner.

## P7: Later decisions (no urgency)

- [ ] Replace `Resource<T>` with a `sealed interface UiState`, together with the next item.
- [ ] Add a UI model separate from the network DTO, and navigate by article ID instead of passing the whole `ResultsItem`. This needs a cache or single source of truth first; see the offline-cache feature idea.
- [ ] Missing test: a cancelled fetch must not emit `Resource.Error`.
- [ ] Repo hygiene:
  - `.mcp.json` is tracked even though it's in `.gitignore`.
  - `.gemini/settings.json.bak` is committed.
  - Agent configs contain absolute local paths.
  - `.claude/settings.json` hooks hardcode `/Users/...`; use `$CLAUDE_PROJECT_DIR`.
- [ ] Lint suggests moving `mipmap-anydpi-v26` to `mipmap-anydpi` (minSdk 26) and bumping Gradle to 9.9.0. Dependabot will handle the Gradle bump.

## Known limitation (no action unless the app ships)

The NYT key is compiled into `BuildConfig.API_KEY`, so anyone can extract it from the APK. That is fine for a demo app. A real fix needs a small backend proxy that holds the key.

## Feature ideas (optional)

- Open the full article (the `url` field, through Custom Tabs) from the detail screen.
- Thumbnails from the `media` field.
- Pull-to-refresh, and a 1, 7 or 30-day period selector.
- A list-detail layout on tablets and foldables using Navigation 3 scenes.
- An offline cache (Room or DataStore) for the last successful list.
- NYT attribution ("Data provided by The New York Times") in the UI. The API terms require it.
- **Platform showcase:**
  - Glance home-screen widget
  - AppFunctions, so assistants can fetch the top stories
  - Baseline Profiles
  - screenshot tests
- **Architecture:** split into `core:network`, `core:data`, and `feature:popular` modules with convention plugins.
