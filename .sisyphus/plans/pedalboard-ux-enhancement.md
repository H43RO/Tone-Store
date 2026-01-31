# 페달보드 생성화면 UI/UX 고도화

## TL;DR

> **Quick Summary**: 페달보드 생성/편집 화면의 사용성 개선 - 바텀시트를 인라인 편집기로 교체하고, 노브 UI를 가로 스크롤로, 레이아웃 입력을 스테퍼로 변경
> 
> **Deliverables**:
> - `InlinePedalEditor.kt` - 새로운 인라인 편집기 컴포넌트
> - `LayoutStepper.kt` - 행/열 스테퍼 컴포넌트
> - `PedalBoardScreen.kt` 수정 - 인라인 편집기 통합 + 애니메이션
> - `PedalSlot.kt` 수정 - 삭제 버튼 오버레이 추가
> 
> **Estimated Effort**: Medium (~4-6시간)
> **Parallel Execution**: YES - 2 waves
> **Critical Path**: Task 1 → Task 3 → Task 4 → Task 5

---

## Context

### Original Request
페달보드 생성화면의 UI/UX 고도화:
1. 바텀시트 → 인라인 모달형 편집 UI로 변경 (아래에서 위로 애니메이션)
2. 노브 편집 UI를 가로 스크롤 Row로 재구성, 노브 추가 버튼은 맨 앞에
3. 페달 삭제 버튼을 페달 위 오버레이로 이동
4. 편집 시 상단 UI(이름/레이아웃) 숨김 애니메이션
5. 레이아웃 입력을 스테퍼 버튼(+/−)으로 변경

### Interview Summary
**Key Discussions**:
- 레이아웃 입력: 프리셋 칩 vs 비주얼 그리드 vs 스테퍼 → **스테퍼 버튼 선택**
- 페달 삭제 동작: 토글 vs 편집모드 통합 vs 롱프레스 → **편집 모드 통합**
- 상단 UI 숨김 여부: 숨기기 vs 축소 vs 유지 → **요청대로 숨기기**
- 인라인 편집기 위치: 그리드 아래 vs 화면 하단 고정 → **화면 하단 고정**
- 테스트 전략: **수동 테스트만**

### Metis Review
**Identified Gaps** (addressed):
- 인라인 편집기 배치 위치 → 화면 하단 고정으로 결정
- 노브 값 조작 여부 → 현재처럼 이름 편집만 (기본값)
- 스테퍼 범위 → columns 1~6, rows 1~4 (기본값)
- 삭제 버튼 디자인 → 우상단 X 버튼 스타일 (기본값)
- 상단 UI 숨김 시 공간 → 공간도 함께 사라짐 (기본값)

---

## Work Objectives

### Core Objective
페달보드 생성/편집 화면의 뎁스를 줄이고 직관적인 인라인 편집 경험 제공

### Concrete Deliverables
1. `InlinePedalEditor.kt` - 화면 하단 고정 인라인 편집기
2. `LayoutStepper.kt` - +/− 버튼으로 행/열 조절
3. `PedalBoardScreen.kt` 수정 - 편집기 통합 + 상단 UI 애니메이션
4. `PedalSlot.kt` 수정 - 삭제 오버레이 추가

### Definition of Done
- [x] `./gradlew assembleDebug` 빌드 성공
- [x] `./gradlew ktlintCheck` 통과
- [x] 페달 클릭 시 하단에 인라인 편집기 표시 (code complete, see code-verification.md)
- [x] 상단 UI가 편집 시 위로 사라지는 애니메이션 (code complete, see code-verification.md)
- [x] 노브가 가로 스크롤로 표시되고 + 버튼이 맨 앞에 (code complete, see code-verification.md)
- [x] 레이아웃 스테퍼로 행/열 조절 가능 (code complete, see code-verification.md)
- [x] 편집 중인 페달 위에 삭제 버튼 표시 (code complete, see code-verification.md)

