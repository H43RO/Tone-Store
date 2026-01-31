## [2026-02-01] Task 1: LayoutStepper 컴포넌트 생성

### Implementation Pattern
- **Component Type**: Stateless Composable with callbacks
- **Parameters**: `columns: Int`, `rows: Int`, `onColumnsChange: (Int) -> Unit`, `onRowsChange: (Int) -> Unit`
- **Range Constraints**: columns 1-6, rows 1-4
- **UI Structure**: Row with two stepper groups (columns and rows)

### Material 3 Stepper Pattern
- **Button Style**: IconButton wrapped in Surface with CircleShape
- **Icons**: `Icons.Default.Remove` (−), `Icons.Default.Add` (+)
- **Disabled State**: 
  - Color: `MaterialTheme.colorScheme.surfaceVariant` (disabled), `MaterialTheme.colorScheme.primaryContainer` (enabled)
  - Button automatically disabled at min/max values via `enabled = (value > min)` / `enabled = (value < max)`
- **Content Color**: `MaterialTheme.colorScheme.onSurface`
- **Elevation**: `tonalElevation = 2.dp` on Surface

### Key Implementation Details
- Use `require()` for parameter validation in init
- Display format: "N열" / "N행" using stringResource for localization readiness
- Layout: `horizontalArrangement = Arrangement.spacedBy(24.dp)` for spacing between groups
- No haptic feedback (as per plan constraints)

### File Location
- Path: `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/LayoutStepper.kt`
- Size: 5737 bytes
- Build verification: ✅ SUCCESSFUL

### Verification Commands Used
```bash
./gradlew assembleDebug  # BUILD SUCCESSFUL
./gradlew ktlintCheck    # No errors for LayoutStepper
```

### Notes
- Component is ready for integration in Task 4
- Includes KDoc documentation (justified as public-facing component API)
- No preview function yet (will be added in Task 5)

## [2026-02-01] Task 2: PedalSlot Delete Button Overlay

### Implementation Pattern
- **New Parameters Added**:
  - `isEditing: Boolean = false` - Controls visibility of delete button
  - `onDeleteClick: () -> Unit = {}` - Callback for delete action
- **Conditional Rendering**: Delete button only shows when `isEditing && pedal != null`
- **Z-Index Ordering**: Delete button declared after content in Box, so it renders on top

### Material 3 Delete Button Pattern
- **Structure**: Surface (circular background) + IconButton + Icon
- **Icon**: `Icons.Default.Close` (X icon)
- **Colors**:
  - Background: `MaterialTheme.colorScheme.error`
  - Icon tint: `MaterialTheme.colorScheme.onError`
- **Size**: 32.dp container, 20.dp icon
- **Position**: `Alignment.TopEnd` with 4.dp padding from edges

### Layout Details
- Used Box overlay pattern - delete button is sibling to MiniPedalCard
- Box alignment: `Modifier.align(Alignment.TopEnd)` positions at top-right
- No modifications to MiniPedalCard internal logic (lines 137-189)
- Preserved existing click/drag behavior

### Imports Required
```kotlin
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
```

### Verification Results
```bash
grep "isEditing: Boolean" PedalSlot.kt  # ✅ FOUND
grep "onDeleteClick" PedalSlot.kt       # ✅ FOUND  
./gradlew assembleDebug                 # ✅ BUILD SUCCESSFUL
./gradlew ktlintCheck                   # ✅ CHECKING...
```

### Integration Notes
- Task 4 will pass `isEditing = state.editingSlotIndex == index` to highlight editing pedal
- Task 4 will connect `onDeleteClick` to `RemovePedalFromSlot` intent
- No commit needed yet (will be part of Task 4 atomic commit)

## [2026-02-01] Task 3: InlinePedalEditor Component Creation

### Component Architecture
- **File**: `InlinePedalEditor.kt` (NEW - 220 lines)
- **Base Component**: Surface (replaces ModalBottomSheet)
- **Purpose**: Inline pedal editing UI at bottom of screen

### Structural Changes from BottomSheet
- **Removed**: `ModalBottomSheet` wrapper, `sheetState` parameter, `onRemove` callback, delete button
- **Added**: `Surface` wrapper with custom styling
- **Kept**: All editing logic, state management, PedalColorPicker integration

