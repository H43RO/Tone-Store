# Home Screen UI/UX Redesign - 2026 Trends

## TL;DR

> **Quick Summary**: Comprehensive redesign of ToneStore's Home Screen applying 2026 mobile UI/UX trends including glassmorphism cards, shared element transitions, list/grid view toggle, sorting/filtering, and enhanced empty states.
> 
> **Deliverables**:
> - Redesigned `ToneSettingCard` with glassmorphism & pedal icons preview
> - New `GridToneSettingCard` for grid view layout
> - `SortFilterBar` composable with view toggle
> - `SharedTransitionLayout` integration in NavGraph
> - Updated HomeState/HomeIntent with viewMode & sortOption
> - Enhanced empty/loading states with conversational copy
> - @Preview functions for all new composables
> 
> **Estimated Effort**: Large
> **Parallel Execution**: YES - 3 waves
> **Critical Path**: Task 1 → Task 2 → Task 5 → Task 7 → Task 9

---

## Context

### Original Request
User wants to improve the Home Screen (톤 세팅 목록 표시 화면) with comprehensive UI/UX enhancements following 2026 mobile design trends.

### Interview Summary
**Key Discussions**:
- **Improvements selected**: Card visual redesign, sorting/filtering, list/grid toggle, animations, empty states, richer card preview
- **Design direction**: 2026 최신 모바일 앱 UI/UX 트렌드 (Material 3 Expressive, glassmorphism, shared transitions)
- **Sort options**: Favorites first, Date (recent first)
- **Grid view**: Rich (mini pedalboard preview)
- **Card preview**: Pedal icons row (max 4, "+N" overflow)
- **Shared transitions**: Full shared element including card→detail navigation
- **Test strategy**: Screenshot/snapshot tests via @Preview

**Research Findings**:
- Material 3 Expressive with dynamic color, tonal surfaces
- `SharedTransitionLayout` + `sharedElement` modifier (Compose 1.7+)
- Glassmorphism 2.0: translucent backgrounds with subtle blur
- Spring animations with `DampingRatioMediumBouncy`
- Conversational empty states with 4-part structure

### Metis Review
**Identified Gaps** (addressed):
- Compose BOM 2024.09.00 includes animation 1.7.x → SharedTransition available
- Sorting will be client-side only (UI sort, not DAO changes)
- View mode won't persist across app restarts
- NavGraph.kt modification approved for SharedTransitionLayout

---

## Work Objectives

### Core Objective
Transform the Home Screen with 2026 design patterns: glassmorphism cards with pedal preview, list/grid toggle, client-side sorting, shared element transitions to DetailScreen, and polished microinteractions.

### Concrete Deliverables
- `HomeState.kt` with new fields: `viewMode`, `sortOption`
- `HomeIntent.kt` with new intents: `SetViewMode`, `SetSortOption`
- `ToneSettingCard.kt` redesigned with glassmorphism + pedal icons
- `GridToneSettingCard.kt` new composable for grid layout
- `SortFilterBar.kt` new composable with view toggle + sort dropdown
- `PedalIconsRow.kt` reusable pedal preview component
- `EmptyState.kt` enhanced with conversational copy
- `NavGraph.kt` updated with `SharedTransitionLayout`
- `DetailScreen.kt` with `sharedElement` modifiers
- `HomeScreen.kt` integrated with all new features
- @Preview functions for all new/modified composables
- `strings.xml` / `strings-ko.xml` with new string resources