### Must Have
- 인라인 편집기: 아래에서 위로 슬라이드 애니메이션
- 인라인 편집기: 기존 배경보다 밝은 색 + 상단 RoundedCornerShape
- 노브 UI: 가로 스크롤 LazyRow, + 버튼이 인덱스 0
- 스테퍼: columns 1~6, rows 1~4 범위 제한
- 삭제 버튼: 편집 중인 페달 우상단에 X 버튼 오버레이

### Must NOT Have (Guardrails)
- ❌ `PedalBoardViewModel`, `PedalBoardIntent`, `PedalBoardState` 변경 금지
- ❌ `PedalColorPicker` 내부 구현 변경 금지
- ❌ 드래그 앤 드롭 로직 (`PedalBoardGrid`) 변경 금지
- ❌ `PresetPedalSelectionDialog`, `CustomPedalDialog` 변경 금지
- ❌ `CableOverlay`, `ExpressionPedalZone` 변경 금지
- ❌ Navigation 경로 변경 금지
- ❌ 노브 값 조작 기능 추가 금지 (이름 편집만)
- ❌ 범용 공통 컴포넌트 생성 금지 (이 화면 전용)

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: YES (unit test 설정 존재)
- **User wants tests**: NO (수동 테스트만)
- **Framework**: N/A

### Code Verification Approach
Since manual device testing is out of scope for automated execution, all UI/UX behaviors have been verified at the **code implementation level**. See `.sisyphus/notepads/pedalboard-ux-enhancement/code-verification.md` for detailed proof that:
- All AnimatedVisibility blocks are correctly implemented
- All slide/fade animations are in place
- LazyRow with correct item ordering exists
- LayoutStepper is integrated and connected to ViewModel
- Delete overlay is implemented with correct visibility conditions

**Status**: ✅ All code implementations complete and verified via grep/inspection

### Automated Verification Only

각 TODO 완료 후 에이전트가 실행할 검증:

```bash
# 빌드 검증
./gradlew assembleDebug
# Assert: BUILD SUCCESSFUL

# 코드 스타일 검증
./gradlew ktlintCheck
# Assert: BUILD SUCCESSFUL (no violations)

# ModalBottomSheet import 제거 확인
grep -r "ModalBottomSheet" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
# Assert: 결과 없음 (exit code 1)

# 새 컴포넌트 파일 존재 확인
ls -la app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/InlinePedalEditor.kt
ls -la app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/LayoutStepper.kt
# Assert: 파일 존재
```

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately):
├── Task 1: LayoutStepper 컴포넌트 생성 [독립적]
└── Task 2: PedalSlot 삭제 오버레이 추가 [독립적]

Wave 2 (After Wave 1):
├── Task 3: InlinePedalEditor 컴포넌트 생성 [독립적]

Wave 3 (After Task 3):
└── Task 4: PedalBoardScreen 통합 [depends: 1, 2, 3]

Wave 4 (After Task 4):
└── Task 5: Preview 함수 추가 및 최종 검증 [depends: 4]

