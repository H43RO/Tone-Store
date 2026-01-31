# 페달보드 편집 UI 버그 수정

## TL;DR

> **Quick Summary**: 인라인 편집기가 화면 하단에 고정되지 않고, 삭제 버튼이 표시되지 않는 버그 수정
> 
> **Deliverables**:
> - `PedalBoardScreen.kt` 수정 - 인라인 편집기를 Box 내 하단 고정으로 이동
> - `PedalBoardGrid.kt` 수정 - `editingSlotIndex`, `onDeletePedal` 파라미터 추가
> 
> **Estimated Effort**: Quick (~30분)
> **Parallel Execution**: NO - 순차적 수정 필요

---

## Context

### 원인 분석

**버그 1: 인라인 편집기가 안 뜸 / 하단에 고정되지 않음**
- 현재 상태: `AnimatedVisibility`가 `verticalScroll` Column 안에 위치
- 문제: 스크롤 가능한 영역 안에 있어서 화면 하단에 고정되지 않음
- 해결: `Scaffold` 바로 아래에 `Box`를 추가하고, `InlinePedalEditor`를 `Modifier.align(Alignment.BottomCenter)`로 하단 고정

**버그 2: 삭제 버튼이 표시되지 않음**
- 현재 상태: `PedalBoardGrid`에서 `PedalSlot`에 `isEditing` 파라미터를 전달하지 않음
- 문제: `isEditing`이 항상 기본값 `false`이므로 삭제 버튼이 표시되지 않음
- 해결: `PedalBoardGrid`에 `editingSlotIndex` 파라미터 추가하고 `PedalSlot`에 전달

---

## TODOs

- [x] 1. PedalBoardGrid에 editingSlotIndex 및 onDeletePedal 파라미터 추가

  **What to do**:
  - `PedalBoardGrid.kt`에 새 파라미터 추가:
    - `editingSlotIndex: Int? = null`
    - `onDeletePedal: (Int) -> Unit = {}`
  - `PedalSlot` 호출 시 파라미터 전달:
    - `isEditing = (editingSlotIndex == index)`
    - `onDeleteClick = { onDeletePedal(index) }`

  **References**:
  - `PedalBoardGrid.kt:32-42` - 현재 파라미터 정의
  - `PedalBoardGrid.kt:173-182` - PedalSlot 호출 부분
  - `PedalSlot.kt:59` - `isEditing` 파라미터 정의

  **Acceptance Criteria**:
  ```bash
  # editingSlotIndex 파라미터 존재 확인
  grep "editingSlotIndex: Int?" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt
  # Assert: 매칭 존재

  # onDeletePedal 파라미터 존재 확인
  grep "onDeletePedal:" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt
  # Assert: 매칭 존재

  # isEditing 전달 확인
  grep "isEditing = " app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt
  # Assert: 매칭 존재

  # 빌드 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  ```

  **Commit**: NO (Task 2와 함께)

---

- [x] 2. PedalBoardScreen 레이아웃 구조 수정

  **What to do**:
  - `Scaffold` 내부 최상위 레이아웃을 `Column`에서 `Box`로 변경
  - 기존 콘텐츠(`PedalBoardEditHeader` + 스크롤 `Column`)를 `Column`으로 래핑
  - `AnimatedVisibility` + `InlinePedalEditor`를 `Box` 내부로 이동
  - `Modifier.align(Alignment.BottomCenter)` 추가하여 하단 고정
  - 스크롤 `Column`에 `.weight(1f)` 추가하여 편집기 공간 확보

  **수정 전 구조**:
  ```
  Scaffold
  └── Column
      ├── PedalBoardEditHeader
      ├── Column (verticalScroll)
      │   ├── AnimatedVisibility (상단 UI)
      │   └── Box (Grid + Cable)
      └── AnimatedVisibility (InlinePedalEditor) ← 스크롤 안에 있어서 문제!
  ```

  **수정 후 구조**:
  ```
  Scaffold
  └── Box
      ├── Column
      │   ├── PedalBoardEditHeader
      │   └── Column (verticalScroll, weight=1f)
      │       ├── AnimatedVisibility (상단 UI)
      │       └── Box (Grid + Cable)
      └── AnimatedVisibility (align=BottomCenter) ← Box 하단에 고정!
          └── InlinePedalEditor
  ```

  **PedalBoardGrid 호출 수정**:
  - `editingSlotIndex = state.editingSlotIndex` 추가
  - `onDeletePedal = { viewModel.handleIntent(PedalBoardIntent.RemovePedalFromSlot(it)) }` 추가

  **References**:
  - `PedalBoardScreen.kt:107-261` - 현재 Scaffold 내부 구조
  - `PedalBoardScreen.kt:196-214` - PedalBoardGrid 호출 부분
  - `PedalBoardScreen.kt:228-260` - AnimatedVisibility + InlinePedalEditor 부분

  **Acceptance Criteria**:
  ```bash
  # Box 최상위 레이아웃 확인
  grep -A 3 "Scaffold" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt | grep "Box"
  # Assert: 매칭 존재

  # Alignment.BottomCenter 사용 확인
  grep "Alignment.BottomCenter" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 매칭 존재

  # editingSlotIndex 전달 확인
  grep "editingSlotIndex = state.editingSlotIndex" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 매칭 존재

  # onDeletePedal 전달 확인
  grep "onDeletePedal" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 매칭 존재

  # 빌드 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL

  # ktlint 검사
  ./gradlew ktlintCheck
  # Assert: BUILD SUCCESSFUL
  ```

  **Commit**: YES
  - Message: `fix(pedalboard): 인라인 편집기 하단 고정 및 삭제 버튼 표시 수정`
  - Files:
    - `PedalBoardGrid.kt`
    - `PedalBoardScreen.kt`

