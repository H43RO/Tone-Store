# Preview Simplification - Mini* 컴포넌트 제거 및 원본 컴포넌트 재사용

## TL;DR

> **Quick Summary**: Mini* 컴포넌트(MiniPedalBoardPreview, MiniExpressionPedalZone)를 삭제하고, 원본 ExpressionPedalZone에 파라미터를 추가하여 프리뷰에서 재사용하도록 단순화
> 
> **Deliverables**:
> - `ExpressionPedalZone.kt` 수정 - isEditable, width, height 파라미터 추가
> - `PedalBoardPreview.kt` 생성 - MiniPedalCard + ExpressionPedalZone 사용
> - `PedalBoardListScreen.kt` 수정 - 새 프리뷰 컴포넌트 사용
> - `MiniPedalBoardPreview.kt` 삭제
> - `MiniExpressionPedalZone.kt` 삭제
> 
> **Estimated Effort**: Quick (~30분)
> **Parallel Execution**: NO - sequential (의존성 존재)
> **Critical Path**: Task 1 → Task 2 → Task 3 → Task 4

---

## Context

### Original Request
사용자: "굳이 MiniPedalCard, MiniExpressionPedalZone 를 만들 필요가 있었을까? 원래 PedalCard, ExpressionPedalZone 을 사용해도 돼. 프리뷰 테스트를 해보니까, 원본 사이즈대로 나와도 UX 상 문제가 없는 것 같아."

### Interview Summary
**핵심 포인트**:
- 원본 컴포넌트 재사용으로 코드 단순화 원함
- ExpressionPedalZone: 프리뷰에서 삭제 버튼 숨김 필요 → `isEditable` 파라미터 추가
- ExpressionPedalZone: 크기 조절 필요 → `width`, `height` 파라미터 추가 (기본값 유지)
- maxRows=2 클리핑 유지
- MiniPedalCard 직접 사용 (PedalSlot 전체가 아닌)

**Research Findings**:
- MiniPedalCard는 `internal fun`으로 PedalSlot.kt에 정의됨 (lines 133-184)
- 같은 `components` 패키지 내에서 접근 가능
- ExpressionPedalZone은 현재 80dp × 200dp 하드코딩 (lines 40-41)
- 삭제 버튼은 lines 100-109에 있음

### Metis Review
**Identified Gaps** (addressed):
- ExpressionPedalZone 크기 문제 → width/height 파라미터 추가로 해결
- MiniPedalCard visibility → 같은 패키지라 internal 접근 가능
- Callback handling → 빈 람다 + isEditable로 버튼 숨김

---

## Work Objectives

### Core Objective
중복된 Mini* 컴포넌트를 제거하고, 원본 컴포넌트에 파라미터를 추가하여 코드를 단순화

### Concrete Deliverables
1. `ExpressionPedalZone.kt` - isEditable, width, height 파라미터 추가
2. `PedalBoardPreview.kt` - 새 프리뷰 컴포넌트 (MiniPedalCard + ExpressionPedalZone 사용)
3. `PedalBoardListScreen.kt` - 새 프리뷰 컴포넌트 import 및 사용
4. `MiniPedalBoardPreview.kt` 삭제
5. `MiniExpressionPedalZone.kt` 삭제

### Definition of Done
- [x] `./gradlew assembleDebug` → BUILD SUCCESSFUL
- [x] `./gradlew ktlintCheck` → No errors
- [x] Mini* 파일 2개 삭제됨
- [x] ExpressionPedalZone에 isEditable, width, height 파라미터 존재
- [x] PedalBoardPreview 컴포넌트 생성됨
- [x] PedalBoardListScreen에서 새 프리뷰 사용

### Must Have
- ExpressionPedalZone에 `isEditable: Boolean = true` 파라미터 추가
- ExpressionPedalZone에 `width: Dp = 80.dp`, `height: Dp = 200.dp` 파라미터 추가
- isEditable=false일 때 삭제 버튼(IconButton) 숨김
- maxRows=2 클리핑 로직 유지