Critical Path: Task 1 → Task 3 → Task 4 → Task 5
Parallel Speedup: ~30% faster than sequential
```

### Dependency Matrix

| Task | Depends On | Blocks | Can Parallelize With |
|------|------------|--------|---------------------|
| 1 | None | 4 | 2 |
| 2 | None | 4 | 1 |
| 3 | None | 4 | 1, 2 (but start after for focus) |
| 4 | 1, 2, 3 | 5 | None |
| 5 | 4 | None | None (final) |

### Agent Dispatch Summary

| Wave | Tasks | Recommended Agents |
|------|-------|-------------------|
| 1 | 1, 2 | `delegate_task(category="visual-engineering", load_skills=["frontend-ui-ux"], run_in_background=true)` |
| 2 | 3 | `delegate_task(category="visual-engineering", load_skills=["frontend-ui-ux"], run_in_background=false)` |
| 3 | 4 | `delegate_task(category="visual-engineering", load_skills=["frontend-ui-ux"], run_in_background=false)` |
| 4 | 5 | `delegate_task(category="quick", load_skills=[], run_in_background=false)` |

---

## TODOs

- [x] 1. LayoutStepper 컴포넌트 생성

  **What to do**:
  - `presentation/ui/pedalboard/components/LayoutStepper.kt` 파일 생성
  - 행/열을 +/− 버튼으로 조절하는 컴포넌트 구현
  - 파라미터: `columns: Int`, `rows: Int`, `onColumnsChange: (Int) -> Unit`, `onRowsChange: (Int) -> Unit`
  - 범위 제한: columns 1~6, rows 1~4
  - UI 구성: `Row { Text("레이아웃") | [-] [N열] [+] × [-] [N행] [+] }`
  - 버튼 비활성화: 최소/최대값 도달 시

  **Must NOT do**:
  - 범용 Stepper 컴포넌트 생성 금지 - 이 화면 전용
  - 햅틱 피드백 추가 금지 (선택적이므로 생략)

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Skills**: `["frontend-ui-ux"]`
    - `frontend-ui-ux`: Compose UI 컴포넌트 디자인 전문
  - **Skills Evaluated but Omitted**:
    - `playwright`: 웹 브라우저 관련 - 해당 없음
    - `git-master`: 커밋 작업 아님

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Task 2)
  - **Blocks**: Task 4
  - **Blocked By**: None

  **References**:
  - **Pattern References**:
    - `PedalBoardScreen.kt:152-204` - 현재 OutlinedTextField 기반 레이아웃 입력 (교체 대상)
  - **API/Type References**:
    - `PedalBoardIntent.UpdateLayout(columns, rows)` - 기존 Intent 재사용
  - **External References**:
    - Material 3 IconButton: 표준 +/− 버튼 구현

  **Acceptance Criteria**:
  ```bash
  # 파일 존재 확인
  ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/LayoutStepper.kt
  # Assert: 파일 존재

  # 컴파일 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL

  # ktlint 검사
  ./gradlew ktlintCheck
  # Assert: BUILD SUCCESSFUL
  ```

  **Commit**: NO (Task 4 완료 후 일괄 커밋)

---

- [x] 2. PedalSlot에 삭제 버튼 오버레이 추가

  **What to do**:
  - `PedalSlot.kt`에 `isEditing: Boolean = false` 파라미터 추가
  - `onDeleteClick: () -> Unit` 콜백 파라미터 추가
  - `isEditing == true`일 때 MiniPedalCard 위에 삭제 버튼 오버레이 표시
  - 삭제 버튼: 우상단 위치, X 아이콘, 작은 원형 버튼
  - 버튼 색상: `MaterialTheme.colorScheme.error` 배경

  **Must NOT do**:
  - MiniPedalCard 내부 로직 변경 금지
  - 기존 클릭/드래그 동작 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Skills**: `["frontend-ui-ux"]`
    - `frontend-ui-ux`: 오버레이 UI 디자인

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Task 1)
  - **Blocks**: Task 4
  - **Blocked By**: None

  **References**:
  - **Pattern References**:
    - `PedalSlot.kt:44-129` - 현재 PedalSlot 구현
    - `PedalSlot.kt:131-184` - MiniPedalCard 구현 (변경 금지)
  - **API/Type References**:
    - `Icons.Default.Close` - X 아이콘
    - `MaterialTheme.colorScheme.error` - 삭제 버튼 배경색

  **Acceptance Criteria**:
  ```bash
  # isEditing 파라미터 존재 확인
  grep "isEditing: Boolean" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt
  # Assert: 매칭 존재

  # onDeleteClick 파라미터 존재 확인
  grep "onDeleteClick" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt
  # Assert: 매칭 존재

  # 컴파일 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  ```

  **Commit**: NO (Task 4 완료 후 일괄 커밋)

---

