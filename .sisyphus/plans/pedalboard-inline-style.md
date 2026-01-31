# 페달보드 UI 인라인 스타일 변경

## TL;DR

> **Quick Summary**: 페달보드 편집 화면의 "모달 스타일" UI를 "인라인 스타일"로 변경하여 그리드와 자연스럽게 통합
> 
> **Deliverables**:
> - PedalboardInfoEditor: Surface 제거, 인라인 스타일
> - InlinePedalEditor: Surface 제거, 인라인 스타일  
> - 전환 애니메이션 제거 (즉시 전환)
> - 삭제 버튼: 정중앙, 반투명 검은색(alpha=0.5)
> 
> **Estimated Effort**: Quick
> **Parallel Execution**: NO - sequential
> **Critical Path**: Task 1 → Task 2 → Task 3 → Task 4 → Build

---

## Context

### Original Request
페달보드 생성 화면 다시 차근차근해보자. 이게 아니야. 페달 정보 편집 UI 는 그리드 바로 아래 위치해야 하고, RoundShape 나 배경색을 바꾸는 등 모달 모양일 필요도 없어. 그리고 페달 편집 UI 도 마찬가지야. 모달 처럼 뜨지 않았으면 좋겠어. RoundShape 나 배경색이 있을 필요가 없어. 그리고 두 UI 간 전환 애니메이션을 자연스럽게 바꿔줘. 그리고 각 페달의 삭제 버튼은 페달 정중앙에 떴으면 좋겠고, 색상은 반투명 검은색으로 바꿔줘

### Interview Summary
**Key Discussions**:
- 전환 애니메이션: 애니메이션 없음 (즉시 전환)
- 삭제 버튼 표시: 페달 선택 시에만
- 삭제 버튼 투명도: alpha = 0.5
- 다크 모드 대응: 모드 무관하게 검은색 유지
- UI 구분: 구분선 없이 패딩만으로 자연스럽게

### Metis Review
**Identified Gaps** (addressed):
- 삭제 버튼 alpha 값: 0.5로 확정
- 다크 모드 대응: 모드 무관 검은색 유지
- Surface 제거 후 구분 방법: 구분선 없음, 기존 패딩 유지

---

## Work Objectives

### Core Objective
"모달 스타일" 편집 UI를 "인라인 스타일"로 변경하여 그리드와 자연스럽게 통합

### Concrete Deliverables
- `PedalboardInfoEditor.kt`: Surface 제거
- `InlinePedalEditor.kt`: Surface 제거
- `PedalBoardScreen.kt`: Crossfade 애니메이션 제거
- `PedalSlot.kt`: 삭제 버튼 위치/색상 변경

### Definition of Done
- [ ] `./gradlew assembleDebug` → BUILD SUCCESSFUL
- [ ] `./gradlew testDebugUnitTest` → BUILD SUCCESSFUL
- [ ] Surface, RoundedCornerShape, tonalElevation 제거됨 (코드 검증)
- [ ] Crossfade 제거됨 (코드 검증)
- [ ] 삭제 버튼 중앙 + 반투명 검은색 적용됨 (코드 검증)

### Must Have
- Surface 완전 제거 (두 편집 UI 모두)
- Crossfade 애니메이션 완전 제거
- 삭제 버튼 정중앙 배치
- 삭제 버튼 Color.Black.copy(alpha = 0.5f)

### Must NOT Have (Guardrails)
- ❌ 내부 로직, 콜백 함수, 상태 관리 코드 변경 금지
- ❌ Preview 함수 로직 변경 금지 (컴파일 에러만 수정)
- ❌ 다른 컴포넌트 파일 변경 금지 (4개 파일만 수정)
- ❌ padding, spacing 값 변경 금지
- ❌ ViewModel Intent/State 변경 금지
- ❌ 콜백 시그니처 변경 금지

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: YES
- **User wants tests**: NO (Manual verification)
- **Framework**: N/A (빌드 검증만)

### Automated Verification

**빌드 검증:**
```bash
./gradlew assembleDebug
# Assert: BUILD SUCCESSFUL

./gradlew testDebugUnitTest  
# Assert: BUILD SUCCESSFUL, 기존 테스트 통과
```

