# XML → Jetpack Compose Migration Plan
## NY-Times Popular Articles App

This plan covers the full migration of the NY-Times app from XML Views / `AppCompatActivity` to Jetpack Compose with Navigation 3, Material 3, and all current Android best practices.

---

## Project Audit Summary

### Current State

| Dimension | Current | Target |
|---|---|---|
| UI framework | XML Views + ViewBinding | Jetpack Compose (100%) |
| Navigation | Two `Activity` classes + `Intent` extras | Navigation 3 (`NavDisplay` + `NavEntry`) |
| Theme | `Theme.AppCompat.Light.DarkActionBar` (XML) | Material 3 `MaterialTheme` (Compose) |
| State | `LiveData` in ViewModel | `StateFlow` collected via `collectAsStateWithLifecycle` |
| Edge-to-edge | Partial — `enableEdgeToEdge()` in `BaseActivity`, manual `ViewCompat` inset listener per screen | Full — `Scaffold` + automatic inset handling |
| Styles API | N/A | **Experimental** Compose Styles (see note below) |
| Hilt DI | ✅ Already set up | Reused as-is |
| Kotlin | 2.3.21 | Reused as-is |
| AGP | 9.2.1 | Reused as-is |
| compileSdk / targetSdk | 36 | Bumped to **37** (required by Styles skill) |

### Files to be Created / Modified

**Screens (XML → Compose):**
- 3 XML layouts deleted: `activity_popular.xml`, `activity_popular_detail.xml`, `item_popular.xml`
- `PopularAdapter.kt` — deleted (replaced by `LazyColumn`)
- `PopularActivity.kt` — replaced by single-Activity host + Compose screens
- `PopularDetailActivity.kt` — deleted (replaced by Compose screen)
- `BaseActivity.kt` — becomes single `MainActivity.kt`

**Non-UI code kept unchanged:**
- `model/` package (API, data, repository) — untouched
- `di/` package — untouched
- `NYTimesApp.kt` — untouched
- `utils/Resource.kt` — untouched
- All existing tests — untouched

---

## User Review Required

> [!IMPORTANT]
> **Styles API is experimental.** The `styles` skill requires `compileSdk 37` and `androidx.compose.foundation:foundation 1.12.0-alpha01` (via Compose BOM `2026.04.01+`) plus an explicit opt-in annotation. The Styles API is for **custom components only** — Material 3 built-in components (buttons, cards) are not styleable via this API. Confirm you are comfortable using experimental APIs before I apply the Styles step.

> [!WARNING]
> **LiveData → StateFlow migration.** `PopularVM` currently uses `LiveData`. Migrating to `StateFlow` is the modern Compose-aligned pattern and avoids the extra `runtime-livedata` dependency. This is a small non-UI change. Let me know if you'd prefer to keep `LiveData` (using `observeAsState`) instead.

> [!NOTE]
> **Single-Activity architecture.** Both `PopularActivity` and `PopularDetailActivity` will be collapsed into a single `MainActivity`. `PopularDetailActivity` will be removed from `AndroidManifest.xml`. Navigation 3 handles screen transitions entirely within Compose.

---

## Open Questions

> [!IMPORTANT]
> **1. Styles API opt-in?** Should I apply the experimental Compose Styles API to the article card component (requires bumping `compileSdk` to 37 and adding an alpha Compose BOM)?
> - Option A: Yes — apply Styles API to `ArticleCard` (experimental, alpha deps)
> - Option B: No — use standard `MaterialTheme` tokens and `Modifier` styling

> [!IMPORTANT]
> **2. Adaptive / list-detail layout?** The app has a classic list→detail navigation pattern. Should I implement a **Material 3 Adaptive list-detail** scene strategy (shows both panes side-by-side on tablets/foldables)?
> - Option A: Yes — use `ListDetailSceneStrategy` from `adaptive-navigation3`
> - Option B: No — single-pane navigation is sufficient

---

## Proposed Changes

### Phase 1 — Dependencies & Build

---

#### [MODIFY] [libs.versions.toml](file:///Users/siddhant/Documents/Others/NY-Times/gradle/libs.versions.toml)

- Add `compose-bom = "2026.05.00"`
- Add `navigation3 = "1.0.0-alpha04"` (latest Navigation 3 alpha)
- Add `adaptive-navigation3` version alias
- Add `lifecycle-compose = "2.10.0"` for `collectAsStateWithLifecycle`
- Add `activity-compose = "1.13.0"`
- Remove: `appcompat`, `recyclerView`, `cardView`, `constraintLayout` (no longer needed post-migration)