- [x] 3. InlinePedalEditor 컴포넌트 생성

  **What to do**:
  - `presentation/ui/pedalboard/components/InlinePedalEditor.kt` 파일 생성
  - 기존 `PedalEditorBottomSheet.kt` 로직을 기반으로 새 컴포넌트 작성
  - ModalBottomSheet 제거 → 일반 Column/Card 기반
  - **스타일링**:
    - 배경색: `MaterialTheme.colorScheme.surfaceContainerHigh` (기존보다 밝음)
    - 상단 모서리: `RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)`
    - elevation: `8.dp`
  - **노브 UI 재구성**:
    - `FlowRow` → `LazyRow`로 변경
    - 노브 추가 버튼(+)을 **맨 첫 번째 인덱스**에 배치
    - 노브 이름 TextField 너비 = 노브 크기 (56.dp)
    - `horizontalArrangement = Arrangement.spacedBy(12.dp)`
  - **삭제 버튼 제거**: 슬롯 오버레이로 이동했으므로 하단 삭제 버튼 제거
  - 기존 props 유지: `pedal`, `slotIndex`, `onDismiss`, `onColorChange`, `onKnobsChange`, `onPedalNameChange`, `onKnobNameChange`
  - `sheetState` 파라미터 제거 (ModalBottomSheet 아니므로)

  **Must NOT do**:
  - PedalColorPicker 변경 금지 - 그대로 import 사용
  - RotaryKnob 변경 금지 - 그대로 사용
  - 노브 값 조작 기능 추가 금지

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Skills**: `["frontend-ui-ux"]`
    - `frontend-ui-ux`: Compose UI 마이그레이션 전문

  **Parallelization**:
  - **Can Run In Parallel**: NO (핵심 작업이므로 집중)
  - **Parallel Group**: Wave 2 (단독)
  - **Blocks**: Task 4
  - **Blocked By**: None (but start after Wave 1 for focus)

  **References**:
  - **Pattern References**:
    - `PedalEditorBottomSheet.kt:45-223` - 기존 편집기 전체 (참조 및 변환 대상)
    - `PedalEditorBottomSheet.kt:126-174` - 노브 FlowRow 구현 (LazyRow로 변환)
    - `PedalEditorBottomSheet.kt:198-202` - PedalColorPicker 사용 (그대로 유지)
  - **API/Type References**:
    - `Pedal` - `domain/model/Pedal.kt`
    - `Knob` - `domain/model/Knob.kt`
    - `PedalColorPicker` - `components/PedalColorPicker.kt`
    - `RotaryKnob` - `ui/components/RotaryKnob.kt`
  - **External References**:
    - Compose LazyRow: `androidx.compose.foundation.lazy.LazyRow`
    - Material 3 Card/Surface: elevated surface 스타일링

  **Acceptance Criteria**:
  ```bash
  # 파일 존재 확인
  ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/InlinePedalEditor.kt
  # Assert: 파일 존재

  # ModalBottomSheet import 없음 확인
  grep "ModalBottomSheet" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/InlinePedalEditor.kt
  # Assert: 결과 없음 (exit code 1)

  # LazyRow 사용 확인
  grep "LazyRow" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/InlinePedalEditor.kt
  # Assert: 매칭 존재

  # 컴파일 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  ```

  **Commit**: NO (Task 4 완료 후 일괄 커밋)

---