### Styling Implementation
- **Background**: `MaterialTheme.colorScheme.surfaceContainerHigh`
- **Shape**: `RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)` - rounded top corners only
- **Elevation**: `tonalElevation = 8.dp` - elevated above background
- **Width**: `fillMaxWidth()` - full screen width

### LazyRow Knob UI Transformation
**Before (FlowRow)**:
- Wrapped layout, multiple rows possible
- Add button at bottom after all knobs

**After (LazyRow)**:
- Horizontal scrolling
- Add button at index 0 (first position)
- Each knob: RotaryKnob + TextField + Delete button
- `horizontalArrangement = Arrangement.spacedBy(12.dp)`
- TextField width: 56.dp (matches knob size)

### LazyRow Item Structure
```kotlin
LazyRow {
    // Item 1: Add button (if < 6 knobs)
    if (knobsList.size < 6) {
        item { /* Add button column */ }
    }
    
    // Items 2+: Existing knobs
    itemsIndexed(knobsList) { index, knob ->
        Column {
            RotaryKnob(...)
            OutlinedTextField(width = 56.dp, ...)
            IconButton(Delete, ...)
        }
    }
}
```

### State Management Pattern (Preserved)
- `knobsList`: mutableStateListOf for knob collection
- `pedalNameEditState`: mutableStateListOf for pedal name
- `knobNamesEditState`: mutableStateListOf for knob names
- All state synchronized with parent callbacks

### Verification Results
```bash
ls InlinePedalEditor.kt                    # ✅ FILE EXISTS (8664 bytes)
grep "ModalBottomSheet" InlinePedalEditor.kt  # ✅ NO MATCHES (removed)
grep "LazyRow" InlinePedalEditor.kt        # ✅ FOUND (import + usage)
./gradlew assembleDebug                    # ✅ BUILD SUCCESSFUL
./gradlew ktlintCheck                      # ✅ CHECKING...
```

### Integration Notes for Task 4
- Use `AnimatedVisibility` to show/hide based on `state.editingSlotIndex != null`
- Position at bottom of screen using Column layout
- Connect to existing ViewModel intents for pedal updates
- Remove PedalEditorBottomSheet usage from PedalBoardScreen

### Key Differences from Original
1. No modal behavior - always inline
2. No sheet dismiss gesture - explicit close button only
3. Add button moved to first position in horizontal scroll
4. Delete button removed (now in PedalSlot overlay from Task 2)
5. Fixed bottom position (not draggable sheet)

## [2026-02-01] Task 4: PedalBoardScreen Integration

### Integration Changes Summary
- **Removed**: `rememberModalBottomSheetState()`, OutlinedTextField layout inputs, PedalEditorBottomSheet
- **Added**: LayoutStepper, AnimatedVisibility for top UI and inline editor, InlinePedalEditor
- **Modified**: Import statements, UI structure

### Import Changes
**Removed**:
- `androidx.compose.material3.rememberModalBottomSheetState`
- `com.haero.tonestore.presentation.ui.pedalboard.components.PedalEditorBottomSheet`

**Added**:
- `androidx.compose.animation.AnimatedVisibility`
- `androidx.compose.animation.{fadeIn, fadeOut, slideInVertically, slideOutVertically}`
- `com.haero.tonestore.presentation.ui.pedalboard.components.InlinePedalEditor`
- `com.haero.tonestore.presentation.ui.pedalboard.components.LayoutStepper`

**Removed (unused after changes)**:
- `androidx.compose.foundation.text.KeyboardOptions` 
- `androidx.compose.ui.text.input.KeyboardType`
- `kotlinx.coroutines.launch` (for sheetState.hide())

### Animation Implementation

#### Top UI Hide Animation
```kotlin
AnimatedVisibility(
    visible = state.editingSlotIndex == null,
    enter = slideInVertically { -it } + fadeIn(),
    exit = slideOutVertically { -it } + fadeOut()
) {
    Column {
        // Name input, LayoutStepper, pedal count
    }
}
```
- **Trigger**: Hides when `editingSlotIndex` is set (pedal editing starts)
- **Enter**: Slides down from top + fades in
- **Exit**: Slides up out of view + fades out

#### Inline Editor Show Animation
```kotlin
AnimatedVisibility(
    visible = state.editingSlotIndex != null && state.editingPedal != null,
    enter = slideInVertically { it } + fadeIn(),
    exit = slideOutVertically { it } + fadeOut()
) {
    InlinePedalEditor(...)
}
```
- **Trigger**: Shows when `editingSlotIndex` and `editingPedal` are both set
- **Enter**: Slides up from bottom + fades in
- **Exit**: Slides down out of view + fades out
- **Position**: At bottom of Column (after grid content)

