# InlinePedalEditor AnimatedVisibility 적용

## TL;DR

> **Quick Summary**: InlinePedalEditor에 AnimatedVisibility 애니메이션 적용, 안전한 null 처리로 크래시 방지
> 
> **Deliverables**:
> - PedalBoardScreen.kt에 AnimatedVisibility + remember 캐싱 패턴 적용
> - `!!` 단언문 제거, null-safe 코드로 변경
> 
> **Estimated Effort**: Quick
> **Parallel Execution**: NO - single task
> **Critical Path**: Task 1 (단일 작업)

---

## Context

### Original Request
PedalBoardScreen에서 InlinePedalEditor에 AnimatedVisibility를 적용해줄 수 있어? 다만, assert(!!) 구문을 사용하면 오류가 날 것 같아. 조금 더 안전한 방향으로 보완해서, 애니메이션을 완벽히 구현해줘.

### Technical Challenge
**문제**: `AnimatedVisibility`의 `visible`이 `false`가 되면 애니메이션이 종료되는 동안에도 content가 계속 렌더링됨. 이때 `state.editingPedal!!`이 이미 null이 되어 NPE 발생.

**해결**: `remember`로 마지막 유효 값을 캐시하여 애니메이션 종료까지 안전하게 사용.

### Pattern Reference
```kotlin
// 안전한 AnimatedVisibility 패턴
val isEditing = state.editingSlotIndex != null && state.editingPedal != null

// 마지막 유효 값 캐싱 - 애니메이션 종료까지 이 값 사용
val cachedPedal = remember(state.editingPedal) { 
    state.editingPedal 
} ?: return@someScope // 또는 초기값
val cachedSlotIndex = remember(state.editingSlotIndex) { 
    state.editingSlotIndex 
} ?: return@someScope

AnimatedVisibility(
    visible = isEditing,
    enter = expandVertically() + fadeIn(),
    exit = shrinkVertically() + fadeOut()
) {
    // cachedPedal, cachedSlotIndex 사용 (null-safe)
    InlinePedalEditor(
        pedal = cachedPedal,
        slotIndex = cachedSlotIndex,
        ...
    )
}
```

---

## Work Objectives

### Core Objective
InlinePedalEditor에 부드러운 등장/퇴장 애니메이션 적용, NPE 없이 안전하게 동작

### Concrete Deliverables
- `PedalBoardScreen.kt`: AnimatedVisibility + remember 캐싱 패턴 적용

### Definition of Done
- [x] `./gradlew assembleDebug` → BUILD SUCCESSFUL
- [x] `!!` 단언문 제거됨 (InlinePedalEditor 호출부)
- [x] AnimatedVisibility로 애니메이션 적용됨
- [x] remember로 값 캐싱하여 애니메이션 종료 시 NPE 방지

### Must Have
- AnimatedVisibility 적용 (expandVertically/shrinkVertically 또는 fadeIn/fadeOut)
- remember로 editingPedal/editingSlotIndex 캐싱
- `!!` 단언문 완전 제거 (InlinePedalEditor 관련)

### Must NOT Have (Guardrails)
- ❌ PedalboardInfoEditor 수정 금지
- ❌ ViewModel/Intent/State 수정 금지
- ❌ 다른 컴포넌트 파일 수정 금지
- ❌ 콜백 로직 변경 금지 (UI wrapper만 변경)

---

## Verification Strategy

### Test Decision
- **Infrastructure exists**: YES
- **User wants tests**: NO (빌드 검증 + 수동 확인)
- **Framework**: N/A

### Automated Verification
```bash
# 빌드 검증
./gradlew assembleDebug
# Assert: BUILD SUCCESSFUL

# !! 단언문 제거 확인 (InlinePedalEditor 호출부)
grep -n "editingPedal!!" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
# Assert: 0 matches (또는 다른 부분만 있음)

# AnimatedVisibility 적용 확인
grep -c "AnimatedVisibility" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
# Assert: >= 1
```

### Manual QA (권장)
- 앱 실행 후 페달 슬롯 클릭 → InlinePedalEditor가 부드럽게 나타남
- 닫기 버튼 클릭 → InlinePedalEditor가 부드럽게 사라짐
- 빠르게 열기/닫기 반복 → 크래시 없음

---

## TODOs

