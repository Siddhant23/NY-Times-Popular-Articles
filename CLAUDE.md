# NY Times Popular Articles (Android)

Single-module Android app (`:app`) that lists the NYT "Most Viewed (7 days)" articles and shows a detail screen.
Package / applicationId: `com.test.android.siddhant`. Public repo: `Siddhant23/NY-Times-Popular-Articles`, default branch `master`.

The project is in **maintenance mode**: changes are mostly Dependabot bumps plus occasional modernisation passes (XML → Compose, Navigation 3, edge-to-edge were done this way).

## Stack

Kotlin · Jetpack Compose (Material 3) · Navigation 3 · Hilt (KSP) · Retrofit 3 + OkHttp 5 · kotlinx.serialization · Coroutines/StateFlow.
AGP 9 uses **built-in Kotlin** — there is no `kotlin-android` plugin; don't add one.
All versions live in `gradle/libs.versions.toml` — read it instead of trusting versions written anywhere else (README is stale).

## Build & test

The project targets JDK 21; the default shell JDK is 17. Always run Gradle with Android Studio's JBR:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew testDebugUnitTest          # JVM + Robolectric unit/Compose tests (fast, run these first)
./gradlew lintDebug                   # Android lint → app/build/reports/lint-results-debug.txt
./gradlew assembleDebug               # debug APK
./gradlew connectedDebugAndroidTest   # needs a running emulator/device
```

- `local.properties` must contain `sdk.dir` and `NYT_API_KEY` (git-ignored). CI reads `NYT_API_KEY` from the env. Without a key the app builds but every request returns 401.
- A green local run does **not** mean master is green — check CI (`gh run list --branch master`) too.

## Architecture

```
ui (Compose screens) → viewmodel (StateFlow<Resource<T>>) → model/repository → model/api (Retrofit) → NYT API
                         ↑ Hilt DI (di/) wires everything
```

| Path (`app/src/main/java/com/test/android/siddhant/`) | Role |
|---|---|
| `MainActivity.kt` | Single activity, `enableEdgeToEdge()`, Nav3 `NavDisplay` with back stack |
| `ui/navigation/Routes.kt` | `@Serializable` `NavKey`s (`PopularListRoute`, `PopularDetailRoute`) |
| `ui/screens/`, `ui/components/`, `ui/theme/` | Compose UI; each screen has a stateless overload used by tests |
| `viewmodel/PopularVM.kt` | `@HiltViewModel`, exposes `uiState: StateFlow<Resource<List<ResultsItem>>>` |
| `model/repository/PopularRepo.kt` | Calls API on `@IoDispatcher` |
| `model/api/` | `ApiService` + `CustomInterceptor(isOnline, apiKey)`: adds the `api-key` query param, throws `NoConnectivityException` when offline, and maps non-2xx to typed `ApiException`s after closing the response |
| `model/data/` | Network DTOs (also used directly by UI and navigation today) |
| `di/` | `NetworkModule`, `CoroutinesDispatchersModule`, qualifiers |
| `utils/` | `Resource` (`Error` carries a `@StringRes messageRes`), exceptions plus `Throwable.toMessageRes()`, constants, connectivity helper |

Tests:
- `app/src/test`: Robolectric, Compose UI tests, ViewModel tests with `TestCoroutineRule`, and network tests with `mockwebserver3`
- `app/src/androidTest`: Compose instrumentation with `HiltTestRunner`

Open work is tracked in `REMAINING_WORK.md`.

## Conventions

- UI is Compose only. Don't add XML layouts, Fragments, View Binding, or Navigation 2 (`androidx.navigation:*`).
- Keep each screen split into a stateful wrapper (takes the ViewModel) and a stateless composable (takes state + lambdas). Tests target the stateless one.
- Inject dispatchers via `@IoDispatcher`; never hardcode `Dispatchers.IO` in classes.
- Collect flows in UI with `collectAsStateWithLifecycle()`.
- In coroutines, don't swallow `CancellationException` (avoid bare `runCatching` around suspend calls; rethrow cancellation).
- User-visible text goes in `res/values/strings.xml`, never in string literals. Error text reaches the UI as a string resource ID, not as an exception message.
- Icons are vector drawables in `res/drawable/`. Don't add `material-icons-extended`.
- Use theme colors (`MaterialTheme.colorScheme.*`), not `Color.Gray` and the like.
- New dependencies go through `libs.versions.toml`, never inline coordinates in `build.gradle.kts`.
- Every bug fix or behaviour change adds or updates a unit test in `app/src/test`.
- Kotlin style: `kotlin.code.style=official`, trailing commas.

## Security rules

- **Never** hardcode, print, log, or commit the NYT API key. Don't read `local.properties` into the conversation; to get `sdk.dir`, use `grep '^sdk.dir' local.properties`.
- Old keys exist in git history; treat any key-like string you see there as compromised, and don't repeat it in output.
- Debug builds log HTTP traffic. Any logging interceptor change must redact the `api-key` query parameter.
- Don't weaken network security (no cleartext traffic, no trust-all TLS).

## CI & dependency gotchas

- Workflows:
  - `build.yml`: release APK
  - `test.yml`: unit tests and lint, plus instrumented tests on API 29 (required check `test (API 29)`) and on API 37 (non-blocking for now)
  - `dependabot-auto-merge.yml`: triggered on `pull_request` by Dependabot PRs. Don't switch it back to `pull_request_target`; GitHub blocks that trigger by default on public repos.
  - `delete.yml`: runs weekly and keeps 30 days of runs
- `dependabot-auto-merge.yml` auto-merges only patch and minor bumps. `--auto` waits for CI only if `master`'s branch protection requires the Build and Test checks (`REMAINING_WORK.md` P0). Until that's in place, check that the latest Build and Test runs passed before trusting `master`.
- Third-party actions are pinned to commit SHAs with a `# vX.Y.Z` comment. Keep that format when you edit a workflow.
- Library bumps can raise the effective `minSdk` (currently 26). On a manifest-merger "minSdkVersion … cannot be smaller" error, raise `minSdk`; don't use `tools:overrideLibrary`.
- Keep `espresso-core` 3.7.0+ as an explicit `androidTestImplementation`. Without it, Compose ui-test resolves espresso-core 3.5.0, which crashes every instrumented test on API 37 (`NoSuchMethodException: InputManager.getInstance`).
- The Gradle configuration cache is on. Build-script code must not capture script objects in task actions or providers.
- Don't reintroduce these artifacts, removed in the Nov 2026 cleanup:
  - `hilt-navigation-compose` (it pulls in Nav2)
  - the JakeWharton serialization converter
  - `com.nhaarman.mockitokotlin2`
  - `mockito-inline`
  - `lifecycle-viewmodel-ktx`
  - `espresso-intents`, `core-testing`
  - `material-icons-extended`
  - `kotlin-parcelize`