**코드 검증 (수정 확인):**
```bash
# PedalboardInfoEditor에서 Surface 제거 확인
grep -c "Surface(" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalboardInfoEditor.kt
# Assert: 0

# Crossfade 제거 확인
grep -c "Crossfade" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
# Assert: 0

# 삭제 버튼 색상 확인
grep "Color.Black.copy(alpha = 0.5f)" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt
# Assert: 발견됨
```

---

## Execution Strategy

### Sequential Execution
```
Task 1: PedalboardInfoEditor (Surface 제거)
    ↓
Task 2: InlinePedalEditor (Surface 제거)
    ↓
Task 3: PedalSlot (삭제 버튼 변경)
    ↓
Task 4: PedalBoardScreen (Crossfade 제거)
    ↓
Build Verification
```

### Dependency Matrix

| Task | Depends On | Blocks | Can Parallelize With |
|------|------------|--------|---------------------|
| 1 | None | None | 2, 3 |
| 2 | None | None | 1, 3 |
| 3 | None | None | 1, 2 |
| 4 | 1, 2 | Build | None |

---

## TODOs

- [x] 1. PedalboardInfoEditor: Surface 제거, 인라인 스타일로 변경

  **What to do**:
  1. `Surface(...)` 컴포저블을 `Column(...)`으로 교체
  2. `shape`, `color`, `tonalElevation` 파라미터 제거
  3. 기존 `Column` 내부 구조는 그대로 유지
  4. import에서 Surface 제거 (사용 안 하면)

  **Must NOT do**:
  - padding 값 변경 금지: `padding(horizontal = 24.dp, vertical = 20.dp)` 유지
  - 내부 OutlinedTextField, LayoutStepper, Text 수정 금지
  - 콜백 함수 시그니처 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 단순 컴포넌트 교체 작업
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Compose UI 변경 작업

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 2, 3)
  - **Blocks**: Task 4
  - **Blocked By**: None

  **References**:

  **Pattern References**:
  - `PedalboardInfoEditor.kt:38-43` - 현재 Surface 구조 (교체 대상)
  - `PedalboardInfoEditor.kt:44-79` - 내부 Column 구조 (유지)

  **변경 전:**
  ```kotlin
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      tonalElevation = 8.dp
  ) {
      Column(
          modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 24.dp, vertical = 20.dp)
      ) {
          // 내부 내용
      }
  }
  ```

  **변경 후:**
  ```kotlin
  Column(
      modifier = modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 20.dp)
  ) {
      // 내부 내용 (그대로 유지)
  }
  ```

  **Acceptance Criteria**:
  ```bash
  # 빌드 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL

  # Surface 제거 확인
  grep -c "Surface(" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalboardInfoEditor.kt
  # Assert: 0

  # RoundedCornerShape 제거 확인  
  grep -c "RoundedCornerShape" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalboardInfoEditor.kt
  # Assert: 0 (OutlinedTextField의 shape 제외)
  ```

  **Commit**: YES
  - Message: `refactor(pedalboard): PedalboardInfoEditor Surface 제거, 인라인 스타일로 변경`
  - Files: `PedalboardInfoEditor.kt`

---