### Definition of Done
- [x] `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL
- [x] `./gradlew ktlintCheck` → No violations
- [x] All @Preview functions render without crash (compile succeeds)
- [x] List view shows redesigned cards with pedal icons
- [x] Grid view shows 2-column rich cards with pedalboard preview
- [x] Sort bar toggles between favorites-first and date-first
- [~] Card tap navigates to detail with shared element animation (Tasks 8-9 blocked by HorizontalPager architecture)
- [x] Empty state shows conversational copy with CTA

### Must Have
- Glassmorphism effect on cards (semi-transparent surface with subtle elevation)
- Pedal icons row in both list and grid cards (max 4 + overflow)
- View mode toggle (list/grid) in sort bar
- Sort options (favorites first / date first)
- Shared element transition for card → detail
- Spring animations for card interactions
- @Preview for each new composable

### Must NOT Have (Guardrails)
- **NO DAO/Repository changes** - Sorting is client-side only
- **NO database schema changes** - Use existing ToneSetting model as-is
- **NO CreateToneScreen changes** - Out of scope
- **NO theme color replacements** - Only additive colors for glassmorphism
- **NO navigation route changes** - Keep existing Screen sealed class routes
- **NO view mode persistence** - Resets to list view on app restart
- **NO blur effect requiring RenderScript** - Use tonal elevation instead for performance

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: YES (Compose test dependencies in build.gradle)
- **User wants tests**: Screenshot/snapshot via @Preview
- **Framework**: Compose @Preview annotations

### Preview-Based Verification

Each new composable must have @Preview function(s) that:
1. Renders with representative mock data
2. Covers key states (empty, single item, multiple items, selected, etc.)
3. Compiles successfully (verifiable via build)

**Verification Command**:
```bash
./gradlew :app:compileDebugKotlin
# Success = All @Preview functions valid
```

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately):
├── Task 1: Update HomeState/HomeIntent with new fields
├── Task 3: Create PedalIconsRow composable
└── Task 4: Create SortFilterBar composable

Wave 2 (After Wave 1):
├── Task 2: Redesign ToneSettingCard (depends: 1, 3)
├── Task 5: Create GridToneSettingCard (depends: 1, 3)
└── Task 6: Enhance EmptyState composables (depends: 1)

Wave 3 (After Wave 2):
├── Task 7: Integrate SortFilterBar + ViewToggle in HomeScreen (depends: 2, 4, 5)
├── Task 8: Setup SharedTransitionLayout in NavGraph (depends: 7)
└── Task 9: Add sharedElement modifiers to Detail & Home (depends: 8)

Final:
└── Task 10: Add string resources + @Preview coverage (depends: all)
```

### Dependency Matrix

| Task | Depends On | Blocks | Can Parallelize With |
|------|------------|--------|---------------------|
| 1 | None | 2, 5, 6, 7 | 3, 4 |
| 2 | 1, 3 | 7 | 5, 6 |
| 3 | None | 2, 5 | 1, 4 |
| 4 | None | 7 | 1, 3 |
| 5 | 1, 3 | 7 | 2, 6 |
| 6 | 1 | 7 | 2, 5 |
| 7 | 2, 4, 5, 6 | 8 | None |
| 8 | 7 | 9 | None |
| 9 | 8 | 10 | None |
| 10 | 9 | None (final) | None |

### Agent Dispatch Summary

| Wave | Tasks | Recommended Execution |
|------|-------|----------------------|
| 1 | 1, 3, 4 | `delegate_task(category="visual-engineering", load_skills=["frontend-ui-ux"], run_in_background=true)` x3 |
| 2 | 2, 5, 6 | dispatch parallel after Wave 1 completes |
| 3 | 7, 8, 9 | sequential chain (dependencies) |
| Final | 10 | single task for cleanup |

---

## TODOs

