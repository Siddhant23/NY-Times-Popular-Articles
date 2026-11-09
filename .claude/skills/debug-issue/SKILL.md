---
name: debug-issue
description: Systematically debug a bug, crash, failing test, or build failure in this Android app using graph-powered code navigation. Use when the user reports something broken (wrong UI state, network error, test failure, Gradle/manifest-merge error).
---

# Debug Issue

Trace the issue from symptom to root cause with the code-review-graph MCP tools, then confirm it with a reproducing test or build.

## Steps

1. `get_minimal_context_tool(task="debug: <symptom>")` to orient yourself.
2. Find the code involved with `semantic_search_nodes_tool` (e.g. `PopularVM`, `CustomInterceptor`, `PopularListScreen`).
3. Trace call chains with `query_graph_tool` using `callers_of` and `callees_of`. Use `get_flow_tool` / `get_affected_flows_tool` to find the entry point.
4. If the bug is new, run `detect_changes_tool` (pass `base` = last known-good commit). Most regressions here come from Dependabot bumps, so also check `git log -- gradle/libs.versions.toml`.
5. Reproduce before fixing:
   - Logic/UI bug: write a failing test in `app/src/test` (Robolectric + Compose rule, or ViewModel test with `TestCoroutineRule`).
   - Build bug: run the failing Gradle task with the JBR JDK (see `CLAUDE.md` → Build & test).
6. Fix, then rerun the test/task. Use `get_impact_radius_tool` to check what else the fix touches.

## Project-specific hot spots

- **Network errors**: `CustomInterceptor` throws `NoConnectivityException` / `UnknownException`. These surface as `Resource.Error` in `PopularVM`, then as a snackbar plus a retry screen. An empty error message usually means an HTTP/2 response with a blank `message`.
- **401 responses**: `NYT_API_KEY` is missing or revoked in `local.properties` / CI env. Never print the key while debugging.
- **Manifest merger `minSdkVersion` errors**: a library bump raised its minSdk. Raise `minSdk` in `app/build.gradle.kts`.
- **Coroutine test flakiness**: tests must use `TestCoroutineRule` and `advanceUntilIdle()`. Never use real dispatchers.

## Token efficiency

- Pass `detail_level="minimal"` where supported, and escalate to `"standard"` only when needed.
- For a single known file, just read it. The graph pays off for cross-file questions.