- [x] 2. InlinePedalEditor: Surface 제거, 인라인 스타일로 변경

  **What to do**:
  1. 최상위 `Surface(...)` 컴포저블을 `Column(...)`으로 교체
  2. `shape`, `color`, `tonalElevation` 파라미터 제거
  3. 기존 내부 구조는 그대로 유지
  4. import에서 Surface 제거 (사용 안 하면)

  **Must NOT do**:
  - padding 값 변경 금지: `padding(horizontal = 24.dp), padding(bottom = 32.dp)` 유지
  - knobsList, pedalNameEditState, knobNamesEditState 로직 수정 금지
  - LaunchedEffect 로직 수정 금지
  - 콜백 함수 시그니처 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 단순 컴포넌트 교체 작업
  - **Skills**: [`frontend-ui-ux`]

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 3)
  - **Blocks**: Task 4
  - **Blocked By**: None

  **References**:

  **Pattern References**:
  - `InlinePedalEditor.kt:80-85` - 현재 Surface 구조 (교체 대상)
  - `InlinePedalEditor.kt:86-236` - 내부 Column 구조 (유지)

  **변경 전:**
  ```kotlin
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      tonalElevation = 8.dp
  ) {
      Column(
          modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 24.dp)
              .padding(bottom = 32.dp)
              .verticalScroll(rememberScrollState())
      ) {
          // 내부 내용
      }
  }
  ```

  **변경 후:**
  ```kotlin
  Column(
      modifier = modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .padding(bottom = 32.dp)
          .verticalScroll(rememberScrollState())
  ) {
      // 내부 내용 (그대로 유지)
  }
  ```

  **Acceptance Criteria**:
  ```bash
  # 빌드 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL

  # 최상위 Surface 제거 확인 (PedalColorPicker 내부 Surface는 OK)
  # InlinePedalEditor 함수 시작 부분에서 Surface 사용 안 함 확인
  grep -A5 "fun InlinePedalEditor" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/InlinePedalEditor.kt | grep -c "Surface("
  # Assert: 0
  ```

  **Commit**: YES
  - Message: `refactor(pedalboard): InlinePedalEditor Surface 제거, 인라인 스타일로 변경`
  - Files: `InlinePedalEditor.kt`

---

- [x] 3. PedalSlot: 삭제 버튼 위치/색상 변경

  **What to do**:
  1. 삭제 버튼 Box의 `Alignment.TopEnd` → `Alignment.Center`로 변경
  2. 삭제 버튼 Box의 `padding(4.dp)` 제거 (중앙 배치에 불필요)
  3. Surface의 `color = MaterialTheme.colorScheme.error` → `color = Color.Black.copy(alpha = 0.5f)`로 변경
  4. Icon의 `tint = MaterialTheme.colorScheme.onError` → `tint = Color.White`로 변경

  **Must NOT do**:
  - 삭제 버튼 표시 조건 변경 금지: `isEditing && pedal != null` 유지
  - 삭제 버튼 아이콘 변경 금지: `Icons.Default.Close` 유지
  - 삭제 버튼 크기 변경 금지: `32.dp` 유지
  - onDeleteClick 콜백 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 스타일 속성 변경 작업
  - **Skills**: [`frontend-ui-ux`]

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2)
  - **Blocks**: None
  - **Blocked By**: None

  **References**:

  **Pattern References**:
  - `PedalSlot.kt:135-159` - 삭제 버튼 현재 구현 (수정 대상)

  **변경 전:**
  ```kotlin
  if (isEditing && pedal != null) {
      Box(
          modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(4.dp)
      ) {
          Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(32.dp)
          ) {
              IconButton(
                  onClick = onDeleteClick,
                  modifier = Modifier.fillMaxSize()
              ) {
                  Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "삭제",
                      tint = MaterialTheme.colorScheme.onError,
                      modifier = Modifier.size(20.dp)
                  )
              }
          }
      }
  }
  ```

  **변경 후:**
  ```kotlin
  if (isEditing && pedal != null) {
      Box(
          modifier = Modifier.align(Alignment.Center)
      ) {
          Surface(
              shape = CircleShape,
              color = Color.Black.copy(alpha = 0.5f),
              modifier = Modifier.size(32.dp)
          ) {
              IconButton(
                  onClick = onDeleteClick,
                  modifier = Modifier.fillMaxSize()
              ) {
                  Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "삭제",
                      tint = Color.White,
                      modifier = Modifier.size(20.dp)
                  )
              }
          }
      }
  }
  ```

  **Acceptance Criteria**:
  ```bash
  # 빌드 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL

  # 중앙 정렬 확인
  grep "Alignment.Center" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt
  # Assert: 삭제 버튼 관련 라인에서 발견

  # 반투명 검은색 확인
  grep "Color.Black.copy(alpha = 0.5f)" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt
  # Assert: 발견됨

  # 흰색 아이콘 확인
  grep "tint = Color.White" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt
  # Assert: 발견됨
  ```

  **Commit**: YES
  - Message: `style(pedalboard): 삭제 버튼 정중앙 배치, 반투명 검은색으로 변경`
  - Files: `PedalSlot.kt`

---