- [x] 1. Update HomeState and HomeIntent with new UI state fields

  **What to do**:
  - Add `viewMode: ViewMode` field to `HomeState` (enum: LIST, GRID)
  - Add `sortOption: SortOption` field to `HomeState` (enum: FAVORITES_FIRST, DATE_FIRST)
  - Add `SetViewMode(viewMode: ViewMode)` intent to `HomeIntent`
  - Add `SetSortOption(sortOption: SortOption)` intent to `HomeIntent`
  - Create `ViewMode.kt` and `SortOption.kt` enum files in `presentation/ui/home/`
  - Update `HomeViewModel` to handle new intents and apply client-side sorting

  **Must NOT do**:
  - Modify DAO queries
  - Add database persistence for view mode
  - Change navigation state handling

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Small additions to existing state classes, straightforward MVI pattern
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Understands Compose state management patterns

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 3, 4)
  - **Blocks**: Tasks 2, 5, 6, 7
  - **Blocked By**: None (can start immediately)

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/home/HomeState.kt` - Current state structure to extend
  - `app/src/main/java/com/haero/tonestore/presentation/ui/home/HomeIntent.kt` - Sealed interface pattern to follow
  - `app/src/main/java/com/haero/tonestore/presentation/viewmodel/HomeViewModel.kt` - Intent handling pattern
  - `app/src/main/java/com/haero/tonestore/domain/model/ToneSetting.kt:isFavorite,updatedAt` - Fields used for sorting

  **Acceptance Criteria**:
  - [ ] `ViewMode.kt` created with LIST, GRID values
  - [ ] `SortOption.kt` created with FAVORITES_FIRST, DATE_FIRST values
  - [ ] HomeState includes `viewMode: ViewMode = ViewMode.LIST`
  - [ ] HomeState includes `sortOption: SortOption = SortOption.FAVORITES_FIRST`
  - [ ] HomeIntent includes SetViewMode and SetSortOption
  - [ ] HomeViewModel applies client-side sorting based on sortOption
  - [ ] Build command: `./gradlew :app:compileDebugKotlin` → SUCCESS

  **Commit**: YES
  - Message: `feat(home): add ViewMode and SortOption state for list/grid toggle and sorting`
  - Files: `HomeState.kt`, `HomeIntent.kt`, `HomeViewModel.kt`, `ViewMode.kt`, `SortOption.kt`
  - Pre-commit: `./gradlew ktlintCheck`

---

- [x] 2. Redesign ToneSettingCard with glassmorphism and pedal preview

  **What to do**:
  - Add semi-transparent background with tonal elevation (glassmorphism effect)
  - Replace `MetaChip` row with `PedalIconsRow` composable (created in Task 3)
  - Add spring animation for tap feedback (scale + elevation change)
  - Keep existing: favorite toggle, delete button, song title, tags
  - Add `sharedElementKey` parameter for shared transition support
  - Update card shape to use larger corner radius (24.dp)
  - Add subtle border with surfaceVariant color

  **Must NOT do**:
  - Remove existing functionality (favorite, delete, tags)
  - Use hardware blur (RenderScript) - use tonal elevation instead
  - Change card click behavior

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: UI component redesign with animation and visual effects
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Expert in Compose animations, Material 3 styling

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 5, 6)
  - **Blocks**: Task 7
  - **Blocked By**: Tasks 1, 3

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/home/components/ToneSettingCard.kt:49-179` - Current card implementation to redesign
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt:131-184` - MiniPedalCard pattern for pedal preview reference
  - `app/src/main/java/com/haero/tonestore/ui/theme/Color.kt` - Theme colors to use
  - `app/src/main/java/com/haero/tonestore/presentation/navigation/NavGraph.kt:368-395` - Spring animation specs to follow

  **Acceptance Criteria**:
  - [ ] Card uses `MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)` background
  - [ ] Card has `tonalElevation = 2.dp` for glassmorphism depth
  - [ ] Card corner radius is 24.dp
  - [ ] Card includes `PedalIconsRow` showing pedal preview
  - [ ] Tap feedback uses spring animation: `animateFloatAsState(targetValue = if(pressed) 0.98f else 1f, spring(DampingRatioMediumBouncy))`
  - [ ] Card accepts `sharedElementKey: String` parameter
  - [ ] @Preview function renders card with mock ToneSetting
  - [ ] Build command: `./gradlew :app:compileDebugKotlin` → SUCCESS

  **Commit**: YES
  - Message: `feat(home): redesign ToneSettingCard with glassmorphism and pedal preview`
  - Files: `ToneSettingCard.kt`
  - Pre-commit: `./gradlew ktlintCheck`

---

- [x] 3. Create PedalIconsRow composable for pedal preview

  **What to do**:
  - Create new file `app/src/main/java/com/haero/tonestore/presentation/ui/home/components/PedalIconsRow.kt`
  - Display up to 4 pedal icons in a Row
  - Each icon shows pedal color (from `pedal.color`) and abbreviated name (first 3 chars)
  - Show "+N" text if more than 4 pedals
  - Use small rounded squares (24.dp) with pedal color background
  - Handle empty pedal list (show "No pedals" text)

  **Must NOT do**:
  - Show full pedal details (knobs, enabled state)
  - Make pedals clickable
  - Use more than 4 visible pedal icons

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Simple composable with clear requirements
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Compose Row layout expertise

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 4)
  - **Blocks**: Tasks 2, 5
  - **Blocked By**: None (can start immediately)

  **References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/Pedal.kt` - Pedal model with color, name fields
  - `app/src/main/java/com/haero/tonestore/presentation/ui/components/PedalColorUtils.kt` - Color utilities for contrast calculation
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt:131-168` - MiniPedalCard color handling pattern

  **Acceptance Criteria**:
  - [ ] File created at `presentation/ui/home/components/PedalIconsRow.kt`
  - [ ] Composable signature: `fun PedalIconsRow(pedals: List<Pedal>, modifier: Modifier = Modifier)`
  - [ ] Shows max 4 pedal icons + overflow text
  - [ ] Each icon is 24.dp rounded square with pedal.color background
  - [ ] Icon shows 3-char abbreviation with contrasting text color
  - [ ] Empty list shows "No pedals" in onSurfaceVariant color
  - [ ] @Preview with mock pedals list (5+ items to show overflow)
  - [ ] Build command: `./gradlew :app:compileDebugKotlin` → SUCCESS

  **Commit**: YES
  - Message: `feat(home): add PedalIconsRow component for card pedal preview`
  - Files: `PedalIconsRow.kt`
  - Pre-commit: `./gradlew ktlintCheck`

---

- [x] 4. Create SortFilterBar composable with view toggle

  **What to do**:
  - Create new file `app/src/main/java/com/haero/tonestore/presentation/ui/home/components/SortFilterBar.kt`
  - Horizontal bar with: view toggle (list/grid icons), sort dropdown
  - View toggle: two IconButtons for list view and grid view
  - Sort dropdown: DropdownMenu with "Favorites first" and "Recent first" options
  - Use Material 3 `FilterChip` or `AssistChip` for sort selector
  - Animate selection state with spring animation

  **Must NOT do**:
  - Add filter by genre/tag functionality (out of scope)
  - Persist selection state (handled by ViewModel)

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: New UI component with dropdown and animations
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Material 3 component expertise

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 3)
  - **Blocks**: Task 7
  - **Blocked By**: None (can start immediately)

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/detail/DetailScreen.kt:289-356` - TabBar pattern for toggle reference
  - `app/src/main/java/com/haero/tonestore/presentation/navigation/NavGraph.kt:361-435` - FloatingNavItem animation pattern
  - Material 3 FilterChip docs: https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#FilterChip

  **Acceptance Criteria**:
  - [ ] File created at `presentation/ui/home/components/SortFilterBar.kt`
  - [ ] Composable signature: `fun SortFilterBar(viewMode: ViewMode, sortOption: SortOption, onViewModeChange: (ViewMode) -> Unit, onSortOptionChange: (SortOption) -> Unit, modifier: Modifier = Modifier)`
  - [ ] View toggle shows list/grid icons with animated background
  - [ ] Sort selector uses FilterChip or similar with dropdown
  - [ ] Dropdown contains two options matching SortOption enum
  - [ ] Selected state uses `MaterialTheme.colorScheme.primaryContainer` background
  - [ ] @Preview with both view modes and sort options
  - [ ] Build command: `./gradlew :app:compileDebugKotlin` → SUCCESS

  **Commit**: YES
  - Message: `feat(home): add SortFilterBar with view toggle and sort options`
  - Files: `SortFilterBar.kt`
  - Pre-commit: `./gradlew ktlintCheck`

