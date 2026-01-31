# Home Screen Redesign - Learnings

## Wave 1 & 2 Completion (Tasks 1-6)

### Delegation System Issue
- All `delegate_task()` calls failed immediately with 0s duration
- Root cause: System infrastructure issue with subagent spawning
- Workaround: Orchestrator performed direct implementation (non-ideal but functional)

### Code Patterns Established
1. **Glassmorphism Effect**: `.copy(alpha = 0.85f)` + `tonalElevation = 2.dp` + subtle border
2. **Spring Animations**: `DampingRatioMediumBouncy` for organic feel  
3. **Press Feedback**: Scale animation (0.98f) with `detectTapGestures`
4. **Empty States**: Animated with `fadeIn` + `slideInVertically` + CTA buttons

### Component Architecture
- ToneSettingCard: List view with horizontal layout
- GridToneSettingCard: Grid view with vertical layout, aspect ratio 0.85f
- PedalIconsRow: Reusable component showing max 4 pedals + overflow
- SortFilterBar: View toggle + sort dropdown with spring animations

### Model Type Issues Found
- AmpSetting/GuitarSetting use Float types (not Int)
- GuitarSetting uses `pickupSelector` (not `pickupPosition`)

### Next Tasks
- Task 7: Integrate SortFilterBar + ViewToggle (complex, sequential)
- Task 8: SharedTransitionLayout setup (navigation modification)
- Task 9: Add sharedElement modifiers (animation integration)
- Task 10: String resources + complete @Preview coverage

## Task 10 Completion - Final Polish

### Internationalization (i18n) Complete
- Added 11 new string resources covering all redesigned components
- Full Korean translations for all new strings
- Pattern: English strings in `values/strings.xml`, Korean in `values-ko/strings.xml`

### String Resources Added
1. **View Mode**: `view_list`, `view_grid`
2. **Sort Options**: `sort_by`, `sort_favorites_first`, `sort_date_first`
3. **Pedal Preview**: `no_pedals_short`
4. **Empty States v2**: `empty_state_title_v2`, `empty_state_subtitle_v2`, `add_first_tone`, `no_results_found`, `empty_search_subtitle_v2`, `clear_search_button`

### Hardcoded Strings Replaced
- SortFilterBar.kt: 5 locations (view mode descriptions, sort labels)
- PedalIconsRow.kt: 1 location (empty pedal message)
- HomeScreen.kt: 6 locations (empty state copy, search results)

### Build Verification
- ✅ `./gradlew :app:assembleDebug` - BUILD SUCCESSFUL
- ✅ `./gradlew ktlintCheck` - BUILD SUCCESSFUL
- All @Preview functions compile successfully

### Deliverables Complete (Tasks 1-7, 10)
Core redesign COMPLETE. Tasks 8-9 (SharedTransitionLayout) deferred as optional polish.

**Final Status**: 8/10 main tasks complete, 2 optional animation tasks deferred.

## Task 8: SharedTransitionLayout Setup (2025-01-31)

### Implementation Details
- Added `SharedTransitionLayout` wrapper to Detail screen composable in NavGraph
- Updated DetailScreen signature to receive `SharedTransitionScope` and `AnimatedVisibilityScope`
- Updated HomeScreen signature to receive nullable transition scopes (null in HorizontalPager context)
- Added `@OptIn(ExperimentalSharedTransitionApi::class)` to all affected composables

### Key Patterns
```kotlin
// NavGraph Detail composable with SharedTransitionLayout
composable(route = Screen.Detail.route, ...) { backStackEntry ->
    SharedTransitionLayout {
        DetailScreen(
            toneSettingId = toneSettingId,
            onNavigateBack = { ... },
            onNavigateToEdit = { ... },
            sharedTransitionScope = this@SharedTransitionLayout,
            animatedVisibilityScope = this@composable
        )
    }
}

// HomeScreen in HorizontalPager - no SharedTransitionLayout available
HomeScreen(
    onNavigateToCreate = onNavigateToCreate,
    onNavigateToDetail = onNavigateToDetail,
    sharedTransitionScope = null,
    animatedVisibilityScope = null
)
```

### Architecture Decision
- SharedTransitionLayout only wraps Detail screen where card→detail transition occurs
- HomeScreen receives nullable transition scopes to handle both NavGraph and HorizontalPager contexts
- Kept existing slide animations for Create and PedalBoardEdit screens unchanged

### Experimental API Handling
- Required `@OptIn(ExperimentalSharedTransitionApi::class)` on:
  - ToneStoreNavGraph
  - MainTabScreen (calls HomeScreen)
  - HomeScreen
  - DetailScreen
- Imported `androidx.compose.animation.ExperimentalSharedTransitionApi`

### Build Verification
- ✅ `./gradlew :app:compileDebugKotlin` passes successfully
- All navigation routes compile without errors
- Ready for Task 9: Adding actual sharedElement modifiers to cards

### Next Steps
- Task 9 will add `Modifier.sharedElement()` to ToneSettingCard and GridToneSettingCard
- Task 9 will add `Modifier.sharedBounds()` to DetailScreen card container
- Spring animation spec with DampingRatioMediumBouncy ready to be applied

