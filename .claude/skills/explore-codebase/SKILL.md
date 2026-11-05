---
name: explore-codebase
description: Navigate and explain this codebase's structure (layers, DI wiring, navigation, data flow) using the knowledge graph. Use when asked how something works, where something lives, or for an architecture overview.
---

# Explore Codebase

Use the code-review-graph MCP tools to answer structural questions, then read only the files that matter.

## Steps

1. `get_minimal_context_tool(task="explore: <question>")`.
2. Broad view: `list_graph_stats_tool`, then `get_architecture_overview_tool` (default `detail_level="minimal"`).
3. Modules: `list_communities_tool`, then `get_community_tool` for the relevant one.
4. Symbols: `semantic_search_nodes_tool`. For a file's contents, use `query_graph_tool` with `pattern="children_of"` or `"file_summary"`.
5. Relationships: `query_graph_tool` with `callers_of` / `callees_of` / `imports_of` / `importers_of` / `tests_for`.
6. Execution paths: `list_flows_tool`, then `get_flow_tool`.
7. Complexity: `find_large_functions_tool`.

## Mental model of this app

`MainActivity` (Nav3 `NavDisplay`) → `PopularListScreen` / `PopularDetailScreen` → `PopularVM` (`StateFlow<Resource<…>>`) → `PopularRepo` → `ApiService` (Retrofit) + `CustomInterceptor` → NYT Most Popular API. `di/` wires these with Hilt. See the table in `CLAUDE.md` → Architecture.

## Tips

- Start broad, then narrow down. Answer with file links (`path:line`), not file dumps.
- The graph covers Kotlin and shell. For Gradle (`*.gradle.kts`, `libs.versions.toml`), manifest, resources, and workflows, read the files directly.