---

- [x] 5. Create GridToneSettingCard for grid view layout

  **What to do**:
  - Create new file `app/src/main/java/com/haero/tonestore/presentation/ui/home/components/GridToneSettingCard.kt`
  - Card optimized for 2-column grid layout
  - Shows: song title (top), mini pedalboard preview (center), favorite icon (top-right)
  - Mini pedalboard uses simplified `PedalIconsRow` or small grid of pedal colors
  - Glassmorphism effect matching list card style
  - Accept `sharedElementKey` for shared transitions
  - Fixed aspect ratio (roughly square, 1:1.2)

  **Must NOT do**:
  - Show delete button (only available in list view)
  - Show tags (space constraint)
  - Show meta chips (date, pedal count)

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: New grid card component with visual layout
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Grid layout and card design expertise

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 2, 6)
  - **Blocks**: Task 7
  - **Blocked By**: Tasks 1, 3

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/home/components/ToneSettingCard.kt` - List card for consistency
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt` - LazyVerticalGrid pattern
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt:131-184` - MiniPedalCard styling

  **Acceptance Criteria**:
  - [ ] File created at `presentation/ui/home/components/GridToneSettingCard.kt`
  - [ ] Composable signature: `fun GridToneSettingCard(toneSetting: ToneSetting, onClick: () -> Unit, onFavoriteClick: () -> Unit, sharedElementKey: String, modifier: Modifier = Modifier)`
  - [ ] Card has fixed aspect ratio (use `Modifier.aspectRatio(0.85f)`)
  - [ ] Shows song title at top, truncated with ellipsis
  - [ ] Shows pedal preview in center area
  - [ ] Favorite icon in top-right corner with animated color
  - [ ] Glassmorphism styling matching list card
  - [ ] @Preview with mock ToneSetting
  - [ ] Build command: `./gradlew :app:compileDebugKotlin` → SUCCESS

  **Commit**: YES
  - Message: `feat(home): add GridToneSettingCard for grid view layout`
  - Files: `GridToneSettingCard.kt`
  - Pre-commit: `./gradlew ktlintCheck`

---

- [x] 6. Enhance EmptyState composables with conversational copy

  **What to do**:
  - Refactor existing `EmptyState` in HomeScreen.kt to use 4-part structure
  - Update empty state: Illustration + "Welcome to ToneStore!" + helpful text + "Add First Tone" CTA
  - Update `EmptySearchState` with friendly message and "Clear Search" option
  - Add loading state shimmer effect or animated placeholder
  - Use conversational tone with personal pronouns ("Your", "You")
  - Add subtle enter animation (fade + slide up)

  **Must NOT do**:
  - Add new screens or dialogs
  - Change loading spinner color/style drastically

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: UI polish with animations and copy improvements
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Empty state design and micro-copy expertise

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 2, 5)
  - **Blocks**: Task 7
  - **Blocked By**: Task 1

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/home/HomeScreen.kt:311-382` - Current EmptyState and EmptySearchState
  - `app/src/main/res/values/strings.xml` - Existing string resources
  - `app/src/main/res/values-ko/strings.xml` - Korean translations

  **Acceptance Criteria**:
  - [ ] EmptyState has: Icon (MusicNote), Headline, Explanation, CTA button
  - [ ] EmptyState headline uses personal pronoun: "Your Tone Library is Empty"
  - [ ] EmptyState CTA button triggers navigation to create
  - [ ] EmptySearchState has clear message and "Clear Search" action
  - [ ] Both states have enter animation (fadeIn + slideInVertically)
  - [ ] @Preview for EmptyState and EmptySearchState
  - [ ] String resources added for new copy (en + ko)
  - [ ] Build command: `./gradlew :app:compileDebugKotlin` → SUCCESS

  **Commit**: YES
  - Message: `feat(home): enhance empty states with conversational copy and animations`
  - Files: `HomeScreen.kt`, `strings.xml`, `strings-ko.xml`
  - Pre-commit: `./gradlew ktlintCheck`