### Must NOT Have (Guardrails)
- ❌ NO MiniPedalCard 구현 변경
- ❌ NO PedalSlot.kt 동작 변경
- ❌ NO 기존 ExpressionPedalZone 사용처(PedalBoardScreen)에 영향
- ❌ NO 프리뷰 함수(@Preview) 삭제 - 유지 필요
- ❌ NO 새로운 Mini* 컴포넌트 생성

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: NO
- **User wants tests**: NO (manual preview verification)
- **Framework**: None

### Automated Verification Only (NO User Intervention)

```bash
# Task 1 완료 후
./gradlew assembleDebug
grep -n "isEditable" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt
# Assert: "isEditable: Boolean = true" 존재

grep -n "width.*Dp\|height.*Dp" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt
# Assert: width, height 파라미터 존재

# Task 2 완료 후
ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardPreview.kt
# Assert: 파일 존재

# Task 3 완료 후
grep -n "PedalBoardPreview" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardListScreen.kt
# Assert: PedalBoardPreview import 및 사용

# Task 4 완료 후
ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt
ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniExpressionPedalZone.kt
# Assert: 두 파일 모두 "No such file or directory"

# 최종 검증
./gradlew clean assembleDebug
./gradlew ktlintCheck
grep -r "MiniPedalBoardPreview\|MiniExpressionPedalZone" app/src/main/java --include="*.kt"
# Assert: 매치 없음 (삭제된 컴포넌트 참조 없음)
```

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately):
└── Task 1: ExpressionPedalZone에 파라미터 추가

Wave 2 (After Wave 1):
└── Task 2: PedalBoardPreview 컴포넌트 생성

Wave 3 (After Wave 2):
└── Task 3: PedalBoardListScreen 업데이트

Wave 4 (After Wave 3):
└── Task 4: Mini* 파일 삭제 및 최종 검증

Critical Path: Task 1 → Task 2 → Task 3 → Task 4
```

### Dependency Matrix

| Task | Depends On | Blocks |
|------|------------|--------|
| 1 | None | 2 |
| 2 | 1 | 3 |
| 3 | 2 | 4 |
| 4 | 3 | None |

---

## TODOs

- [x] 1. ExpressionPedalZone에 isEditable, width, height 파라미터 추가

  **What to do**:
  - `isEditable: Boolean = true` 파라미터 추가
  - `width: Dp = 80.dp` 파라미터 추가
  - `height: Dp = 200.dp` 파라미터 추가
  - 하드코딩된 `.width(80.dp).height(200.dp)`를 파라미터로 교체
  - `isEditable=false`일 때 IconButton(삭제 버튼) 숨김: `if (isEditable) { IconButton(...) }`
  - @Preview 함수들은 유지

  **Must NOT do**:
  - 기존 시각적 외관 변경 금지
  - 기본값이 아닌 다른 사용처 영향 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `[]`
  - Reason: 단순 파라미터 추가 및 조건문 래핑

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocks**: Task 2
  - **Blocked By**: None

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt:24-29` - 함수 시그니처
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt:40-41` - 하드코딩된 width/height
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt:100-109` - 삭제 버튼 (IconButton)

  **Code Changes Required**:
  
  1. 함수 시그니처 변경 (line 24-29):
  ```kotlin
  @Composable
  fun ExpressionPedalZone(
      expressionPedal: Pedal?,
      onSelectPedal: () -> Unit,
      onRemovePedal: () -> Unit,
      modifier: Modifier = Modifier,
      isEditable: Boolean = true,     // NEW
      width: Dp = 80.dp,              // NEW
      height: Dp = 200.dp             // NEW
  )
  ```

  2. 크기 적용 변경 (line 39-41):
  ```kotlin
  Box(
      modifier = modifier
          .width(width)      // 80.dp → width
          .height(height)    // 200.dp → height
  ```

  3. 삭제 버튼 조건부 렌더링 (line 100-109 래핑):
  ```kotlin
  if (isEditable) {
      IconButton(
          onClick = onRemovePedal,
          modifier = Modifier.size(24.dp)
      ) {
          Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Remove",
              modifier = Modifier.size(16.dp)
          )
      }
  }
  ```

  **Acceptance Criteria**:
  ```bash
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  grep -n "isEditable.*Boolean.*true" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt
  # Assert: 파라미터 존재
  
  grep -n "width.*Dp.*80" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt
  # Assert: width 파라미터 존재
  
  grep -n "if.*isEditable" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt
  # Assert: IconButton 조건부 렌더링
  ```

  **Commit**: YES
  - Message: `refactor(pedalboard): add isEditable and size params to ExpressionPedalZone`
  - Files: `ExpressionPedalZone.kt`