#### [MODIFY] [build.gradle.kts (root)](file:///Users/siddhant/Documents/Others/NY-Times/build.gradle.kts)

- Add `compose-compiler` plugin alias (`org.jetbrains.kotlin.plugin.compose`) at root level with `apply false`

#### [MODIFY] [build.gradle.kts (app)](file:///Users/siddhant/Documents/Others/NY-Times/app/build.gradle.kts)

- Bump `compileSdk` to **37** (if Styles API accepted; otherwise keep 36)
- Apply `compose-compiler` plugin
- Add `buildFeatures { compose = true }`
- Add Compose BOM + `material3`, `ui`, `ui-tooling-preview`, `ui-test-junit4`
- Add `activity-compose`, `lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`
- Add `androidx.navigation3:navigation3-ui` and `androidx.navigation3:navigation3-runtime`
- (Optional) Add `androidx.compose.material3.adaptive:adaptive-navigation3`
- Remove: `appcompat`, `recyclerView`, `cardView`, `constraintLayout`, `activityKtx` (replaced by `activity-compose`)

---

### Phase 2 — Theme & Styles

---

#### [NEW] `ui/theme/Color.kt`

Material 3 color tokens derived from the existing brand colors:
- `colorPrimary = #008577` → seed color for M3 `ColorScheme`
- `colorAccent = #D81B60` → secondary color
- Light and dark scheme definitions using `lightColorScheme` / `darkColorScheme`

#### [NEW] `ui/theme/Type.kt`

Material 3 `Typography` object using system defaults (project has no custom fonts).

#### [NEW] `ui/theme/Theme.kt`

```kotlin
@Composable
fun NYTimesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
```

#### [NEW] `ui/theme/ComponentStyles.kt` *(only if Styles API approved)*

Experimental Compose Styles for the `ArticleCard` custom component:
- `articleCardStyle` — background, shape, elevation, min-height tokens
- Opt-in annotation: `@OptIn(ExperimentalFoundationStyleApi::class)`

#### [MODIFY] [styles.xml](file:///Users/siddhant/Documents/Others/NY-Times/app/src/main/res/values/styles.xml)

Change parent theme to `Theme.Material3.DayNight.NoActionBar` (the XML theme is only used for the Activity window — the Compose layer takes over all UI theming).

---

### Phase 3 — ViewModel Modernization

---

#### [MODIFY] [PopularVM.kt](file:///Users/siddhant/Documents/Others/NY-Times/app/src/main/java/com/test/android/siddhant/viewmodel/PopularVM.kt)

Replace `LiveData` with `StateFlow`:

```kotlin
private val _uiState = MutableStateFlow<Resource<List<ResultsItem>?>>(Resource.Loading())
val uiState: StateFlow<Resource<List<ResultsItem>?>> = _uiState.asStateFlow()
```

`fetchArticlesList()` becomes `init { fetchArticlesList() }` — auto-triggered on VM creation, no manual lifecycle launch needed.

---

### Phase 4 — Navigation Keys

---

#### [NEW] `ui/navigation/Routes.kt`

```kotlin
data object PopularListRoute
data class PopularDetailRoute(val abstract: String)
```

---

### Phase 5 — Screen Composables

---

#### [NEW] `ui/screens/PopularListScreen.kt`

Compose replacement for `PopularActivity` + `activity_popular.xml` + `item_popular.xml` + `PopularAdapter`:

- `PopularListScreen(onArticleClick: (ResultsItem) -> Unit, viewModel: PopularVM)`
- Collects `uiState` via `collectAsStateWithLifecycle()`
- Shows `CircularProgressIndicator` on loading
- Shows `Snackbar` via `SnackbarHostState` on error
- `LazyColumn` of `ArticleCard` composables (replacing RecyclerView + PopularAdapter)
- `Scaffold` with `TopAppBar` (M3 `CenterAlignedTopAppBar`)
- Insets handled automatically by `Scaffold`

#### [NEW] `ui/screens/PopularDetailScreen.kt`

Compose replacement for `PopularDetailActivity` + `activity_popular_detail.xml`:

- `PopularDetailScreen(abstract: String, onBack: () -> Unit)`
- `Scaffold` with `TopAppBar` + back navigation icon
- `Text` styled with `MaterialTheme.typography.bodyLarge`