---

## Success Criteria

### Verification Commands
```bash
# 빌드 검증
./gradlew assembleDebug
# Expected: BUILD SUCCESSFUL

# ktlint
./gradlew ktlintCheck
# Expected: BUILD SUCCESSFUL
```

### Manual Testing (수정 후)
- [ ] 페달 클릭 시 하단에 인라인 편집기가 슬라이드 업으로 표시됨
- [ ] 편집기가 화면 하단에 고정되어 스크롤해도 위치 유지
- [ ] 편집 중인 페달 위에 빨간색 X 삭제 버튼이 표시됨
- [ ] 삭제 버튼 클릭 시 페달이 슬롯에서 제거됨
- [ ] 편집기 닫기 버튼 클릭 시 편집 모드 해제

---

## Code Changes Summary

### PedalBoardGrid.kt

**추가할 파라미터** (Line 32-42 근처):
```kotlin
fun PedalBoardGrid(
    slots: List<Pedal?>,
    columns: Int,
    rows: Int,
    editingSlotIndex: Int? = null,  // ← 추가
    onSlotClick: (Int) -> Unit,
    onAddClick: (Int) -> Unit,
    onSwapSlots: (fromIndex: Int, toIndex: Int) -> Unit,
    onSlotPositioned: (Int, Offset) -> Unit = { _, _ -> },
    onDeletePedal: (Int) -> Unit = {},  // ← 추가
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
)
```

**PedalSlot 호출 수정** (Line 173-182 근처):
```kotlin
PedalSlot(
    index = index,
    pedal = pedal,
    showAddButton = (pedal == null),
    onAddClick = { onAddClick(index) },
    onPedalClick = { onSlotClick(index) },
    isEditable = isEditable,
    isDragging = isDragging,
    isDropTarget = isDropTarget,
    isEditing = (editingSlotIndex == index),  // ← 추가
    onDeleteClick = { onDeletePedal(index) }  // ← 추가
)
```

### PedalBoardScreen.kt

**레이아웃 구조 변경** (Line 107-261):
```kotlin
Scaffold(...) { paddingValues ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PedalBoardEditHeader(...)
            
            Column(
                modifier = Modifier
                    .weight(1f)  // ← 추가: 편집기 공간 확보
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // 기존 상단 UI + Grid 내용
            }
        }
        
        // 인라인 편집기 - Box 하단에 고정
        AnimatedVisibility(
            visible = state.editingSlotIndex != null && state.editingPedal != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)  // ← 핵심: 하단 고정
        ) {
            InlinePedalEditor(...)
        }
    }
}
```

**PedalBoardGrid 호출 수정** (Line 196-214 근처):
```kotlin
PedalBoardGrid(
    slots = state.slots,
    columns = state.columns,
    rows = state.rows,
    editingSlotIndex = state.editingSlotIndex,  // ← 추가
    onSlotClick = { slotIndex ->
        viewModel.handleIntent(PedalBoardIntent.OpenPedalEditor(slotIndex))
    },
    onAddClick = { slotIndex ->
        addingToSlotIndex = slotIndex
        showAddPedalDialog = true
    },
    onSwapSlots = { fromIndex, toIndex ->
        viewModel.handleIntent(PedalBoardIntent.SwapSlots(fromIndex, toIndex))
    },
    onSlotPositioned = { index, offset ->
        slotPositions[index] = offset
    },
    onDeletePedal = { slotIndex ->  // ← 추가
        viewModel.handleIntent(PedalBoardIntent.RemovePedalFromSlot(slotIndex))
    },
    isEditable = true,
    modifier = Modifier.weight(1f)
)
```