---

- [x] 2. PedalBoardPreview 컴포넌트 생성

  **What to do**:
  - `components/PedalBoardPreview.kt` 파일 생성
  - MiniPedalBoardPreview의 로직을 가져오되 MiniExpressionPedalZone 대신 ExpressionPedalZone 사용
  - MiniPedalCard 사용 (같은 패키지라 internal 접근 가능)
  - maxRows=2 클리핑 로직 유지
  - @Preview 함수 포함

  **Must NOT do**:
  - MiniPedalCard 구현 변경 금지
  - 새로운 Mini* 컴포넌트 생성 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `[]`
  - Reason: 기존 로직 재조합

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocks**: Task 3
  - **Blocked By**: Task 1

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt:18-74` - 기존 구현 (복사 후 수정)
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt:133-184` - MiniPedalCard (internal, 재사용)
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt` - 수정된 ExpressionPedalZone

  **Code Structure**:
  ```kotlin
  package com.haero.tonestore.presentation.ui.pedalboard.components

  // imports...

  @Composable
  fun PedalBoardPreview(
      slots: List<Pedal?>,
      columns: Int,
      rows: Int,
      expressionPedal: Pedal?,
      modifier: Modifier = Modifier,
      slotHeight: Dp = 140.dp,
      maxRows: Int = 2
  ) {
      val displayRows = minOf(rows, maxRows)
      val verticalSpacing = 8.dp
      val horizontalSpacing = 6.dp
      
      Row(
          modifier = modifier,
          horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
      ) {
          Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(verticalSpacing)
          ) {
              for (rowIndex in 0 until displayRows) {
                  Row(horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)) {
                      for (colIndex in 0 until columns) {
                          val index = rowIndex * columns + colIndex
                          val pedal = slots.getOrNull(index)
                          
                          if (pedal != null) {
                              MiniPedalCard(
                                  pedal = pedal,
                                  modifier = Modifier.weight(1f).height(slotHeight)
                              )
                          } else {
                              Spacer(modifier = Modifier.weight(1f).height(slotHeight))
                          }
                      }
                  }
              }
          }
          
          if (expressionPedal != null) {
              val expressionHeight = (slotHeight + verticalSpacing) * displayRows - verticalSpacing
              ExpressionPedalZone(
                  expressionPedal = expressionPedal,
                  onSelectPedal = {},
                  onRemovePedal = {},
                  isEditable = false,
                  width = 40.dp,  // 프리뷰용 축소 크기
                  height = expressionHeight,
                  modifier = Modifier.align(Alignment.CenterVertically)
              )
          }
      }
  }

  // @Preview functions...
  ```

  **Acceptance Criteria**:
  ```bash
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardPreview.kt
  # Assert: 파일 존재
  
  grep -n "fun PedalBoardPreview" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardPreview.kt
  # Assert: 함수 정의 존재
  
  grep -n "ExpressionPedalZone" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardPreview.kt
  # Assert: ExpressionPedalZone 사용 (MiniExpressionPedalZone 아님)
  
  grep -n "isEditable.*false" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardPreview.kt
  # Assert: isEditable = false로 호출
  ```

  **Commit**: YES
  - Message: `feat(pedalboard): create PedalBoardPreview using original components`
  - Files: `PedalBoardPreview.kt`

---

- [x] 3. PedalBoardListScreen 업데이트

  **What to do**:
  - import 문 변경: `MiniPedalBoardPreview` → `PedalBoardPreview`
  - `PedalBoardCard` 내 컴포넌트 호출 이름 변경
  - 기능 동일, 이름만 변경

  **Must NOT do**:
  - 다른 로직 변경 금지
  - 파라미터 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `[]`
  - Reason: 단순 이름 교체

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocks**: Task 4
  - **Blocked By**: Task 2

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardListScreen.kt:60` - import 문
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardListScreen.kt:293-299` - 컴포넌트 사용

  **Code Changes Required**:
  
  1. import 변경 (line 60):
  ```kotlin
  // BEFORE
  import com.haero.tonestore.presentation.ui.pedalboard.components.MiniPedalBoardPreview
  // AFTER
  import com.haero.tonestore.presentation.ui.pedalboard.components.PedalBoardPreview
  ```

  2. 컴포넌트 호출 변경 (line 293):
  ```kotlin
  // BEFORE
  MiniPedalBoardPreview(
  // AFTER
  PedalBoardPreview(
  ```

  **Acceptance Criteria**:
  ```bash
  ./gradlew assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  grep -n "import.*PedalBoardPreview" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardListScreen.kt
  # Assert: PedalBoardPreview import 존재
  
  grep -n "MiniPedalBoardPreview" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardListScreen.kt
  # Assert: 매치 없음 (기존 참조 제거됨)
  ```

  **Commit**: YES
  - Message: `refactor(pedalboard): use PedalBoardPreview in PedalBoardListScreen`
  - Files: `PedalBoardListScreen.kt`

---

- [x] 4. Mini* 파일 삭제 및 최종 검증

  **What to do**:
  - `MiniPedalBoardPreview.kt` 삭제
  - `MiniExpressionPedalZone.kt` 삭제
  - 전체 빌드 검증
  - 린트 검증
  - 삭제된 컴포넌트 참조 없음 확인

  **Must NOT do**:
  - 다른 파일 삭제 금지
  - PedalSlot.kt의 MiniPedalCard 삭제 금지 (여전히 사용됨)

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `[]`
  - Reason: 파일 삭제 및 검증

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocked By**: Task 3

  **References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt` - 삭제 대상
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniExpressionPedalZone.kt` - 삭제 대상

  **Acceptance Criteria**:
  ```bash
  # 파일 삭제 확인
  ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniPedalBoardPreview.kt 2>&1
  # Assert: "No such file or directory"
  
  ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/MiniExpressionPedalZone.kt 2>&1
  # Assert: "No such file or directory"
  
  # 참조 없음 확인
  grep -r "MiniPedalBoardPreview\|MiniExpressionPedalZone" app/src/main/java --include="*.kt"
  # Assert: 매치 없음
  
  # 최종 빌드 검증
  ./gradlew clean assembleDebug
  # Assert: BUILD SUCCESSFUL
  
  ./gradlew ktlintCheck
  # Assert: No errors
  ```

  **Commit**: YES
  - Message: `refactor(pedalboard): remove redundant Mini* preview components`
  - Files: (deleted) `MiniPedalBoardPreview.kt`, `MiniExpressionPedalZone.kt`

---

## Commit Strategy

| After Task | Message | Files |
|------------|---------|-------|
| 1 | `refactor(pedalboard): add isEditable and size params to ExpressionPedalZone` | ExpressionPedalZone.kt |
| 2 | `feat(pedalboard): create PedalBoardPreview using original components` | PedalBoardPreview.kt |
| 3 | `refactor(pedalboard): use PedalBoardPreview in PedalBoardListScreen` | PedalBoardListScreen.kt |
| 4 | `refactor(pedalboard): remove redundant Mini* preview components` | MiniPedalBoardPreview.kt (deleted), MiniExpressionPedalZone.kt (deleted) |

---

## Success Criteria

### Verification Commands
```bash
# 전체 빌드
./gradlew clean assembleDebug
# Expected: BUILD SUCCESSFUL

# 린트 체크
./gradlew ktlintCheck
# Expected: No errors

# 삭제된 파일 참조 없음
grep -r "MiniPedalBoardPreview\|MiniExpressionPedalZone" app/src/main/java --include="*.kt"
# Expected: No matches
```

### Final Checklist
- [x] ExpressionPedalZone에 isEditable, width, height 파라미터 추가됨
- [x] PedalBoardPreview.kt 생성됨
- [x] PedalBoardListScreen에서 새 프리뷰 사용
- [x] MiniPedalBoardPreview.kt 삭제됨
- [x] MiniExpressionPedalZone.kt 삭제됨
- [x] 빌드 성공
- [x] 린트 통과
- [x] 삭제된 컴포넌트 참조 없음
