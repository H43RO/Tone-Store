# PedalBoardListScreen Visual Preview Enhancement

## TL;DR

> **Quick Summary**: Add visual pedalboard preview to each card in the pedalboard list so users can identify boards by shape rather than name. Preview includes scaled-down grid with actual pedal colors/positions and mini expression pedal zone.
> 
> **Deliverables**:
> - `MiniPedalBoardPreview` composable (new file)
> - `MiniExpressionPedalZone` composable (new file)
> - Updated `PedalBoardCard` with preview at top
> - `MiniPedalCard` visibility change (private → internal)
> - Preview composables for visual testing
> 
> **Estimated Effort**: Medium
> **Parallel Execution**: NO - sequential (dependencies between tasks)
> **Critical Path**: Task 1 → Task 2 → Task 3 → Task 4 → Task 5

---

## Context

### Original Request
User hypothesis: "Users can find their desired pedalboard more easily by visual shape rather than by name"

Current `PedalBoardCard` shows only:
- GridView icon (52dp)
- Name
- Meta chips (`4×2`, `3/8 pedals`)
- Delete button

User wants to add a visual preview showing actual pedal arrangement.

### Interview Summary
**Key Discussions**:
- **Option chosen**: Wide preview at card top (full-width grid + expression zone)
- **Scale**: 40dp slot height (original 140dp, ~28% scale)
- **Empty slots**: Preserve grid positions but hide visually (transparent)
- **Expression pedal**: Include mini version in preview
- **MiniPedalCard reuse**: Make it internal/public in PedalSlot.kt
- **Grid scaling**: Fixed max-width with aspect ratio preservation

**Research Findings**:
- `PedalBoardGrid.kt` uses `LazyVerticalGrid` with drag gestures - NOT suitable for preview (too heavy)
- `MiniPedalCard` is private in `PedalSlot.kt` (line 133) - needs visibility change
- Grid configs vary: 2×1 (2 slots) to 8×4 (32 slots) - preview must handle gracefully
- `ExpressionPedalZone` is 80dp×200dp with click handlers - needs simplified version

### Metis Review
**Identified Gaps** (addressed):
- Performance with large grids → Use lightweight composable, not LazyVerticalGrid
- Inconsistent visual density → Fixed max-width with aspect ratio preservation
- MiniPedalCard code duplication → Make internal instead of duplicating
- Card height explosion → Preview height scales with rows, reasonable bounds

---

## Work Objectives

### Core Objective
Add a visual thumbnail preview to each `PedalBoardCard` showing the actual pedal arrangement, so users can visually identify their pedalboards without reading names.

### Concrete Deliverables
1. `MiniPedalBoardPreview.kt` - New composable in `presentation/ui/pedalboard/components/`
2. `MiniExpressionPedalZone.kt` - New composable in `presentation/ui/pedalboard/components/`
3. Modified `PedalBoardCard` - Column layout with preview at top
4. Modified `MiniPedalCard` - Changed from `private` to `internal`
5. Preview composables for visual verification

### Definition of Done
- [x] `./gradlew assembleDebug` → BUILD SUCCESSFUL
- [x] `./gradlew ktlintCheck` → No errors
- [x] All Preview composables render without crash in Android Studio
- [x] Preview correctly shows pedal positions and colors
- [x] Preview correctly shows expression pedal when present
- [x] Empty slots are invisible (transparent) but preserve grid positions

### Must Have
- Visual preview showing actual pedal arrangement from `SavedPedalBoard.slots`
- Pedal colors visible in preview
- Expression pedal zone when `expressionPedal != null`
- Fixed max-width preview with aspect ratio preservation
- Non-interactive preview (display only)

### Must NOT Have (Guardrails)
- ❌ NO click handlers or interactivity in preview
- ❌ NO animations in preview
- ❌ NO modifications to existing `PedalBoardGrid.kt`
- ❌ NO modifications to existing `ExpressionPedalZone.kt`
- ❌ NO changes to existing card info section styling (icon, text, delete button)
- ❌ NO use of `LazyVerticalGrid` in preview (performance)
- ❌ NO knob value display (keep generic 50% position from MiniKnobIndicator)
- ❌ NO drag gesture handling in preview

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: No (no test framework setup in project)
- **User wants tests**: NO (quick iteration, manual preview verification)
- **Framework**: None