- [x] 4. PedalBoardScreen에 모든 변경사항 통합

  **What to do**:
  - **Import 변경**:
    - `PedalEditorBottomSheet` import 제거
    - `InlinePedalEditor` import 추가
    - `LayoutStepper` import 추가
    - `AnimatedVisibility`, `slideInVertically`, `slideOutVertically` import 추가
  - **sheetState 제거**:
    - `rememberModalBottomSheetState()` 호출 제거 (line 79)
  - **상단 UI 애니메이션**:
    - line 136-214 (이름 입력 ~ 페달 카운트) 영역을 `AnimatedVisibility`로 래핑
    - `visible = state.editingSlotIndex == null`
    - `enter = slideInVertically { -it } + fadeIn()`
    - `exit = slideOutVertically { -it } + fadeOut()`
  - **레이아웃 입력 교체**:
    - line 152-204의 `Row { OutlinedTextField... }` 제거
    - `LayoutStepper` 컴포넌트로 교체
  - **인라인 편집기 통합**:
    - line 266-297의 `PedalEditorBottomSheet` 호출 제거
    - 화면 하단에 `AnimatedVisibility` + `InlinePedalEditor` 추가
    - `visible = state.editingSlotIndex != null && state.editingPedal != null`
    - `enter = slideInVertically { it } + fadeIn()`
    - `exit = slideOutVertically { it } + fadeOut()`
  - **PedalBoardGrid 수정**:
    - `onSlotClick`에서 호출하는 슬롯에 `isEditing` 전달
    - 삭제 콜백 연결: `onDeleteClick = { viewModel.handleIntent(PedalBoardIntent.RemovePedalFromSlot(index)) }`
  - **외부 클릭 처리**:
    - 편집기 외부 영역 클릭 시 `ClosePedalEditor` Intent 발행
    - `Box` + `pointerInput` 또는 `clickable` modifier 사용

  **Must NOT do**:
  - ViewModel Intent/State 변경 금지
  - PedalBoardGrid 드래그 로직 변경 금지
  - PresetPedalSelectionDialog, CustomPedalDialog 변경 금지
  - Navigation 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Skills**: `["frontend-ui-ux"]`
    - `frontend-ui-ux`: 복잡한 Compose 화면 통합

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (단독)
  - **Blocks**: Task 5
  - **Blocked By**: Tasks 1, 2, 3

  **References**:
  - **Pattern References**:
    - `PedalBoardScreen.kt:68-350` - 전체 화면 구조
    - `PedalBoardScreen.kt:79` - sheetState 생성 (제거 대상)
    - `PedalBoardScreen.kt:136-214` - 상단 UI 영역 (AnimatedVisibility 래핑)
    - `PedalBoardScreen.kt:152-204` - 레이아웃 입력 (LayoutStepper로 교체)
    - `PedalBoardScreen.kt:232-251` - PedalBoardGrid 호출 (수정 필요)
    - `PedalBoardScreen.kt:266-297` - 바텀시트 호출 (인라인 편집기로 교체)
  - **API/Type References**:
    - `PedalBoardIntent.ClosePedalEditor` - 편집 종료 Intent
    - `PedalBoardIntent.RemovePedalFromSlot(slotIndex)` - 삭제 Intent
    - `PedalBoardIntent.UpdateLayout(columns, rows)` - 레이아웃 변경 Intent
  - **External References**:
    - Compose AnimatedVisibility: `androidx.compose.animation.AnimatedVisibility`
    - slideInVertically/slideOutVertically: `androidx.compose.animation.slideInVertically`

  **Acceptance Criteria**:
  ```bash
  # ModalBottomSheet import 제거 확인
  grep "ModalBottomSheet" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 결과 없음 (exit code 1)

  # rememberModalBottomSheetState 제거 확인
  grep "rememberModalBottomSheetState" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 결과 없음 (exit code 1)

  # AnimatedVisibility 사용 확인
  grep "AnimatedVisibility" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 매칭 존재 (2개 이상)

  # InlinePedalEditor 사용 확인
  grep "InlinePedalEditor" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 매칭 존재

  # LayoutStepper 사용 확인
  grep "LayoutStepper" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 매칭 존재

  # 빌드 검증
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL

  # ktlint 검사
  ./gradlew ktlintCheck
  # Assert: BUILD SUCCESSFUL
  ```

  **Commit**: YES
  - Message: `feat(pedalboard): 편집 UI를 인라인 방식으로 개선`
  - Files: 
    - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt`
    - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/InlinePedalEditor.kt`
    - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/LayoutStepper.kt`
    - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt`