#### [NEW] `ui/components/ArticleCard.kt`

Compose replacement for `item_popular.xml` + card layout:

- `ArticleCard(item: ResultsItem, onClick: () -> Unit)`
- M3 `Card` with `CardDefaults.cardElevation`
- Circle avatar placeholder (replacing `ic_circle_shape` drawable + `View`)
- Title, byline, date with proper M3 typography
- Trailing chevron icon
- *(If Styles approved)*: `Modifier.styleable(...)` with `ArticleCardStyle`

---

### Phase 6 — Single Activity Host + Navigation 3

---

#### [NEW] `MainActivity.kt`

Replaces both `PopularActivity` and `PopularDetailActivity`:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            NYTimesTheme {
                val backStack = rememberSaveableStateHolder()
                    .let { remember { mutableStateListOf<Any>(PopularListRoute) } }
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<PopularListRoute> {
                            PopularListScreen(
                                onArticleClick = { item ->
                                    backStack.add(PopularDetailRoute(item.abstract.orEmpty()))
                                }
                            )
                        }
                        entry<PopularDetailRoute> { route ->
                            PopularDetailScreen(
                                abstract = route.abstract,
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }
                    }
                )
            }
        }
    }
}
```

#### [MODIFY] [AndroidManifest.xml](file:///Users/siddhant/Documents/Others/NY-Times/app/src/main/AndroidManifest.xml)

- Replace `PopularActivity` launcher entry with `MainActivity`
- Remove `PopularDetailActivity` entry entirely
- Application theme → `@style/AppTheme` (updated to M3 NoActionBar)

---

### Phase 7 — Edge-to-Edge

---

Per the `edge-to-edge` skill checklist:

- ✅ `enableEdgeToEdge()` called in `MainActivity.onCreate()` (before `super`)
- ✅ `Scaffold` in each screen propagates `innerPadding` via `contentPadding` on `LazyColumn`
- ✅ M3 `TopAppBar` manages status bar insets automatically
- ✅ No `TextField` inputs in this app — IME handling not required
- ✅ `window.isNavigationBarContrastEnforced = false` set for screens with a bottom edge

---

### Phase 8 — Cleanup

---

#### [DELETE] `view/PopularActivity.kt`
#### [DELETE] `view/PopularDetailActivity.kt`
#### [DELETE] `view/BaseActivity.kt`
#### [DELETE] `view/PopularAdapter.kt`
#### [DELETE] `res/layout/activity_popular.xml`
#### [DELETE] `res/layout/activity_popular_detail.xml`
#### [DELETE] `res/layout/item_popular.xml`
#### [MODIFY] `build.gradle.kts` — remove `viewBinding = true`

---

## Final Package Structure

```
com.test.android.siddhant/
├── NYTimesApp.kt                    (unchanged)
├── di/                              (unchanged)
├── model/                           (unchanged)
├── utils/                           (unchanged - Resource.kt, etc.)
├── viewmodel/
│   └── PopularVM.kt                 (LiveData → StateFlow)
└── ui/
    ├── theme/
    │   ├── Color.kt                 (NEW)
    │   ├── Type.kt                  (NEW)
    │   ├── Theme.kt                 (NEW)
    │   └── ComponentStyles.kt       (NEW - if Styles approved)
    ├── navigation/
    │   └── Routes.kt                (NEW)
    ├── screens/
    │   ├── PopularListScreen.kt     (NEW)
    │   └── PopularDetailScreen.kt   (NEW)
    ├── components/
    │   └── ArticleCard.kt           (NEW)
    └── MainActivity.kt              (NEW - replaces both activities)
```

---

## Verification Plan

### Build validation
```bash
./gradlew :app:assembleDebug
```

### Compose Previews
Each new composable will include `@Preview` annotations:
- `ArticleCardPreview` — shows populated and empty states
- `PopularListScreenPreview` — loading, success, error states
- `PopularDetailScreenPreview` — sample abstract text

### Manual Verification
1. App launches on `PopularListScreen` (list of articles from API)
2. Tap an article → navigates to `PopularDetailScreen` with abstract text
3. Back press / back button → returns to list
4. System back gesture works correctly
5. Edge-to-edge: content scrolls under status bar, last list item visible above nav bar
6. Light/dark theme: colors and system bar icon tints correct

### Automated Tests
Existing unit tests (`PopularVM`, repository, etc.) remain unchanged.
