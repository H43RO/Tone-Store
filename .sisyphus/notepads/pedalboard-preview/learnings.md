# Learnings - pedalboard-preview

## Conventions & Patterns

(Subagents will append findings here)

## Task 1: MiniPedalCard Visibility Change

**Change:** Converted `MiniPedalCard` from `private` to `internal` visibility (line 133)
- **File:** `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt`
- **Reason:** Enable reuse in preview components (MiniExpressionPedalZone, MiniPedalBoardPreview)
- **Scope:** Only visibility modifier changed; function signature, annotations, and body untouched
- **Verification:** 
  - Build successful (`./gradlew assembleDebug`) - 8s
  - Lint clean (`./gradlew ktlintCheck`) - 13s
  - No other functions affected; MiniKnobIndicator remains private
- **Commit:** `refactor(pedalboard): make MiniPedalCard internal for preview reuse` (a1d8187)

**Key insight:** Kotlin's `internal` visibility is module-scoped, perfect for cross-component reuse within the same module. This allows MiniPedalCard to be imported by downstream components without exposing it outside the pedalboard module.

## Task 5: Final Build Verification

**Timestamp:** 2026-01-31 23:58 KST

**Commands Executed:**
1. `./gradlew clean assembleDebug` → BUILD SUCCESSFUL in 53s
2. `./gradlew ktlintCheck` → BUILD SUCCESSFUL in 14s  
3. File verification → Both new files exist

**Verification Results:**
- ✅ Build passed with 0 errors (2 warnings - unrelated to this feature)
- ✅ Lint passed with 0 errors
- ✅ `MiniPedalBoardPreview.kt` exists (5854 bytes)
- ✅ `MiniExpressionPedalZone.kt` exists (3693 bytes)
- ✅ `PedalBoardListScreen.kt` updated with preview integration (lines 293-299)

**Feature Complete:**
All 5 tasks completed successfully:
1. ✅ MiniPedalCard visibility changed to internal
2. ✅ MiniExpressionPedalZone created (24dp×60dp scaled component)
3. ✅ MiniPedalBoardPreview created (Row/Column layout with 40dp slots)
4. ✅ PedalBoardCard updated with Column layout and preview at top
5. ✅ Final verification passed

**Definition of Done Checklist:**
- ✅ `./gradlew assembleDebug` → BUILD SUCCESSFUL
- ✅ `./gradlew ktlintCheck` → No errors
- ✅ All Preview composables present (5 in MiniPedalBoardPreview, 2 in MiniExpressionPedalZone)
- ✅ Preview shows pedal positions and colors (via MiniPedalCard reuse)
- ✅ Preview shows expression pedal when present (MiniExpressionPedalZone integration)
- ✅ Empty slots invisible but preserve grid positions (Spacer with transparent background)

**Must Have Verification:**
- ✅ Visual preview showing actual pedal arrangement from SavedPedalBoard.slots
- ✅ Pedal colors visible (Color.color from Pedal model)
- ✅ Expression pedal zone when expressionPedal != null
- ✅ Fixed max-width preview with aspect ratio preservation (Row/Column layout)
- ✅ Non-interactive preview (no click handlers, no drag gestures)

**Must NOT Have Verification:**
- ✅ NO click handlers in preview components
- ✅ NO animations in preview
- ✅ NO modifications to PedalBoardGrid.kt
- ✅ NO modifications to ExpressionPedalZone.kt
- ✅ NO changes to card info section styling (icon, text, delete button preserved)
- ✅ NO use of LazyVerticalGrid (used Row/Column)
- ✅ NO knob value display in preview
- ✅ NO drag gesture handling

**Key Technical Decisions Validated:**
- Scale factor: 40dp slot height (~28% of 140dp) → appropriate thumbnail size
- Layout: Row/Column instead of LazyVerticalGrid → lightweight, performant for static display
- Empty slots: Spacer with same dimensions but transparent → grid positions preserved
- Component reuse: MiniPedalCard made internal → no code duplication
- Expression pedal: Separate scaled component (MiniExpressionPedalZone) → consistent with original design

**Manual QA Remaining (User-side):**
- Visual inspection of Preview composables in Android Studio Preview pane
- Runtime testing on device/emulator to see actual cards with previews
- Visual verification that pedalboard shapes match their actual layouts

**Session Summary:**
- 4 commits across Tasks 1-4 (no commit for Task 5)
- All builds passed throughout session
- Git pre-commit hook ran ktlintFormat automatically
- Orchestrator handled tasks directly (delegation was experiencing failures)
