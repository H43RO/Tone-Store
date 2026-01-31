# PedalBoard Preview Resize - 카드 프리뷰 크기 증가 및 행 제한

## TL;DR

> **Quick Summary**: 카드 내 페달보드 프리뷰 크기를 증가시키고 (40dp → 100dp), 3행 이상의 페달보드는 2행까지만 표시하도록 clipping 적용
> 
> **Deliverables**:
> - `MiniPedalBoardPreview.kt` 수정 - slotHeight 증가, maxRows 파라미터 추가
> - `MiniExpressionPedalZone.kt` 수정 - 동적 height 파라미터 추가
> - Preview composable 업데이트
> 
> **Estimated Effort**: Quick (2-3 files, ~30분)
> **Parallel Execution**: NO - sequential (의존성 존재)
> **Critical Path**: Task 1 → Task 2 → Task 3

---

## Context

### Original Request
사용자: "결과물이 마음에 안 드네. 우선 이 UI가 카드에 모두 담겼으면 좋겠어. 카드 사이즈가 늘어나도 좋아. 그냥 페달보드 레이아웃이 모두 다 보이도록 해줘. 그런데 만약, 3줄 이상의 페달보드의 경우는 2줄까지만 보여줘."

### Interview Summary
**핵심 포인트**:
- 현재 프리뷰 (40dp slot height)가 너무 작아서 페달 인식 불가
- 이미지처럼 페달이 명확히 보이는 크기로 증가 필요
- 카드 크기 증가 허용
- 3행 이상은 2행까지만 표시 (clipping)
- Expression pedal도 비례하여 크기 증가

**Research Findings**:
- 원본 `PedalBoardGrid`: slotHeight = 140dp
- 원본 `ExpressionPedalZone`: 80dp × 200dp
- 현재 Mini 버전: slotHeight = 40dp (28%), expressionPedal = 24dp × 60dp

### Metis Review
**Identified Gaps** (addressed):
- slotHeight 값 미확정 → 100dp 적용 (원본 70%)
- 넓은 보드 오버플로우 → weight(1f)로 자동 분배
- 클리핑 표시자 필요 여부 → 단순 클리핑만 (범위 외)
- MiniPedalCard 패딩 스케일링 → 범위 외

---

## Work Objectives

### Core Objective
페달보드 카드 프리뷰의 크기를 증가시켜 사용자가 페달 레이아웃을 명확히 인식할 수 있도록 하고, 3행 이상의 보드는 2행까지만 표시

### Concrete Deliverables
1. `MiniPedalBoardPreview.kt` - slotHeight 100dp, maxRows 파라미터 추가
2. `MiniExpressionPedalZone.kt` - height 파라미터 추가 (동적 높이)
3. 각 파일의 @Preview 함수 업데이트

### Definition of Done
- [x] `./gradlew assembleDebug` → BUILD SUCCESSFUL
- [x] `./gradlew ktlintCheck` → No errors
- [x] 프리뷰에서 페달이 명확히 보임 (slot height >= 80dp)
- [x] 3행 이상 보드가 2행까지만 표시됨
- [x] Expression pedal이 프리뷰 높이에 맞춰 동적으로 조절됨

### Must Have
- slotHeight 기본값을 40dp에서 100dp로 증가
- maxRows 파라미터 추가 (기본값 = 2)
- MiniExpressionPedalZone에 height 파라미터 추가
- displayRows = min(rows, maxRows)로 행 제한

### Must NOT Have (Guardrails)
- ❌ NO `MiniPedalCard` 수정 (PedalSlot.kt 공유 컴포넌트)
- ❌ NO 클리핑 표시자 ("+ N more rows") 추가
- ❌ NO 스크롤 기능 추가
- ❌ NO 데이터 모델 변경
- ❌ NO 애니메이션 추가
- ❌ NO spacing 값 변경 (slotHeight만 변경)

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: NO
- **User wants tests**: NO (manual preview verification)
- **Framework**: None

### Automated Verification Only (NO User Intervention)

```bash
# Compilation verification
./gradlew assembleDebug
# Assert: BUILD SUCCESSFUL

# Lint verification
./gradlew ktlintCheck
# Assert: No errors

# Static code verification
grep -n "maxRows.*Int.*=" MiniPedalBoardPreview.kt
# Assert: Contains "maxRows: Int = 2" parameter

grep -n "slotHeight.*Dp.*=" MiniPedalBoardPreview.kt
# Assert: Default is >= 80.dp (not 40.dp)
```

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately):
└── Task 1: MiniExpressionPedalZone에 height 파라미터 추가

Wave 2 (After Wave 1):
└── Task 2: MiniPedalBoardPreview 수정 (slotHeight, maxRows)

Wave 3 (After Wave 2):
└── Task 3: 빌드 검증 및 린트 체크

