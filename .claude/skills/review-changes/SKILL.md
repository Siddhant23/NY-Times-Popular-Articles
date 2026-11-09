---
name: review-changes
description: Perform a structured, risk-aware review of local changes, a branch, or a Dependabot PR using change detection, impact analysis, and this project's Android checklist. Use when asked to review a diff, PR, or "what changed".
---

# Review Changes

Run a risk-ranked review that combines the graph tools with this project's Android checklist.

## Steps

1. `get_minimal_context_tool(task="review: <target>")`.
2. `detect_changes_tool` (use `base="origin/master"` for a branch, or the merge-base for a PR).
3. `get_affected_flows_tool` for the impacted execution paths, then `get_impact_radius_tool` for the blast radius.
4. For each high-risk function, run `query_graph_tool(pattern="tests_for")`. Untested changes need a concrete test suggestion.
5. Gradle/catalog changes aren't in the graph, so read the diff of `gradle/libs.versions.toml` and `app/build.gradle.kts` directly. For Dependabot PRs, also check CI: `gh pr checks <n>`.

## Android checklist

- **Security**
  - No API key literals.
  - `local.properties` is untouched.
  - Logging redacts `api-key`.
  - No cleartext/TLS weakening.
  - No new exported components without need.
- **Coroutines**
  - Dispatchers are injected.
  - `CancellationException` isn't swallowed.
  - No `GlobalScope`.
  - Repeated `retry()` calls don't stack jobs.
- **Compose**
  - State is hoisted.
  - Strings come from resources.
  - Colors come from the theme.
  - `LazyColumn` items have stable keys.
  - Icons have `contentDescription` where meaningful.
- **Architecture**
  - No Nav2 / XML / View Binding reintroduced.
  - New deps go through `libs.versions.toml`.
- **Dependencies**
  - minSdk compatibility (manifest merge).
  - No deprecated artifacts (list in `CLAUDE.md`).
  - Major-version bumps get a changelog check.

## Output format

Group findings into high, medium, and low risk. For each one give:
- what changed, with `path:line`, and why it matters
- test coverage status
- the suggested fix or test

End with a one-line merge recommendation.
