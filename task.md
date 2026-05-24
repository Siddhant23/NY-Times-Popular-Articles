# Migration Task Tracker

## Phase 1 — Dependencies & Build
- `[x]` Update `libs.versions.toml` (add Compose BOM, Nav3, adaptive, remove XML deps)
- `[x]` Update root `build.gradle.kts` (compose-compiler plugin)
- `[x]` Update `app/build.gradle.kts` (compose plugin, features, deps)
- `[ ]` Gradle sync (verify build)

## Phase 2 — Theme
- `[x]` Create `ui/theme/Color.kt`
- `[x]` Create `ui/theme/Type.kt`
- `[x]` Create `ui/theme/Theme.kt`
- `[x]` Update `res/values/styles.xml` (M3 NoActionBar parent)

## Phase 3 — ViewModel Modernization
- `[x]` Update `PopularVM.kt` (LiveData → StateFlow, auto-fetch in init)

## Phase 4 — Navigation Keys / Routes
- `[x]` Create `ui/navigation/Routes.kt`

## Phase 5 — Screen Composables
- `[x]` Create `ui/components/ArticleCard.kt`
- `[x]` Create `ui/screens/PopularListScreen.kt`
- `[x]` Create `ui/screens/PopularDetailScreen.kt`

## Phase 6 — Single Activity + Navigation 3
- `[x]` Create `MainActivity.kt` with NavDisplay
- `[x]` Update `AndroidManifest.xml`

## Phase 7 — Cleanup
- `[x]` Delete `view/PopularActivity.kt`
- `[x]` Delete `view/PopularDetailActivity.kt`
- `[x]` Delete `view/BaseActivity.kt`
- `[x]` Delete `view/PopularAdapter.kt`
- `[x]` Delete `res/layout/activity_popular.xml`
- `[x]` Delete `res/layout/activity_popular_detail.xml`
- `[x]` Delete `res/layout/item_popular.xml`
- `[x]` Remove `viewBinding = true` from `app/build.gradle.kts`

## Phase 8 — Verify
- `[ ]` Run `./gradlew :app:assembleDebug`
- `[ ]` Fix any compilation errors