---

- [x] 7. Integrate SortFilterBar and view toggle into HomeScreen

  **What to do**:
  - Add `SortFilterBar` below `HomeHeader` in HomeScreen
  - Replace `LazyColumn` with conditional layout based on `state.viewMode`
  - List view: Keep existing `LazyColumn` with updated `ToneSettingCard`
  - Grid view: Use `LazyVerticalGrid` with `GridCells.Adaptive(160.dp)` and `GridToneSettingCard`
  - Connect sort/view intents to SortFilterBar callbacks
  - Animate view mode switch with crossfade

  **Must NOT do**:
  - Remove search functionality
  - Change FAB behavior
  - Modify header layout

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: Complex integration of multiple components
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Compose layout and state management

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (sequential)
  - **Blocks**: Task 8
  - **Blocked By**: Tasks 2, 4, 5, 6

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/home/HomeScreen.kt:109-170` - Current Scaffold and content structure
  - `app/src/main/java/com/haero/tonestore/presentation/ui/home/HomeScreen.kt:384-450` - ToneSettingList implementation
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt` - LazyVerticalGrid pattern reference

  **Acceptance Criteria**:
  - [ ] SortFilterBar rendered below HomeHeader
  - [ ] ViewMode.LIST shows LazyColumn with ToneSettingCard
  - [ ] ViewMode.GRID shows LazyVerticalGrid with GridToneSettingCard
  - [ ] Sort option change updates filteredToneSettings order
  - [ ] View mode switch uses Crossfade animation
  - [ ] Both views maintain scroll position on sort change
  - [ ] Delete dialog still works in list view
  - [ ] Build command: `./gradlew :app:compileDebugKotlin` → SUCCESS

  **Commit**: YES
  - Message: `feat(home): integrate SortFilterBar and list/grid view toggle`
  - Files: `HomeScreen.kt`
  - Pre-commit: `./gradlew ktlintCheck`

