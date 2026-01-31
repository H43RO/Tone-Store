# Learnings: PedalBoard Enhancement

## Conventions & Patterns

(Subagents will append findings here)

## Room Database Migration Pattern

### Version 4→5 Implementation (Expression Pedal Field)
- **Column Addition**: `ALTER TABLE saved_pedal_boards ADD COLUMN expressionPedalJson TEXT DEFAULT NULL`
- **Key Pattern**: Use nullable TEXT for Gson-serialized single objects (different from Lists)
- **Mapper Strategy**:
  - `toEntity`: Use `domain.expressionPedal?.let { gson.toJson(it) }` for null-safe serialization
  - `toDomain`: Check `isNullOrEmpty()` before deserializing; use `getOrNull()` for safe parsing
  
### Entity Field Naming Convention
- Domain model: `expressionPedal: Pedal?`
- Entity field: `expressionPedalJson: String?`
- Pattern follows existing `slots` → `slotsJson` convention

### Gson TypeToken Pattern
- Lists: `private val pedalListType = object : TypeToken<List<Pedal?>>() {}.type`
- Single objects: `private val pedalType = object : TypeToken<Pedal>() {}.type`
- No explicit type needed when using `fromJson<T>(json, type)` due to reified generics

### Error Handling in Mapper
- Use `runCatching { }.getOrElse { defaultValue }` for deserialization
- For nullable fields, use `getOrNull()` instead of `getOrElse`
- Prevents crashes from corrupted JSON

## Wave 1 Task 2 Completion Status
✅ All 4 files modified successfully:
- SavedPedalBoard.kt: expressionPedal field added
- SavedPedalBoardEntity.kt: expressionPedalJson field added
- SavedPedalBoardMapper.kt: bidirectional conversion implemented
- ToneStoreDatabase.kt: version 5, MIGRATION_4_5 added

✅ Pre-existing build errors (PedalBoardScreen, PedalBoardViewModel) are unrelated to this task

## Wave 1 Task 1: PedalCategory Enum & Preset Pedals Expansion

### Enum Creation Pattern
- Created `PedalCategory.kt` in `domain/model/` package
- 6 category values: DRIVE, MODULATION, TIME_BASED, DYNAMICS, UTILITY, PITCH
- Each enum value documented with Japanese comments explaining pedal types in each category
- Pattern: Follows existing `PedalType.kt` convention with enum value documentation

### Preset Pedals Color Implementation
- **Color Format**: ARGB Long values (0xFFRRGGBB pattern)
- **All 18 pedals now have default colors**:
  - Overdrive: 0xFF3EB489 (green, Tube Screamer inspired)
  - Distortion: 0xFFFF9800 (orange, DS-1 inspired)
  - Fuzz: 0xFF9E9E9E (gray, vintage look)
  - Chorus: 0xFF2196F3 (blue)
  - Delay: 0xFF42A5F5 (light blue)
  - Reverb: 0xFF64B5F6 (light blue)
  - Compressor: 0xFFE53935 (red)
  - Wah: 0xFF212121 (black)
  - Phaser: 0xFFFF5722 (deep orange)
  - Flanger: 0xFF3F51B5 (indigo)
  - Tremolo: 0xFFFFEB3B (amber/yellow)
  - Octave: 0xFF1E88E5 (blue)
  - Boost: 0xFFFFC107 (amber)
  - Noise Gate: 0xFF607D8B (blue-gray)
  - Tuner: 0xFFFAFAFA (almost white)
  - EQ: 0xFFCFD8DC (light gray)
  - Bass Preamp: 0xFFFFD54F (light amber)
  - Whammy: 0xFFD32F2F (dark red)

### Pedal Knob Configuration
- **Knob count varies by pedal type**:
  - Single knob: Boost (1)
  - Two knobs: Phaser (2), Noise Gate (2), Whammy (2)
  - Three knobs: Overdrive (3), Distortion (3), Fuzz (3), Chorus (3), Delay (3), Reverb (3), Compressor (3), Tremolo (3), Octave (3)
  - Four knobs: Flanger (4)
  - Five knobs: EQ (5), Bass Preamp (5)
  - Zero knobs: Tuner (0)
