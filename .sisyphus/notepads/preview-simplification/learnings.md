# Learnings - Preview Simplification

## Project Conventions
- Kotlin + Jetpack Compose project
- Package: `com.haero.tonestore.presentation.ui.pedalboard.components`
- internal visibility allows same-package access
- Compose parameters use default values for backwards compatibility

## Patterns Observed
- MiniPedalCard is internal in PedalSlot.kt (lines 133-184)
- ExpressionPedalZone hardcodes 80dp × 200dp (lines 40-41)
- Delete button (IconButton) at lines 100-109

## PedalBoardPreview Implementation (Task 2)
- Successfully created PedalBoardPreview.kt reusing MiniPedalCard (internal) from PedalSlot.kt
- ExpressionPedalZone parameters working correctly: isEditable=false hides delete button
- Dynamic height calculation: `(slotHeight + verticalSpacing) * displayRows - verticalSpacing`
- Width set to 40.dp for compact preview expression pedal
- maxRows=2 clipping logic: `val displayRows = minOf(rows, maxRows)`
- All 5 preview functions copied from MiniPedalBoardPreview.kt work without modification
- Build verification: gradle assembleDebug passes successfully
- Git commit: "feat(pedalboard): create PedalBoardPreview using original components"

## Task 4 Completion: File Deletion (2026-02-01)
- Successfully deleted MiniPedalBoardPreview.kt and MiniExpressionPedalZone.kt
- MiniPedalCard in PedalSlot.kt (line 133) confirmed still exists
- No references to deleted components found in codebase
- Clean build: ./gradlew clean assembleDebug → BUILD SUCCESSFUL
- Lint check: ./gradlew ktlintCheck → BUILD SUCCESSFUL
- Git commit: "refactor(pedalboard): remove redundant Mini* preview components" (26a7435)
- Simplification complete: codebase now uses original components with parameters instead of Mini* variants