### Automated Verification Only (NO User Intervention)

Since no test infrastructure exists and user opted for manual verification, all acceptance criteria will be verified via:

1. **Build verification** - `./gradlew assembleDebug`
2. **Lint verification** - `./gradlew ktlintCheck`
3. **Preview composables** - Visual inspection in Android Studio (Preview pane)

**Preview Configurations to Create:**
```kotlin
@Preview(name = "2x1 Single Pedal", showBackground = true)
@Preview(name = "4x2 Mixed (default)", showBackground = true)
@Preview(name = "8x4 Full Board", showBackground = true)
@Preview(name = "4x2 Empty Board", showBackground = true)
@Preview(name = "4x2 With Expression Pedal", showBackground = true)
@Preview(name = "PedalBoardCard with Preview", showBackground = true)
```

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately):
└── Task 1: Change MiniPedalCard visibility [no dependencies]

Wave 2 (After Wave 1):
├── Task 2: Create MiniExpressionPedalZone [depends: 1]
└── Task 3: Create MiniPedalBoardPreview [depends: 1]

Wave 3 (After Wave 2):
└── Task 4: Update PedalBoardCard [depends: 2, 3]

Wave 4 (After Wave 3):
└── Task 5: Build verification & lint check [depends: 4]

Critical Path: Task 1 → Task 3 → Task 4 → Task 5
```

### Dependency Matrix

| Task | Depends On | Blocks | Can Parallelize With |
|------|------------|--------|---------------------|
| 1 | None | 2, 3 | None |
| 2 | 1 | 4 | 3 |
| 3 | 1 | 4 | 2 |
| 4 | 2, 3 | 5 | None |
| 5 | 4 | None | None |

### Agent Dispatch Summary

| Wave | Tasks | Recommended Agents |
|------|-------|-------------------|
| 1 | 1 | delegate_task(category="quick", load_skills=[]) |
| 2 | 2, 3 | delegate_task(category="visual-engineering", load_skills=["frontend-ui-ux"]) - can parallelize |
| 3 | 4 | delegate_task(category="visual-engineering", load_skills=["frontend-ui-ux"]) |
| 4 | 5 | delegate_task(category="quick", load_skills=[]) |

---

## TODOs

- [x] 1. Change MiniPedalCard visibility from private to internal

  **What to do**:
  - In `PedalSlot.kt`, change `private fun MiniPedalCard` to `internal fun MiniPedalCard`
  - This allows other files in the same module to access it for the preview

  **Must NOT do**:
  - Do NOT move MiniPedalCard to a different file
  - Do NOT change any logic inside MiniPedalCard
  - Do NOT modify MiniKnobIndicator (keep it private)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Single-line change, trivial modification
  - **Skills**: `[]`
    - No special skills needed for visibility modifier change
  - **Skills Evaluated but Omitted**:
    - `frontend-ui-ux`: Not needed for visibility change, no UI logic

  **Parallelization**:
  - **Can Run In Parallel**: NO (first task)
  - **Parallel Group**: Wave 1 (alone)
  - **Blocks**: Tasks 2, 3
  - **Blocked By**: None (can start immediately)

  **References** (CRITICAL):

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt:132-133` - The function declaration to modify

  **Exact Change**:
  - Line 133: Change `private fun MiniPedalCard` to `internal fun MiniPedalCard`

  **Acceptance Criteria**:

  ```bash
  # Agent runs:
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL (compilation succeeds with internal visibility)
  
  ./gradlew ktlintCheck
  # Assert: No lint errors
  ```

  **Evidence to Capture:**
  - [ ] Build output showing successful compilation

  **Commit**: YES
  - Message: `refactor(pedalboard): make MiniPedalCard internal for preview reuse`
  - Files: `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

- [x] 2. Create MiniExpressionPedalZone composable

  **What to do**:
  - Create new file `MiniExpressionPedalZone.kt` in `presentation/ui/pedalboard/components/`
  - Implement scaled-down version of ExpressionPedalZone (~24dp width × ~60dp height, proportional to 80dp×200dp)
  - Display-only: no click handlers, no remove button
  - Show pedal color and name when present
  - Show placeholder text "Wah/Whammy" when empty (scaled down)
  - Keep the foot pedal shape (rounded top, square bottom)
  - Add @Preview composables for visual verification

  **Must NOT do**:
  - Do NOT add any click handlers or interactivity
  - Do NOT modify existing `ExpressionPedalZone.kt`
  - Do NOT add IconButton for remove
  - Do NOT add animation

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: UI component creation with visual design considerations (scaling, proportions)
  - **Skills**: `["frontend-ui-ux"]`
    - `frontend-ui-ux`: Needed for visual proportions, color handling, layout decisions
  - **Skills Evaluated but Omitted**:
    - `playwright`: Not needed for component creation, no browser testing

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 3)
  - **Parallel Group**: Wave 2 (with Task 3)
  - **Blocks**: Task 4
  - **Blocked By**: Task 1

  **References** (CRITICAL):

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt:24-121` - Original implementation to simplify and scale down. Follow the foot pedal shape (RoundedCornerShape), color handling pattern, and Canvas stripe pattern.

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/Pedal.kt` - Pedal model with `name` and `color` properties
  - `app/src/main/java/com/haero/tonestore/domain/model/SavedPedalBoard.kt:22` - `expressionPedal: Pedal?` field

  **Scaling Calculations**:
  - Original: 80dp width × 200dp height
  - Target scale: ~28% (matching 40dp/140dp slot ratio)
  - Mini size: ~24dp width × ~60dp height (adjust for visual balance)

  **Key Visual Elements to Preserve**:
  - Foot pedal shape: `RoundedCornerShape(topStart=16.dp, topEnd=16.dp, bottomStart=4.dp, bottomEnd=4.dp)` → scale to ~5dp/1dp
  - Color background with stripe pattern (simplified or omitted for mini)
  - Name text (very small, may need to truncate)

  **Acceptance Criteria**:

  ```bash
  # Agent runs:
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  ./gradlew ktlintCheck
  # Assert: No lint errors
  ```

  **Preview Composables to Create:**
  ```kotlin
  @Preview(name = "Mini Expression - Empty", showBackground = true)
  @Composable
  private fun MiniExpressionPedalZoneEmptyPreview() {
      ToneStoreTheme {
          MiniExpressionPedalZone(expressionPedal = null)
      }
  }
  
  @Preview(name = "Mini Expression - With Wah", showBackground = true)
  @Composable
  private fun MiniExpressionPedalZoneWithPedalPreview() {
      ToneStoreTheme {
          MiniExpressionPedalZone(
              expressionPedal = Pedal(
                  id = "wah",
                  name = "Wah",
                  type = PedalType.PRESET,
                  color = 0xFF4CAF50.toInt(), // Green
                  knobs = emptyList()
              )
          )
      }
  }
  ```

  **Evidence to Capture:**
  - [ ] Build output showing successful compilation
  - [ ] Preview composables render (verify in Android Studio Preview pane)

  **Commit**: YES
  - Message: `feat(pedalboard): add MiniExpressionPedalZone for card preview`
  - Files: `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniExpressionPedalZone.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