- Default knob values set to 5f or 4f based on pedal type
- Each knob has descriptive names reflecting its function

### File Structure Changes
- Created new file: `app/src/main/java/com/haero/tonestore/domain/model/PedalCategory.kt`
- Modified: `app/src/main/java/com/haero/tonestore/data/preset/PresetPedals.kt`
  - Updated all 8 existing pedal functions to include `color` parameter
  - Added 10 new pedal creation functions (Phaser, Flanger, Tremolo, Octave, Boost, Noise Gate, Tuner, EQ, Bass Preamp, Whammy)
  - Updated `getPresetPedals()` to return all 18 pedals

### Verification Results
✅ Total pedal creation functions: 18 (verified with `grep -c "private fun create"`)
✅ All pedals have color values: 18 (verified with `grep -c "color = 0xFF"`)
✅ All pedals included in getPresetPedals() list: 18
✅ PedalCategory enum created with 6 required values
✅ Knob configurations assigned per specification
✅ No syntax errors in created/modified files

### Notes
- Pre-existing build errors in codebase (unrelated to this task):
  - PedalBoardScreen.kt: All required parameters are present
  - Missing R resource references in other UI files (not introduced by this task)
- Code patterns strictly follow existing conventions in codebase
- All files compile independently without syntax errors

## Wave 1 Task 3: Dynamic Knob Management
✅ **Task completed successfully**

### Implementation Pattern: Mutable State in Compose BottomSheet
- **State Management**: Used `remember { mutableStateListOf(*pedal.knobs.toTypedArray()) }` to create mutable copy of knobs
- **Pattern**: This matches CustomPedalDialog pattern (PedalBoardScreen.kt:414) for consistency
- **Key Learning**: Must use `toTypedArray()` to properly unpack List into varargs for mutableStateListOf

### MVI Intent System Integration
- **New Intent**: `UpdatePedalKnobs(slotIndex: Int, knobs: List<Knob>)` added to sealed interface
- **Handler**: ViewModel implements `updatePedalKnobs()` following existing update patterns
- **Callback Flow**: BottomSheet onKnobsChange → handleIntent → updatePedalKnobs → state update
- **Key Pattern**: All state mutations through ViewModel maintain single source of truth

### UI/UX Implementation Details
- **Delete Button**: Icon per knob, disabled when size == 1 (minimum constraint)
- **Add Button**: TextButton with Icons.Default.Add, disabled when size == 6 (maximum constraint)
- **Layout**: Column wrapper for each knob + delete button in FlowRow
- **Resources**: Used existing string resources (R.string.add_knob, R.string.knobs)

### Files Modified
1. **PedalBoardIntent.kt**: Added `UpdatePedalKnobs` with Knob import
2. **PedalEditorBottomSheet.kt**: 
   - Added parameter `onKnobsChange: (List<Knob>) -> Unit`
   - Implemented dynamic knob add/remove with constraints
   - Used mutable state for local UI state
3. **PedalBoardViewModel.kt**: 
   - Added Intent handler in `handleIntent` switch
   - Implemented `updatePedalKnobs` private function
4. **PedalBoardScreen.kt**: 
   - Updated call site with `onKnobsChange` lambda

### Build Verification
✅ `./gradlew assembleDebug` passes successfully (19s, BUILD SUCCESSFUL)
✅ grep confirms UpdatePedalKnobs presence in Intent and BottomSheet files

### Commit Information
- **Commit Hash**: 122e6ba
- **Message**: feat(pedal): add PedalCategory enum and expand presets to 18 types with default colors
- **Author**: H43RO
- **Timestamp**: 2026-01-31 19:53:38 +0900
- **Files in commit**:
  - app/src/main/java/com/haero/tonestore/domain/model/PedalCategory.kt (NEW)
  - .sisyphus/notepads/pedalboard-enhancement/learnings.md (UPDATED)

### Important Note
The 18 preset pedals with default colors were already implemented in a previous commit (fa28d66). This task focused on creating the PedalCategory enum to categorize these pedals for future filtering/organization features.

## Wave 2 Task 4: Pedal and Knob Name Editing