- [x] 1. PedalBoardScreen: InlinePedalEditor에 AnimatedVisibility 적용

  **What to do**:
  1. import 추가: `expandVertically`, `shrinkVertically`, `fadeIn`, `fadeOut`
  2. `remember`로 마지막 유효 `editingPedal`/`editingSlotIndex` 캐싱
  3. `AnimatedVisibility` wrapper 추가
  4. 내부에서 캐시된 값 사용 (null-safe)
  5. 콜백에서도 캐시된 slotIndex 사용

  **Must NOT do**:
  - PedalboardInfoEditor 수정 금지
  - 콜백 로직 변경 금지
  - ViewModel Intent 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: [`frontend-ui-ux`]

  **References**:

  **Pattern References**:
  - `PedalBoardScreen.kt:187-211` - 현재 InlinePedalEditor 호출부 (수정 대상)

  **현재 코드 (lines 187-226)**:
  ```kotlin
  InlinePedalEditor(
      pedal = state.editingPedal!!,
      slotIndex = state.editingSlotIndex!!,
      onDismiss = { viewModel.handleIntent(PedalBoardIntent.ClosePedalEditor) },
      onColorChange = { color ->
          viewModel.handleIntent(
              PedalBoardIntent.UpdatePedalColor(state.editingSlotIndex!!, color)
          )
      },
      onKnobsChange = { knobs ->
          viewModel.handleIntent(
              PedalBoardIntent.UpdatePedalKnobs(state.editingSlotIndex!!, knobs)
          )
      },
      onPedalNameChange = { name ->
          viewModel.handleIntent(
              PedalBoardIntent.UpdatePedalName(state.editingSlotIndex!!, name)
          )
      },
      onKnobNameChange = { knobIndex, name ->
          viewModel.handleIntent(
              PedalBoardIntent.UpdateKnobName(state.editingSlotIndex!!, knobIndex, name)
          )
      }
  )
  PedalboardInfoEditor(...)
  ```

  **변경 후 코드**:
  ```kotlin
  // 마지막 유효 값 캐싱 - AnimatedVisibility 종료 애니메이션 동안 사용
  val lastEditingPedal = remember { mutableStateOf<Pedal?>(null) }
  val lastEditingSlotIndex = remember { mutableStateOf<Int?>(null) }
  
  // 유효한 값이 있을 때 캐시 업데이트
  LaunchedEffect(state.editingPedal, state.editingSlotIndex) {
      if (state.editingPedal != null && state.editingSlotIndex != null) {
          lastEditingPedal.value = state.editingPedal
          lastEditingSlotIndex.value = state.editingSlotIndex
      }
  }
  
  val isEditingPedal = state.editingSlotIndex != null && state.editingPedal != null
  
  AnimatedVisibility(
      visible = isEditingPedal,
      enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
      exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
  ) {
      // 캐시된 값 사용 - null이면 렌더링하지 않음
      val pedal = lastEditingPedal.value ?: return@AnimatedVisibility
      val slotIndex = lastEditingSlotIndex.value ?: return@AnimatedVisibility
      
      InlinePedalEditor(
          pedal = pedal,
          slotIndex = slotIndex,
          onDismiss = { viewModel.handleIntent(PedalBoardIntent.ClosePedalEditor) },
          onColorChange = { color ->
              viewModel.handleIntent(PedalBoardIntent.UpdatePedalColor(slotIndex, color))
          },
          onKnobsChange = { knobs ->
              viewModel.handleIntent(PedalBoardIntent.UpdatePedalKnobs(slotIndex, knobs))
          },
          onPedalNameChange = { name ->
              viewModel.handleIntent(PedalBoardIntent.UpdatePedalName(slotIndex, name))
          },
          onKnobNameChange = { knobIndex, name ->
              viewModel.handleIntent(PedalBoardIntent.UpdateKnobName(slotIndex, knobIndex, name))
          }
      )
  }
  
  PedalboardInfoEditor(...)
  ```

  **Import 추가**:
  ```kotlin
  import androidx.compose.animation.expandVertically
  import androidx.compose.animation.shrinkVertically
  import androidx.compose.animation.fadeIn
  import androidx.compose.animation.fadeOut
  import androidx.compose.runtime.mutableStateOf
  // mutableStateOf는 이미 import되어 있음 (line 40)
  ```

  **Acceptance Criteria**:
  ```bash
  # 빌드 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL

  # !! 단언문 제거 확인
  grep -c "editingPedal!!" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 0

  grep -c "editingSlotIndex!!" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 0 (InlinePedalEditor 부분만, 다른 다이얼로그는 OK)

  # AnimatedVisibility 적용 확인
  grep "AnimatedVisibility" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 발견됨
  ```

  **Commit**: YES
  - Message: `feat(pedalboard): InlinePedalEditor에 AnimatedVisibility 애니메이션 적용`
  - Files: `PedalBoardScreen.kt`

---

## Commit Strategy

| After Task | Message | Files |
|------------|---------|-------|
| 1 | `feat(pedalboard): InlinePedalEditor에 AnimatedVisibility 애니메이션 적용` | PedalBoardScreen.kt |

---

## Success Criteria

### Verification Commands
```bash
./gradlew assembleDebug    # Expected: BUILD SUCCESSFUL
```

### Final Checklist
- [x] AnimatedVisibility 적용됨
- [x] remember 캐싱으로 NPE 방지됨
- [x] `!!` 단언문 제거됨
- [x] 부드러운 등장/퇴장 애니메이션 동작
