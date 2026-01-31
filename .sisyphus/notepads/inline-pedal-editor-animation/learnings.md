# AnimatedVisibility Implementation Learnings

## Pattern: Safe Null Handling During Exit Animation

### Problem
When using `AnimatedVisibility` with compose state that can be null, the exit animation will fail with NPE if the state is nullified while the animation is running. This happens because:
- `AnimatedVisibility` keeps rendering content during exit animation
- If `state.editingPedal!!` or `state.editingSlotIndex!!` becomes null, the composable crashes mid-animation
- The animation doesn't have time to complete smoothly

### Solution: Cached Values Pattern
```kotlin
// 1. Create mutable states to cache the last valid values
val lastEditingPedal = remember { mutableStateOf<Pedal?>(null) }
val lastEditingSlotIndex = remember { mutableStateOf<Int?>(null) }

// 2. Update cache ONLY when both values are non-null
LaunchedEffect(state.editingPedal, state.editingSlotIndex) {
    if (state.editingPedal != null && state.editingSlotIndex != null) {
        lastEditingPedal.value = state.editingPedal
        lastEditingSlotIndex.value = state.editingSlotIndex
    }
}

// 3. Define visibility condition
val isEditingPedal = state.editingSlotIndex != null && state.editingPedal != null

// 4. Wrap with AnimatedVisibility
AnimatedVisibility(
    visible = isEditingPedal,
    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
) {
    // 5. Extract cached values with safe early return
    val pedal = lastEditingPedal.value ?: return@AnimatedVisibility
    val slotIndex = lastEditingSlotIndex.value ?: return@AnimatedVisibility
    
    // Use cached values (no !! assertions)
    InlinePedalEditor(pedal = pedal, slotIndex = slotIndex, ...)
}
```

### Key Points
- `remember { mutableStateOf<T?>(null) }` survives recomposition and keeps last valid value
- `LaunchedEffect` with null-check guard ensures cache only updates with valid data
- Cached values persist during animation lifecycle, preventing NPE on state transitions
- Early return `?: return@AnimatedVisibility` provides safe null handling in lambda scope
- Callbacks use cached variables (`slotIndex`) instead of `state.editingSlotIndex!!`

### Animation Behavior
- **Enter**: `expandVertically(expandFrom = Top) + fadeIn()` → grows from top, fades in
- **Exit**: `shrinkVertically(shrinkTowards = Top) + fadeOut()` → shrinks to top, fades out
- Smooth synchronized combined animation (expand+fade happen together)

## Code Quality Improvements
- Removed all `!!` assertions from InlinePedalEditor section
- No runtime crashes during animation state transitions
- Clean separation: visibility logic → cached values → safe usage

## Build Status
✓ ./gradlew assembleDebug → BUILD SUCCESSFUL
✓ AnimatedVisibility wrapper applied
✓ No !! found in InlinePedalEditor section