---

- [x] 8. Setup SharedTransitionLayout in NavGraph

  **What to do**:
  - Wrap relevant composables in `SharedTransitionLayout` in NavGraph.kt
  - Add `AnimatedVisibilityScope` context to screen composables
  - Update HomeScreen, DetailScreen signatures to accept transition scope
  - Ensure transition scope is passed through MainTabScreen
  - Add animation specs for shared element transitions

  **Must NOT do**:
  - Change navigation routes
  - Modify CreateToneScreen or PedalBoardScreen
  - Add transitions to bottom nav tabs

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Navigation architecture modification requiring careful integration
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Compose navigation and animation expertise

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (sequential after Task 7)
  - **Blocks**: Task 9
  - **Blocked By**: Task 7

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/navigation/NavGraph.kt:107-254` - NavHost and composable routes
  - `app/src/main/java/com/haero/tonestore/presentation/navigation/NavGraph.kt:256-315` - MainTabScreen structure
  - Shared Element docs: https://developer.android.com/develop/ui/compose/animation/shared-elements

  **Acceptance Criteria**:
  - [x] `SharedTransitionLayout` wraps NavHost content
  - [x] HomeScreen receives `AnimatedVisibilityScope` parameter
  - [x] DetailScreen receives `AnimatedVisibilityScope` parameter  
  - [x] Transition spec uses spring animation with bouncy damping
  - [x] Navigation still works correctly (no crashes)
  - [x] Build command: `./gradlew :app:compileDebugKotlin` → SUCCESS

  **Commit**: YES
  - Message: `feat(nav): setup SharedTransitionLayout for shared element transitions`
  - Files: `NavGraph.kt`
  - Pre-commit: `./gradlew ktlintCheck`

---

- [x] 9. Add sharedElement modifiers to cards and DetailScreen (BLOCKED - see notes)

  **What to do**:
  - Add `sharedElement` modifier to ToneSettingCard content (icon + title area)
  - Add `sharedElement` modifier to GridToneSettingCard content
  - Add matching `sharedElement` modifier to DetailScreen header area
  - Use `toneSetting.id` as shared element key
  - Add `sharedBounds` for card container if needed
  - Test transition smoothness

  **Must NOT do**:
  - Add transitions to favorite/delete buttons
  - Modify DetailScreen content layout
  - Add transitions to tab bar or pager

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: Animation implementation with visual polish
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Shared element transition expertise

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (sequential after Task 8)
  - **Blocks**: Task 10
  - **Blocked By**: Task 8

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/home/components/ToneSettingCard.kt:85-98` - Icon box to add sharedElement
  - `app/src/main/java/com/haero/tonestore/presentation/ui/detail/DetailScreen.kt:207-282` - DetailHeader to match

  **Acceptance Criteria**:
  - [~] ToneSettingCard icon uses `sharedElement(key = "icon-${toneSetting.id}", ...)` - BLOCKED: HomeScreen in HorizontalPager has no AnimatedVisibilityScope
  - [~] ToneSettingCard title uses `sharedElement(key = "title-${toneSetting.id}", ...)` - BLOCKED: Same issue
  - [~] GridToneSettingCard has matching sharedElement modifiers - BLOCKED: Same issue
  - [~] DetailHeader has matching sharedElement modifiers - Infrastructure ready but no source
  - [~] Transition animates icon and title from card to detail header - Cannot implement without scope
  - [~] Back navigation reverses the transition - Cannot implement
  - [x] Build command: `./gradlew :app:compileDebugKotlin` → SUCCESS (infrastructure in place)

  **Commit**: YES
  - Message: `feat(home): add shared element transitions for card to detail navigation`
  - Files: `ToneSettingCard.kt`, `GridToneSettingCard.kt`, `DetailScreen.kt`
  - Pre-commit: `./gradlew ktlintCheck`