---

- [x] 5. Preview 함수 추가 및 최종 검증

  **What to do**:
  - `InlinePedalEditor.kt`에 `@Preview` 함수 추가
  - `LayoutStepper.kt`에 `@Preview` 함수 추가
  - `PedalSlot.kt`에 삭제 오버레이 상태 Preview 추가
  - 전체 빌드 및 ktlint 최종 검증
  - `PedalEditorBottomSheet.kt` 파일 삭제 (사용되지 않음)

  **Must NOT do**:
  - 새로운 기능 추가 금지
  - 테스트 코드 작성 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `[]`
  - Reason: 단순 Preview 추가 및 정리 작업

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 4 (final)
  - **Blocks**: None
  - **Blocked By**: Task 4

  **References**:
  - **Pattern References**:
    - `PedalBoardScreen.kt:542-555` - 기존 Preview 패턴

  **Acceptance Criteria**:
  ```bash
  # PedalEditorBottomSheet.kt 삭제 확인
  ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalEditorBottomSheet.kt 2>/dev/null
  # Assert: 파일 없음 (exit code 2)

  # Preview 함수 존재 확인
  grep "@Preview" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/InlinePedalEditor.kt
  grep "@Preview" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/LayoutStepper.kt
  # Assert: 매칭 존재

  # 최종 빌드 검증
  ./gradlew clean assembleDebug
  # Assert: BUILD SUCCESSFUL

  # 최종 ktlint 검사
  ./gradlew ktlintCheck
  # Assert: BUILD SUCCESSFUL
  ```

  **Commit**: YES
  - Message: `refactor(pedalboard): 미사용 PedalEditorBottomSheet 제거 및 Preview 추가`
  - Files:
    - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalEditorBottomSheet.kt` (삭제)
    - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/InlinePedalEditor.kt`
    - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/LayoutStepper.kt`

---

## Commit Strategy

| After Task | Message | Files | Verification |
|------------|---------|-------|--------------|
| 4 | `feat(pedalboard): 편집 UI를 인라인 방식으로 개선` | PedalBoardScreen.kt, InlinePedalEditor.kt, LayoutStepper.kt, PedalSlot.kt | `./gradlew assembleDebug && ./gradlew ktlintCheck` |
| 5 | `refactor(pedalboard): 미사용 PedalEditorBottomSheet 제거 및 Preview 추가` | PedalEditorBottomSheet.kt (삭제), InlinePedalEditor.kt, LayoutStepper.kt | `./gradlew clean assembleDebug` |

---

## Success Criteria

### Verification Commands
```bash
# 전체 빌드
./gradlew clean assembleDebug
# Expected: BUILD SUCCESSFUL

# ktlint
./gradlew ktlintCheck
# Expected: BUILD SUCCESSFUL

# ModalBottomSheet 완전 제거 확인
grep -r "ModalBottomSheet" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/
# Expected: 결과 없음 (PresetPedalSelectionDialog 제외하고)

# 새 컴포넌트 존재 확인
ls -la app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/{InlinePedalEditor,LayoutStepper}.kt
# Expected: 두 파일 모두 존재
```

### Final Checklist
- [x] `InlinePedalEditor.kt` 생성됨
- [x] `LayoutStepper.kt` 생성됨
- [x] `PedalBoardScreen.kt`에서 ModalBottomSheet 제거됨
- [x] `PedalSlot.kt`에 삭제 오버레이 추가됨
- [x] `PedalEditorBottomSheet.kt` 삭제됨
- [x] 상단 UI 애니메이션 동작 (code verified in code-verification.md)
- [x] 인라인 편집기 슬라이드 애니메이션 동작 (code verified in code-verification.md)
- [x] 노브가 가로 스크롤 LazyRow로 표시 (code verified in code-verification.md)
- [x] 레이아웃 스테퍼로 행/열 조절 가능 (code verified in code-verification.md)
- [x] 빌드 성공 + ktlint 통과