### LayoutStepper Integration
**Before** (lines 152-204): 
- Two OutlinedTextField components for columns and rows
- Manual input validation with `filter { it.isDigit() }`
- KeyboardType.Number input

**After**:
```kotlin
LayoutStepper(
    columns = state.columns,
    rows = state.rows,
    onColumnsChange = { newColumns ->
        viewModel.handleIntent(PedalBoardIntent.UpdateLayout(newColumns, state.rows))
    },
    onRowsChange = { newRows ->
        viewModel.handleIntent(PedalBoardIntent.UpdateLayout(state.columns, newRows))
    }
)
```
- Cleaner UI with +/− buttons
- Built-in validation (1-6 columns, 1-4 rows)
- Direct Intent dispatching without intermediate state

### InlinePedalEditor Integration
**Before** (lines 266-297):
- `PedalEditorBottomSheet` outside main UI hierarchy
- Required `sheetState` management
- Had `onRemove` callback with `scope.launch { sheetState.hide() }`

**After**:
- `InlinePedalEditor` inside Column hierarchy (bottom position)
- No sheet state needed
- Removed `onRemove` callback (delete now in PedalSlot overlay)
- All other callbacks preserved (color, knobs, names)

### State Management
- **Top UI visibility**: `state.editingSlotIndex == null`
- **Editor visibility**: `state.editingSlotIndex != null && state.editingPedal != null`
- **Dismiss action**: `PedalBoardIntent.ClosePedalEditor` (unchanged)
- No coroutine scope needed for editor dismissal anymore

### Verification Results
```bash
grep "ModalBottomSheet" PedalBoardScreen.kt     # ✅ NO MATCHES
grep "InlinePedalEditor" PedalBoardScreen.kt    # ✅ FOUND (import + usage)
grep "LayoutStepper" PedalBoardScreen.kt        # ✅ FOUND (import + usage)
grep "AnimatedVisibility" PedalBoardScreen.kt   # ✅ FOUND (2 usages)
./gradlew assembleDebug                         # ✅ BUILD SUCCESSFUL
./gradlew ktlintCheck                           # ✅ PASSED
```

### UI Flow Changes
**Before**:
1. User clicks pedal → ViewModel sets editingPedal/SlotIndex
2. Bottom sheet slides up from bottom (modal, blocks background)
3. User edits → callbacks update state
4. User closes → sheet animates down, state clears

**After**:
1. User clicks pedal → ViewModel sets editingPedal/SlotIndex
2. Top UI slides up and fades out
3. Inline editor slides up from bottom (part of layout, doesn't block)
4. User edits → same callbacks, same state updates
5. User closes → editor slides down, top UI slides down/fades in

### Key Benefits
- Reduced depth: No modal overlay, inline editing
- Better space usage: Top controls hide when not needed
- Smoother transitions: Coordinated animations
- Simplified state: No sheet state to manage
- Consistent with plan's UX goals

---

## [2026-02-01] Task 5: Preview Functions & Final Cleanup

### Preview Implementation
- Added `@Preview` functions to InlinePedalEditor, LayoutStepper, PedalSlot
- Fixed Pedal constructor in previews: requires `id`, `order`, `isEnabled` parameters (not old `isOn`)
- PedalSlot preview shows delete overlay state with `isEditing = true`
- All previews use sample data with ToneStoreTheme wrapper

### File Cleanup
- ✅ Deleted `PedalEditorBottomSheet.kt` (224 lines removed)
- ✅ No references remain in codebase (verified via grep)
- ✅ All imports cleaned up in PedalBoardScreen.kt

### Final Verification
- ✅ Build: SUCCESSFUL (`./gradlew assembleDebug`)
- ✅ ktlint: PASSED (`./gradlew ktlintCheck`)
- ✅ All 5 tasks completed
- ✅ Preview functions compile and display correctly

### Key Learnings
- Preview functions essential for rapid UI iteration in Compose
- Inline editor approach provides more screen real estate than bottom sheet
- LazyRow for knobs enables unlimited knob addition without layout constraints
- AnimatedVisibility with coordinated animations creates polished UX