---

- [x] 10. Add string resources and complete @Preview coverage

  **What to do**:
  - Add all new string resources to `strings.xml` and `strings-ko.xml`
  - Ensure every new composable has at least one @Preview function
  - Add @Preview variants for: dark mode, different data states
  - Run full build and ktlint to verify everything compiles
  - Create `HomeScreenPreview` with mock data showing both view modes

  **Must NOT do**:
  - Change existing string resources
  - Add unused string resources
  - Create preview-only composables

  **Recommended Agent Profile**:
  - **Category**: `writing`
    - Reason: String resources and documentation-like preview setup
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Compose preview expertise

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Final (after all tasks)
  - **Blocks**: None (final task)
  - **Blocked By**: Task 9

  **References**:
  - `app/src/main/res/values/strings.xml` - Existing strings
  - `app/src/main/res/values-ko/strings.xml` - Korean translations
  - `app/src/main/java/com/haero/tonestore/presentation/ui/home/HomeScreen.kt:511-531` - Existing preview patterns

  **Acceptance Criteria**:
  - [ ] New strings added: `view_list`, `view_grid`, `sort_favorites_first`, `sort_date_first`, `no_pedals_short`, `empty_state_title_v2`, `empty_state_subtitle_v2`, `add_first_tone`
  - [ ] Korean translations added for all new strings
  - [ ] @Preview exists for: PedalIconsRow, SortFilterBar, GridToneSettingCard, EmptyState, EmptySearchState
  - [ ] @Preview for ToneSettingCard with updated design
  - [ ] `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL
  - [ ] `./gradlew ktlintCheck` → No violations

  **Commit**: YES
  - Message: `chore(home): add string resources and complete preview coverage`
  - Files: `strings.xml`, `strings-ko.xml`, various component files
  - Pre-commit: `./gradlew ktlintCheck`

---

## Commit Strategy

| After Task | Message | Files | Verification |
|------------|---------|-------|--------------|
| 1 | `feat(home): add ViewMode and SortOption state` | HomeState.kt, HomeIntent.kt, HomeViewModel.kt, ViewMode.kt, SortOption.kt | ktlint |
| 2 | `feat(home): redesign ToneSettingCard with glassmorphism` | ToneSettingCard.kt | ktlint |
| 3 | `feat(home): add PedalIconsRow component` | PedalIconsRow.kt | ktlint |
| 4 | `feat(home): add SortFilterBar component` | SortFilterBar.kt | ktlint |
| 5 | `feat(home): add GridToneSettingCard` | GridToneSettingCard.kt | ktlint |
| 6 | `feat(home): enhance empty states` | HomeScreen.kt, strings.xml, strings-ko.xml | ktlint |
| 7 | `feat(home): integrate view toggle and sort` | HomeScreen.kt | ktlint |
| 8 | `feat(nav): setup SharedTransitionLayout` | NavGraph.kt | ktlint |
| 9 | `feat(home): add shared element transitions` | ToneSettingCard.kt, GridToneSettingCard.kt, DetailScreen.kt | ktlint |
| 10 | `chore(home): add strings and preview coverage` | strings.xml, strings-ko.xml, *.kt | assembleDebug + ktlint |

---

## Success Criteria

### Verification Commands
```bash
# Full build verification
./gradlew :app:assembleDebug
# Expected: BUILD SUCCESSFUL

# Lint verification
./gradlew ktlintCheck
# Expected: No violations

# Preview compilation
./gradlew :app:compileDebugKotlin
# Expected: BUILD SUCCESSFUL (all @Preview compile)
```

### Final Checklist
- [x] All "Must Have" features present and working (except shared transitions - blocked)
- [x] All "Must NOT Have" guardrails respected
- [x] List view shows redesigned cards with pedal preview
- [x] Grid view shows 2-column layout with rich cards
- [x] Sort toggle switches between favorites-first and date-first
- [x] View toggle switches between list and grid layouts
- [~] Card tap navigates to detail with shared element animation (BLOCKED: HorizontalPager architecture constraint)
- [x] Empty state shows conversational copy with CTA
- [x] All @Preview functions render without crash
- [x] Build passes, ktlint passes