- [x] 4. PedalBoardScreen: Crossfade 제거, 즉시 전환으로 변경

  **What to do**:
  1. `Crossfade(...)` 컴포저블을 단순 `if-else`로 교체
  2. `animationSpec = tween(durationMillis = 150)` 제거
  3. import에서 `Crossfade`, `tween` 제거 (사용 안 하면)

  **Must NOT do**:
  - PedalboardInfoEditor, InlinePedalEditor에 전달하는 파라미터 변경 금지
  - 조건문 로직 변경 금지: `state.editingSlotIndex != null && state.editingPedal != null` 유지
  - ViewModel Intent 호출 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 컴포넌트 교체 작업
  - **Skills**: [`frontend-ui-ux`]

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (after Wave 1)
  - **Blocks**: Build verification
  - **Blocked By**: Tasks 1, 2 (Surface 제거 완료 후)

  **References**:

  **Pattern References**:
  - `PedalBoardScreen.kt:191-238` - 현재 Crossfade 구현 (교체 대상)

  **변경 전:**
  ```kotlin
  Crossfade(
      targetState = state.editingSlotIndex != null && state.editingPedal != null,
      animationSpec = tween(durationMillis = 150)
  ) { isEditingPedal ->
      if (isEditingPedal && state.editingPedal != null && state.editingSlotIndex != null) {
          InlinePedalEditor(...)
      } else {
          PedalboardInfoEditor(...)
      }
  }
  ```

  **변경 후:**
  ```kotlin
  if (state.editingSlotIndex != null && state.editingPedal != null) {
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
  } else {
      PedalboardInfoEditor(
          name = state.name,
          columns = state.columns,
          rows = state.rows,
          pedalCount = state.pedalCount,
          totalSlots = state.totalSlots,
          nameError = state.nameError,
          onNameChange = { viewModel.handleIntent(PedalBoardIntent.UpdateName(it)) },
          onColumnsChange = { newColumns ->
              viewModel.handleIntent(PedalBoardIntent.UpdateLayout(newColumns, state.rows))
          },
          onRowsChange = { newRows ->
              viewModel.handleIntent(PedalBoardIntent.UpdateLayout(state.columns, newRows))
          }
      )
  }
  ```

  **Acceptance Criteria**:
  ```bash
  # 빌드 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL

  # Crossfade 제거 확인
  grep -c "Crossfade" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 0

  # tween 제거 확인 (import 포함)
  grep -c "tween" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 0
  ```

  **Commit**: YES
  - Message: `refactor(pedalboard): Crossfade 애니메이션 제거, 즉시 전환으로 변경`
  - Files: `PedalBoardScreen.kt`

---

- [x] 5. 최종 빌드 검증 및 테스트

  **What to do**:
  1. 전체 빌드 검증
  2. 유닛 테스트 실행
  3. 불필요한 import 정리

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocked By**: Tasks 1, 2, 3, 4

  **Acceptance Criteria**:
  ```bash
  # 전체 빌드
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL

  # 유닛 테스트
  ./gradlew testDebugUnitTest
  # Assert: BUILD SUCCESSFUL

  # 코드 스타일 (선택)
  ./gradlew ktlintCheck
  # Assert: BUILD SUCCESSFUL 또는 warning만
  ```

  **Commit**: NO (필요시 import 정리 커밋)

---

## Commit Strategy

| After Task | Message | Files |
|------------|---------|-------|
| 1 | `refactor(pedalboard): PedalboardInfoEditor Surface 제거, 인라인 스타일로 변경` | PedalboardInfoEditor.kt |
| 2 | `refactor(pedalboard): InlinePedalEditor Surface 제거, 인라인 스타일로 변경` | InlinePedalEditor.kt |
| 3 | `style(pedalboard): 삭제 버튼 정중앙 배치, 반투명 검은색으로 변경` | PedalSlot.kt |
| 4 | `refactor(pedalboard): Crossfade 애니메이션 제거, 즉시 전환으로 변경` | PedalBoardScreen.kt |

---

## Success Criteria

### Verification Commands
```bash
./gradlew assembleDebug    # Expected: BUILD SUCCESSFUL
./gradlew testDebugUnitTest # Expected: BUILD SUCCESSFUL
```

### Final Checklist
- [ ] All "Must Have" present
- [ ] All "Must NOT Have" absent
- [ ] All builds pass
- [ ] 4 commits created