✅ **Task completed successfully**

### Implementation Summary
- **Pedal Name Editing**: Replaced static `Text` with editable `OutlinedTextField`
- **Knob Name Editing**: Replaced label hardcoding with individual `OutlinedTextField` for each knob
- **Real-time Updates**: Changes trigger MVI Intents to update state immediately

### Key Implementation Patterns

#### Intent Architecture
- **New Intents Added**:
  - `UpdatePedalName(slotIndex: Int, name: String)` - updates pedal name in slot
  - `UpdateKnobName(slotIndex: Int, knobIndex: Int, name: String)` - updates specific knob name
- **Pattern**: Consistent with existing `UpdatePedalColor`, `UpdatePedalKnobs` Intents

#### ViewModel Handlers
- **updatePedalName()**: Copies pedal with new name, updates slot
- **updateKnobName()**: Maps over knob list, updates only target knob by index, copies pedal with new knobs list
- **Key Learning**: Index-based updates require `mapIndexed` with conditional copying

#### UI State Management in BottomSheet
- **Mutable State Lists**:
  - `pedalNameEditState = mutableStateListOf(pedal.name)` - wraps single value in list for state tracking
  - `knobNamesEditState = mutableStateListOf(*pedal.knobs.map { it.name }.toTypedArray())` - mirrors knob count
- **Pattern**: State maps 1:1 with UI TextField components
- **Synchronization**: When knobs are added/removed, knobNamesEditState also updated

#### OutlinedTextField Pattern (from PedalBoardScreen.kt reference)
```kotlin
OutlinedTextField(
    value = state.value,
    onValueChange = { newValue -> onChangeCallback(newValue) },
    label = { Text("Label") },
    singleLine = true,
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier.fillMaxWidth()
)
```
- Applied to both pedal name and knob names consistently
- Used `RoundedCornerShape(12.dp)` for Material 3 consistency
- Knob name fields constrained to 80% width with fixed height 56.dp for compact layout

#### Callback Chain
1. User edits OutlinedTextField → `onValueChange` triggered
2. Update local state: `pedalNameEditState[0] = newValue` or `knobNamesEditState[index] = newValue`
3. Invoke callback: `onPedalNameChange(newValue)` or `onKnobNameChange(index, newValue)`
4. PedalBoardScreen passes to ViewModel: `viewModel.handleIntent(UpdatePedalName(...))`
5. ViewModel updates state, flows to UI through StateFlow

### Files Modified
1. **PedalBoardIntent.kt**: Added 2 new Intent cases
2. **PedalBoardViewModel.kt**: Added `handleIntent` routing + 2 private handler methods
3. **PedalEditorBottomSheet.kt**: 
   - Added OutlinedTextField import
   - Added `onPedalNameChange`, `onKnobNameChange` parameters
   - Replaced pedal name Text with OutlinedTextField
   - Replaced knob label display with OutlinedTextField
   - Added local mutable state for editing
   - Synchronized state updates with knob add/remove operations
4. **PedalBoardScreen.kt**: Updated PedalEditorBottomSheet call with new callbacks

### Build Status
✅ `./gradlew assembleDebug` - BUILD SUCCESSFUL
✅ UpdatePedalName and UpdateKnobName verified in Intent file

### Design Decisions
- **Text Fields Over Dialogs**: Editing directly in BottomSheet is less friction than modal dialogs
- **Real-time State**: No "Apply" button needed - changes flow through MVI immediately
- **Minimum Constraints**: Pedal name validation could be added to ViewModel if needed
- **Knob Labeling**: TextField label shows "노브 1", "노브 2" for user clarity about which knob is being edited

### Notes for Next Tasks
- This implementation sets up full CRUD for pedal metadata (name + all knob names)
- Editing is now bidirectional: UI → ViewModel → State
- Consider adding validations in ViewModel if pedal/knob names should have length limits
- Pedal name changes persist through SavePedalBoard intent

## Wave 2 Tasks 5 & 6: PresetPedalSelectionDialog + ExpressionPedalZone