- [x] 3. Create MiniPedalBoardPreview composable

  **What to do**:
  - Create new file `MiniPedalBoardPreview.kt` in `presentation/ui/pedalboard/components/`
  - Implement a display-only preview of the pedalboard grid
  - Use 40dp slot height (configurable via parameter with default)
  - Fixed max-width with aspect ratio preservation
  - Preserve grid positions but make empty slots invisible/transparent
  - Use `Row`/`Column` layout (NOT LazyVerticalGrid) for simplicity and performance
  - Reuse `MiniPedalCard` (now internal) for rendering pedals
  - Add @Preview composables for various configurations

  **Layout Structure**:
  ```
  Row {
    Box(gridArea) {
      Column {
        Row { slots[0], slots[1], ... } // row 0
        Row { slots[n], ... }           // row 1
        ...
      }
    }
    MiniExpressionPedalZone(expressionPedal) // if present
  }
  ```

  **Must NOT do**:
  - Do NOT use LazyVerticalGrid (too heavy for static preview)
  - Do NOT add drag gesture handling
  - Do NOT add click handlers
  - Do NOT add animation
  - Do NOT modify existing PedalBoardGrid.kt

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: UI component with complex layout logic, visual scaling, grid rendering
  - **Skills**: `["frontend-ui-ux"]`
    - `frontend-ui-ux`: Layout composition, visual spacing, responsive scaling
  - **Skills Evaluated but Omitted**:
    - `playwright`: Not needed for component creation

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 2)
  - **Parallel Group**: Wave 2 (with Task 2)
  - **Blocks**: Task 4
  - **Blocked By**: Task 1

  **References** (CRITICAL):

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt:132-184` - `MiniPedalCard` implementation to reuse for rendering individual pedals
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt:49-51` - Original dimensions to scale from (slotHeight=140.dp, horizontalSpacing=8.dp, verticalSpacing=12.dp)
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt:60` - Grid height calculation pattern

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/SavedPedalBoard.kt:16-25` - Input model with `slots`, `columns`, `rows`, `expressionPedal`
  - `app/src/main/java/com/haero/tonestore/domain/model/Pedal.kt` - Pedal model for slot contents

  **Scaling Calculations**:
  - Original slot height: 140.dp → Mini: 40.dp (~28%)
  - Original horizontal spacing: 8.dp → Mini: ~2.dp
  - Original vertical spacing: 12.dp → Mini: ~3.dp
  - Grid max width: calculate based on columns × slot width + spacing

  **Empty Slot Handling**:
  - When `slots[index] == null`, render a `Spacer` or `Box` with same dimensions but transparent/invisible
  - This preserves grid positions so pedal arrangement is accurate

  **Acceptance Criteria**:

  ```bash
  # Agent runs:
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  ./gradlew ktlintCheck
  # Assert: No lint errors
  ```

  **Preview Composables to Create:**
  ```kotlin
  @Preview(name = "2x1 Single Pedal", showBackground = true, widthDp = 200)
  @Composable
  private fun MiniPreview2x1() { /* 2 columns, 1 row, 1 pedal */ }
  
  @Preview(name = "4x2 Mixed", showBackground = true, widthDp = 350)
  @Composable
  private fun MiniPreview4x2Mixed() { /* Default size, some pedals */ }
  
  @Preview(name = "8x4 Full", showBackground = true, widthDp = 400)
  @Composable
  private fun MiniPreview8x4Full() { /* Max size, all slots filled */ }
  
  @Preview(name = "4x2 Empty Board", showBackground = true, widthDp = 350)
  @Composable
  private fun MiniPreview4x2Empty() { /* No pedals at all */ }
  
  @Preview(name = "With Expression Pedal", showBackground = true, widthDp = 350)
  @Composable
  private fun MiniPreviewWithExpression() { /* Grid + expression zone */ }
  ```

  **Evidence to Capture:**
  - [ ] Build output showing successful compilation
  - [ ] All 5 Preview composables render correctly

  **Commit**: YES
  - Message: `feat(pedalboard): add MiniPedalBoardPreview for visual card preview`
  - Files: `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

- [x] 4. Update PedalBoardCard to include preview

  **What to do**:
  - In `PedalBoardListScreen.kt`, modify `PedalBoardCard` composable (lines 269-346)
  - Change layout from `Row` to `Column`
  - Add `MiniPedalBoardPreview` at the top of the card
  - Keep existing info section (icon + name + meta chips + delete) below preview
  - Adjust card padding and spacing for new layout
  - Update existing preview composable if present

  **New Layout Structure**:
  ```
  Surface(card) {
    Column {
      // NEW: Preview area
      MiniPedalBoardPreview(
        slots = pedalBoard.slots,
        columns = pedalBoard.columns,
        rows = pedalBoard.rows,
        expressionPedal = pedalBoard.expressionPedal,
        modifier = Modifier.padding(12.dp).fillMaxWidth()
      )
      
      // EXISTING: Info row (unchanged logic, same styling)
      Row {
        GridViewIcon(52dp)
        Column { Name, MetaChips }
        DeleteButton
      }
    }
  }
  ```

  **Must NOT do**:
  - Do NOT change the existing info row styling (icon size, text style, delete button)
  - Do NOT change the Surface shape or colors
  - Do NOT add any new functionality beyond the preview

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: Layout restructuring, integrating new component into existing UI
  - **Skills**: `["frontend-ui-ux"]`
    - `frontend-ui-ux`: Layout composition, spacing adjustments, visual integration
  - **Skills Evaluated but Omitted**:
    - `playwright`: Not needed for layout changes

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (alone)
  - **Blocks**: Task 5
  - **Blocked By**: Tasks 2, 3

  **References** (CRITICAL):

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardListScreen.kt:269-346` - Current `PedalBoardCard` implementation to modify
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardListScreen.kt:287-344` - Existing Row layout to preserve as inner component

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/SavedPedalBoard.kt` - Model with all fields needed for preview
  - New `MiniPedalBoardPreview` composable signature (from Task 3)

  **Imports to Add**:
  - `import com.haero.tonestore.presentation.ui.pedalboard.components.MiniPedalBoardPreview`

  **Acceptance Criteria**:

  ```bash
  # Agent runs:
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  ./gradlew ktlintCheck
  # Assert: No lint errors
  ```

  **Preview Verification:**
  - Update or add `@Preview` for `PedalBoardCard` showing the integrated preview

  **Evidence to Capture:**
  - [ ] Build output showing successful compilation
  - [ ] Preview composable for card renders with preview section visible

  **Commit**: YES
  - Message: `feat(pedalboard): add visual preview to PedalBoardCard`
  - Files: `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardListScreen.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

- [x] 5. Final build verification and lint check

  **What to do**:
  - Run full build to ensure all changes compile correctly
  - Run lint check to verify code style compliance
  - Verify all new files are properly formatted
  - Document any warnings (non-blocking)

  **Must NOT do**:
  - Do NOT ignore build errors
  - Do NOT skip lint check

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Simple verification commands, no code changes
  - **Skills**: `[]`
    - No special skills needed for build/lint commands
  - **Skills Evaluated but Omitted**:
    - All skills: Not needed for verification

  **Parallelization**:
  - **Can Run In Parallel**: NO (final step)
  - **Parallel Group**: Wave 4 (alone)
  - **Blocks**: None (final task)
  - **Blocked By**: Task 4

  **References**: None (verification only)

  **Acceptance Criteria**:

  ```bash
  # Agent runs full build:
  ./gradlew clean assembleDebug
  # Assert: BUILD SUCCESSFUL
  # Assert: 0 errors
  
  # Agent runs lint:
  ./gradlew ktlintCheck
  # Assert: No lint errors
  # Note: Warnings are acceptable, document them
  
  # Agent verifies new files exist:
  ls -la app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt
  ls -la app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniExpressionPedalZone.kt
  # Assert: Both files exist
  ```

  **Evidence to Capture:**
  - [ ] Clean build output
  - [ ] Lint check output
  - [ ] File listing confirmation

  **Commit**: NO (verification only, no changes)

---

## Commit Strategy

| After Task | Message | Files | Verification |
|------------|---------|-------|--------------|
| 1 | `refactor(pedalboard): make MiniPedalCard internal for preview reuse` | PedalSlot.kt | `./gradlew assembleDebug` |
| 2 | `feat(pedalboard): add MiniExpressionPedalZone for card preview` | MiniExpressionPedalZone.kt | `./gradlew assembleDebug` |
| 3 | `feat(pedalboard): add MiniPedalBoardPreview for visual card preview` | MiniPedalBoardPreview.kt | `./gradlew assembleDebug` |
| 4 | `feat(pedalboard): add visual preview to PedalBoardCard` | PedalBoardListScreen.kt | `./gradlew assembleDebug` |
| 5 | (no commit) | - | Full verification |

---

## Success Criteria

### Verification Commands
```bash
# Full build verification
./gradlew clean assembleDebug
# Expected: BUILD SUCCESSFUL

# Lint verification
./gradlew ktlintCheck
# Expected: No errors

# New files exist
ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt
ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniExpressionPedalZone.kt
# Expected: Both files exist
```

### Final Checklist
- [x] All "Must Have" present
  - [x] Visual preview showing actual pedal arrangement
  - [x] Pedal colors visible
  - [x] Expression pedal zone when present
  - [x] Fixed max-width with aspect ratio preservation
  - [x] Non-interactive preview
- [x] All "Must NOT Have" absent
  - [x] No click handlers in preview
  - [x] No animations in preview
  - [x] PedalBoardGrid.kt unmodified
  - [x] ExpressionPedalZone.kt unmodified
  - [x] Existing card info section styling unchanged
- [x] All builds pass
- [x] All lint checks pass
- [x] Preview composables render correctly
