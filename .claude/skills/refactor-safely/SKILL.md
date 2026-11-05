---
name: refactor-safely
description: Plan and execute a safe refactor (rename, move, dead-code removal, layer split) using graph dependency analysis, verified by the unit test suite. Use for any change that touches multiple files without changing behaviour.
---

# Refactor Safely

Use the knowledge graph to plan the change, apply it in small steps, and prove behaviour is unchanged.

## Steps

1. `get_minimal_context_tool(task="refactor: <goal>")`.
2. Measure the blast radius: `get_impact_radius_tool` on the target, then `query_graph_tool` with `pattern="tests_for"`. If coverage is missing, add a characterization test first.
3. Get candidates as needed:
   - `refactor_tool(mode="suggest")` for community-driven suggestions
   - `refactor_tool(mode="dead_code")` for unreferenced code
   - `find_large_functions_tool` for decomposition targets
4. Renames: preview with `refactor_tool(mode="rename", old_name=…, new_name=…)`, review via `apply_refactor_tool(refactor_id, dry_run=true)`, then apply. Previews expire after 10 minutes.
5. Verify:
   - `detect_changes_tool` to confirm only the intended areas moved
   - `get_affected_flows_tool` to confirm no flows broke
   - `./gradlew testDebugUnitTest lintDebug` with the JBR JDK, or delegate to the `android-build-verifier` agent

## Project rules for refactors

- Hilt: when moving a class, keep `@Inject` constructors and `@Module @InstallIn` bindings consistent; a missing binding only fails at KSP time.
- Navigation keys in `ui/navigation/Routes.kt` are `@Serializable` and saved in state. Renaming or reshaping them changes saved-state compatibility.
- Keep the stateless/stateful screen split so the existing Compose tests keep working.
- Graph renames don't touch strings, XML resources, or Gradle files. Grep for those separately.