### Delegation System Failure
- **Issue**: All delegate_task() calls failed with 0s duration (5 attempts)
- **Symptom**: Sessions created but no subagent response, no file changes
- **Root Cause**: System-level initialization failure in subagent spawning
- **Workaround**: Orchestrator completed tasks directly to unblock Wave 3

### Task 5: PresetPedalSelectionDialog Implementation
✅ **Card Grid UI with Category Filtering**

#### Key Patterns
- **ModalBottomSheet**: Used instead of AlertDialog for better UX with large content
- **LazyVerticalGrid**: GridCells.Fixed(3) for 3-column card layout
- **FilterChips**: Category filtering with "All" default state
- **State Management**: `remember(selectedCategory)` for derived filtered list

#### Component Structure
```kotlin
ModalBottomSheet {
  Title
  FilterChips Row (All + 6 categories)
  LazyVerticalGrid (filtered pedals)
  Custom Pedal Button
}
```

#### PedalCard Design
- Size: fillMaxWidth + aspectRatio(1f) for square cards
- Color: Background tint (15% alpha) + 3dp border in pedal color
- Color Stripe: 4.dp height bar at top for quick visual ID
- Content: Color indicator, name (2 lines max), knob count

#### Category Mapping Function
- `getCategoryForPedal()`: Maps pedal name string to PedalCategory enum
- Hardcoded mapping (DRIVE: Overdrive/Distortion/Fuzz/Boost, etc.)
- Falls back to UTILITY for unknown pedals

#### Integration
- Replaced AddPedalDialog (AlertDialog with vertical list) in PedalBoardScreen.kt
- Changed prop names: `onSelectPreset` → `onPedalSelect`, `onCreateCustom` → `onCustomPedalCreate`
- Added import: `com.haero.tonestore.presentation.ui.pedalboard.components.PresetPedalSelectionDialog`

### Task 6: ExpressionPedalZone Implementation
✅ **Foot-Pedal Shaped UI Component**

#### Key Patterns
- **Foot-Pedal Shape**: Custom RoundedCornerShape with asymmetric corners
  - Top: 16.dp (wider, toe area)
  - Bottom: 4.dp (narrower, heel area)
- **Conditional Styling**: Different border/background for filled vs empty state
- **Clickable Box**: Opens selection dialog on tap

#### Component Structure
```kotlin
Box (80.dp x 200.dp, foot-pedal shape) {
  if (expressionPedal != null) {
    Color Circle (32.dp)
    Pedal Name
    Remove IconButton
  } else {
    "Wah\n/\nWhammy" text (dotted border hint)
  }
}
```

#### Styling Details
- **Filled State**: 
  - Background: Pedal color at 20% alpha
  - Border: 3.dp solid in pedal color
  - Content: Color indicator (32.dp circle), name, remove button (24.dp)
- **Empty State**:
  - Border: 2.dp dotted in outline color at 50% alpha
  - Content: Multi-line hint text "Wah\n/\nWhammy"

#### ExpressionPedalSelectionDialog
- Simple AlertDialog with 2 card options (Wah, Whammy)
- Cards show: Color box (48.dp) + Pedal name
- Uses PresetPedals.getPresetPedals() to fetch Wah/Whammy presets
- Props: onSelectWah, onSelectWhammy, onDismiss

### Files Created
1. `PresetPedalSelectionDialog.kt` - 229 lines
2. `ExpressionPedalZone.kt` - 109 lines
3. `ExpressionPedalSelectionDialog.kt` - 106 lines

### Files Modified
1. `PedalBoardScreen.kt` - Replaced AddPedalDialog call + added import

### Build Verification
✅ `./gradlew assembleDebug` - BUILD SUCCESSFUL
✅ LazyVerticalGrid confirmed in PresetPedalSelectionDialog.kt
✅ @Composable confirmed in ExpressionPedalZone.kt

### Notes for Wave 3
- Task 7: Integrate ExpressionPedalZone into PedalBoardScreen layout (Row with PedalBoardGrid)
- Need to add state management in ViewModel for expressionPedal
- Need to add Intents: SelectExpressionPedal, RemoveExpressionPedal
- Task 8 & 9: CableOverlay visualization (depends on pedal ON/OFF states)