## Claude Code setup in this repo

- Project skills: `.claude/skills/*/SKILL.md` (graph-driven explore / debug / review / refactor).
- Project sub-agents: `.claude/agents/*.md`. Delegate to them instead of re-implementing their steps:
  - `android-build-verifier`: build, tests, lint
  - `android-code-reviewer`: review a diff against the rules above
  - `dependency-auditor`: version catalog, Dependabot, CI health
  - `android-test-writer`: add or extend tests
- Hooks in `.claude/settings.json` keep the code-review-graph index updated after edits.
- Other agent configs (`GEMINI.md`, `.gemini/`, `.github/code-review-graph.instruction.md`, `.vscode/`) are maintained separately. Only change them when asked.

<!-- code-review-graph MCP tools -->
## MCP Tools: code-review-graph

This project has a code-review-graph knowledge graph, exposed as MCP tools named `mcp__code-review-graph__<name>_tool`.
**Use the graph tools before Grep/Glob/Read for cross-file questions** (callers, impact, test coverage, architecture). For a single file whose path you already know, just read it — the app is only ~25 Kotlin files.

### When to use graph tools first

- **Exploring code**: `semantic_search_nodes_tool` or `query_graph_tool` instead of Grep
- **Understanding impact**: `get_impact_radius_tool` instead of manually tracing imports
- **Code review**: `detect_changes_tool` + `get_review_context_tool` instead of reading entire files
- **Finding relationships**: `query_graph_tool` with `callers_of` / `callees_of` / `imports_of` / `importers_of` / `children_of` / `tests_for` / `file_summary`
- **Architecture questions**: `get_architecture_overview_tool` + `list_communities_tool`

### Key tools

| Tool | Use when |
| ------ | ---------- |
| `get_minimal_context_tool` | First call for any task — compact stats, risk, and suggested next tools |
| `detect_changes_tool` | Reviewing code changes (risk-scored; pass `base` for branch reviews) |
| `get_review_context_tool` | Need source snippets for review, token-efficiently |
| `get_impact_radius_tool` | Understanding the blast radius of a change |
| `get_affected_flows_tool` | Finding which execution paths are impacted |
| `query_graph_tool` | Tracing callers, callees, imports, tests, dependencies |
| `semantic_search_nodes_tool` | Finding functions/classes by name or keyword |
| `get_architecture_overview_tool` | High-level structure |
| `refactor_tool` / `apply_refactor_tool` | Rename previews, dead code, refactor suggestions |

### Workflow

1. The graph auto-updates after edits (via the hooks in `.claude/settings.json`). If results look stale, run `code-review-graph update --repo "$CLAUDE_PROJECT_DIR"`.
2. Start with `get_minimal_context_tool(task=...)`, and pass `detail_level="minimal"` where it's supported.
3. Use `detect_changes_tool` for reviews, and `get_affected_flows_tool` for impact.
4. Use `query_graph_tool` with `pattern="tests_for"` to check coverage.