Critical Path: Task 1 → Task 2 → Task 3
```

### Dependency Matrix

| Task | Depends On | Blocks |
|------|------------|--------|
| 1 | None | 2 |
| 2 | 1 | 3 |
| 3 | 2 | None |

---

## TODOs

- [x] 1. MiniExpressionPedalZone에 height 파라미터 추가

  **What to do**:
  - `MiniExpressionPedalZone` 함수에 `height: Dp` 파라미터 추가
  - 기존 하드코딩된 `.height(60.dp)`를 파라미터로 교체
  - 기본값 제공하여 기존 호출 유지
  - @Preview 함수 업데이트

  **Must NOT do**:
  - width는 변경하지 않음 (비례 유지)
  - 기존 스타일/색상 로직 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `[]`
  - Reason: 단순 파라미터 추가

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocks**: Task 2
  - **Blocked By**: None

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniExpressionPedalZone.kt:41-42` - 현재 하드코딩된 width/height

  **Acceptance Criteria**:
  ```bash
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  grep -n "height.*Dp" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniExpressionPedalZone.kt
  # Assert: "height: Dp" 파라미터 존재
  ```

  **Commit**: YES
  - Message: `refactor(pedalboard): add dynamic height parameter to MiniExpressionPedalZone`
  - Files: `MiniExpressionPedalZone.kt`

---

- [x] 2. MiniPedalBoardPreview 수정 - slotHeight 증가 및 maxRows 추가

  **What to do**:
  - `slotHeight` 기본값을 `40.dp` → `100.dp`로 변경
  - `maxRows: Int = 2` 파라미터 추가
  - `displayRows = minOf(rows, maxRows)` 계산 추가
  - 행 반복을 `rows`에서 `displayRows`로 변경
  - `expressionHeight` 계산: `(slotHeight + verticalSpacing) * displayRows - verticalSpacing`
  - `MiniExpressionPedalZone` 호출 시 `height = expressionHeight` 전달
  - `verticalSpacing`을 `3.dp` → `8.dp`로 증가 (비례)
  - `horizontalSpacing`을 `2.dp` → `6.dp`로 증가 (비례)
  - @Preview 함수에 3행, 4행 케이스 추가

  **Must NOT do**:
  - `MiniPedalCard` 수정 금지
  - 기존 weight(1f) 로직 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `[]`
  - Reason: 파라미터 및 계산 로직 수정

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocks**: Task 3
  - **Blocked By**: Task 1

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt:18-68` - 현재 구현
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt:49-51` - 원본 spacing 참조

  **Code Changes Required**:
  
  1. 함수 시그니처 변경:
  ```kotlin
  @Composable
  fun MiniPedalBoardPreview(
      slots: List<Pedal?>,
      columns: Int,
      rows: Int,
      expressionPedal: Pedal?,
      modifier: Modifier = Modifier,
      slotHeight: Dp = 100.dp,  // 40.dp → 100.dp
      maxRows: Int = 2          // NEW
  )
  ```

  2. 행 계산 로직 추가:
  ```kotlin
  val displayRows = minOf(rows, maxRows)
  val verticalSpacing = 8.dp   // 3.dp → 8.dp
  val horizontalSpacing = 6.dp // 2.dp → 6.dp
  ```

  3. 반복문 변경:
  ```kotlin
  for (rowIndex in 0 until displayRows) {  // rows → displayRows
  ```

  4. Expression pedal 높이 계산:
  ```kotlin
  if (expressionPedal != null) {
      val expressionHeight = (slotHeight + verticalSpacing) * displayRows - verticalSpacing
      MiniExpressionPedalZone(
          expressionPedal = expressionPedal,
          height = expressionHeight,  // NEW
          modifier = Modifier.align(Alignment.CenterVertically)
      )
  }
  ```

  **Acceptance Criteria**:
  ```bash
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  grep -n "maxRows" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt
  # Assert: "maxRows: Int = 2" 존재
  
  grep -n "slotHeight.*=" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt
  # Assert: "100.dp" 또는 더 큰 값
  
  grep -n "displayRows" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt
  # Assert: displayRows 계산 로직 존재
  ```

  **Commit**: YES
  - Message: `feat(pedalboard): increase preview size and limit to 2 rows max`
  - Files: `MiniPedalBoardPreview.kt`

---

- [x] 3. 빌드 검증 및 린트 체크

  **What to do**:
  - Clean build 실행
  - Lint check 실행
  - 파일 존재 확인

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocked By**: Task 2

  **Acceptance Criteria**:
  ```bash
  ./gradlew clean assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  ./gradlew ktlintCheck
  # Assert: No errors
  ```

  **Commit**: NO (검증만)

---

## Commit Strategy

| After Task | Message | Files |
|------------|---------|-------|
| 1 | `refactor(pedalboard): add dynamic height parameter to MiniExpressionPedalZone` | MiniExpressionPedalZone.kt |
| 2 | `feat(pedalboard): increase preview size and limit to 2 rows max` | MiniPedalBoardPreview.kt |
| 3 | (no commit) | - |

---

## Success Criteria

### Verification Commands
```bash
# Full build
./gradlew clean assembleDebug
# Expected: BUILD SUCCESSFUL

# Lint check
./gradlew ktlintCheck
# Expected: No errors
```

### Final Checklist
- [x] slotHeight 기본값이 100dp (또는 그 이상)
- [x] maxRows 파라미터 존재 (기본값 = 2)
- [x] MiniExpressionPedalZone에 height 파라미터 존재
- [x] 3행 이상 보드가 2행까지만 렌더링됨
- [x] 빌드 성공
- [x] 린트 통과
