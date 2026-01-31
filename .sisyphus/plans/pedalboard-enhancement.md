# PedalBoard Screen Enhancement

## TL;DR

> **Quick Summary**: 페달보드 화면을 실제 기타리스트의 워크플로우에 맞게 개선. 프리셋 페달 18종 확장, 카드 그리드 선택 UI, 케이블 연결 시각화, Expression 페달 전용 영역, 노브/이름 편집 기능 추가.
> 
> **Deliverables**:
> - 18개 프리셋 페달 (기존 8 + 신규 10) with 기본 색상
> - 카드 그리드 스타일 페달 선택 다이얼로그
> - 노브 추가/삭제, 페달/노브 이름 변경 기능
> - Expression Pedal Zone (Wah/Whammy 전용 영역)
> - 케이블 연결 UI (직선 + 잭 아이콘, ON/OFF 시각화)
> - 신호 체인 번호 표시
> 
> **Estimated Effort**: Large
> **Parallel Execution**: YES - 3 waves
> **Critical Path**: Task 1 → Task 2 → Task 5 → Task 8 → Task 9

---

## Context

### Original Request
사용자가 페달보드 생성 화면을 개선하고자 함:
1. 프리셋 페달 선택 다이얼로그 리디자인 (카드 그리드)
2. 프리셋 페달 종류 확장 (8개 → 18개)
3. 각 프리셋에 기본 색상 지정 (유명 페달 참조)
4. 노브 추가/삭제, 이름 변경 기능
5. 케이블 연결 UI (신호 흐름 시각화)
6. 와우/와미 페달 전용 영역 (Expression Zone)
7. 신호 체인 번호 표시, 추천 순서 안내

### Interview Summary
**Key Discussions**:
- 다이얼로그 스타일: 카드 그리드 (2-3열, 색상 미리보기)
- 케이블 스타일: 직선 + 1/4인치 잭 아이콘
- Expression Zone 위치: 그리드 오른쪽 고정
- 케이블 연결 모드: ON 페달 자동 연결 (신호 체인 순서)
- OFF 페달 표시: 점선으로 바이패스
- 기존 Wah 마이그레이션: 그대로 유지 (slots에 남김)

**Research Findings**:
- 현재 `Pedal.kt`에 `color: Long?` 필드 존재
- `PresetPedals.kt`에 8개 페달, 색상 미지정
- `PedalEditorBottomSheet.kt`는 읽기 전용 노브만 표시
- `SavedPedalBoard`/`SavedPedalBoardEntity`에 `expressionPedal` 필드 없음
- Room DB 현재 version 4

### Metis Review
**Identified Gaps** (addressed):
- 케이블 연결 모드 미결정 → 자동 연결 (신호 체인 순서)
- OFF 페달 표시 방식 미결정 → 점선 바이패스
- 기존 Wah 마이그레이션 → 그대로 유지
- `PedalCategory` enum 필요 → Task 1에서 추가

---

## Work Objectives

### Core Objective
페달보드 화면을 실제 기타 페달보드처럼 직관적이고 풍부한 기능으로 개선하여 사용자 경험 향상.

### Concrete Deliverables
- `PresetPedals.kt`: 18개 프리셋 + 기본 색상 + 카테고리
- `PedalCategory.kt`: 새 enum 파일 (DRIVE, MODULATION, TIME_BASED, DYNAMICS, UTILITY, PITCH)
- `SavedPedalBoard.kt` + `SavedPedalBoardEntity.kt`: expressionPedal 필드 추가
- `ToneStoreDatabase.kt`: MIGRATION_4_5 추가
- `PresetPedalSelectionDialog.kt`: 새 카드 그리드 다이얼로그
- `PedalEditorBottomSheet.kt`: 노브 추가/삭제, 이름 변경 기능
- `ExpressionPedalZone.kt`: Wah/Whammy 전용 컴포넌트
- `CableOverlay.kt`: 케이블 연결 시각화 Canvas
- `PedalBoardScreen.kt`: Expression Zone 통합, 케이블 오버레이 적용

### Definition of Done
- [ ] `./gradlew assembleDebug` 빌드 성공
- [ ] 18개 프리셋 페달 모두 선택 가능
- [ ] Expression Zone에 Wah/Whammy 배치 가능
- [ ] 케이블 연결이 ON 페달 간 직선으로 표시
- [ ] 노브 추가/삭제 동작 확인

### Must Have
- 18개 프리셋 페달 (기존 8 + 신규 10)
- 각 프리셋에 고유한 기본 색상
- 카드 그리드 스타일 선택 다이얼로그
- 노브 추가/삭제 기능 (최소 1개, 최대 6개)
- 페달 이름, 노브 이름 변경 기능
- Expression Pedal Zone (오른쪽 고정)
- ON 페달 간 케이블 연결 시각화
- OFF 페달 점선 바이패스 표시
- 신호 체인 번호 (1, 2, 3...) 표시

### Must NOT Have (Guardrails)
- 사운드 재생/미리듣기 기능 추가 금지
- 외부 페달 데이터베이스 연동 금지
- CreateToneScreen 수정 금지 (이 태스크 범위 아님)
- 페달 ON/OFF 프리셋 기능 (향후 확장, 이번 스코프 아님)
- 과도한 애니메이션 추가 (기존 앱 스타일 유지)
- 기존 저장된 데이터 손실 유발하는 마이그레이션

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: YES (Compose test dependencies)
- **User wants tests**: @Preview + assembleDebug 빌드 검증
- **Framework**: Compose Preview + Gradle build

### Automated Verification Approach

모든 태스크는 다음 방식으로 검증:

1. **빌드 검증**: `./gradlew assembleDebug` 성공
2. **Preview 검증**: @Preview 함수 생성하여 UI 컴포넌트 렌더링 확인
3. **런타임 검증**: 필요시 `adb shell am start` 명령으로 특정 화면 실행

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately):
├── Task 1: PedalCategory enum + 18 presets with colors
├── Task 2: SavedPedalBoard expressionPedal field + Room migration
└── Task 3: PedalEditorBottomSheet 노브 추가/삭제 기능

Wave 2 (After Wave 1):
├── Task 4: PedalEditorBottomSheet 이름 변경 기능 [depends: 3]
├── Task 5: PresetPedalSelectionDialog 카드 그리드 [depends: 1]
└── Task 6: ExpressionPedalZone 컴포넌트 [depends: 2]

Wave 3 (After Wave 2):
├── Task 7: PedalBoardScreen에 Expression Zone 통합 [depends: 6]
├── Task 8: CableOverlay 케이블 시각화 [depends: 1]
└── Task 9: PedalBoardScreen에 케이블 오버레이 + 신호 번호 통합 [depends: 7, 8]

Critical Path: Task 1 → Task 5 → Task 8 → Task 9
```

### Dependency Matrix

| Task | Depends On | Blocks | Can Parallelize With |
|------|------------|--------|---------------------|
| 1 | None | 5, 8 | 2, 3 |
| 2 | None | 6 | 1, 3 |
| 3 | None | 4 | 1, 2 |
| 4 | 3 | None | 5, 6 |
| 5 | 1 | None | 4, 6 |
| 6 | 2 | 7 | 4, 5 |
| 7 | 6 | 9 | 8 |
| 8 | 1 | 9 | 7 |
| 9 | 7, 8 | None | None (final) |

### Agent Dispatch Summary

| Wave | Tasks | Recommended Dispatch |
|------|-------|---------------------|
| 1 | 1, 2, 3 | 3 parallel agents: category="quick" for each |
| 2 | 4, 5, 6 | 3 parallel agents after Wave 1 completes |
| 3 | 7, 8, 9 | Task 7, 8 parallel, then Task 9 sequential |

---

## TODOs

### Wave 1 (Start Immediately)

- [x] 1. PedalCategory enum 추가 및 PresetPedals 18종 확장

  **What to do**:
  - `PedalCategory.kt` 새 파일 생성: DRIVE, MODULATION, TIME_BASED, DYNAMICS, UTILITY, PITCH
  - `PresetPedals.kt` 수정:
    - 기존 8개 페달에 `defaultColor` 값 추가
    - 신규 10개 페달 함수 추가 (Phaser, Flanger, Tremolo, Octave, Boost, Noise Gate, Tuner, EQ, Bass Preamp, Whammy)
    - 각 페달에 `category: PedalCategory` 파라미터 전달
  - 색상 값은 아래 표 참조:
    | 페달 | 색상 Hex | Long 값 |
    |------|---------|---------|
    | Overdrive | #3EB489 | 0xFF3EB489 |
    | Distortion | #FF9800 | 0xFFFF9800 |
    | Fuzz | #9E9E9E | 0xFF9E9E9E |
    | Chorus | #2196F3 | 0xFF2196F3 |
    | Delay | #42A5F5 | 0xFF42A5F5 |
    | Reverb | #64B5F6 | 0xFF64B5F6 |
    | Compressor | #E53935 | 0xFFE53935 |
    | Wah | #212121 | 0xFF212121 |
    | Phaser | #FF5722 | 0xFFFF5722 |
    | Flanger | #3F51B5 | 0xFF3F51B5 |
    | Tremolo | #FFEB3B | 0xFFFFEB3B |
    | Octave | #1E88E5 | 0xFF1E88E5 |
    | Boost | #FFC107 | 0xFFFFC107 |
    | Noise Gate | #607D8B | 0xFF607D8B |
    | Tuner | #FAFAFA | 0xFFFAFAFA |
    | EQ | #CFD8DC | 0xFFCFD8DC |
    | Bass Preamp | #FFD54F | 0xFFFFD54F |
    | Whammy | #D32F2F | 0xFFD32F2F |

  **Must NOT do**:
  - `Pedal.kt` 모델 자체를 변경하지 않음 (color 필드 이미 존재)
  - 기존 페달의 knob 구성 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 단순 파일 추가 + 기존 파일 확장, 로직 복잡도 낮음
  - **Skills**: [`git-master`]
    - `git-master`: 커밋 생성에 필요
  - **Skills Evaluated but Omitted**:
    - `frontend-ui-ux`: UI 작업 아님, 데이터 레이어 변경

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 2, 3)
  - **Blocks**: Tasks 5, 8
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/data/preset/PresetPedals.kt:14-23` - getPresetPedals() 구조, 새 페달 함수 추가 위치
  - `app/src/main/java/com/haero/tonestore/data/preset/PresetPedals.kt:29-39` - createOverdrive() 패턴: Pedal 생성 방식, knobs 구조

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/Pedal.kt:14-22` - Pedal data class, color: Long? 필드 존재
  - `app/src/main/java/com/haero/tonestore/domain/model/PedalType.kt` - PedalType enum (PRESET, CUSTOM)

  **New File Location**:
  - `app/src/main/java/com/haero/tonestore/domain/model/PedalCategory.kt` - 새 enum 파일

  **WHY Each Reference Matters**:
  - `PresetPedals.kt:14-23`: getPresetPedals()에 신규 페달 추가 위치 확인
  - `PresetPedals.kt:29-39`: createOverdrive() 패턴을 복사하여 신규 페달 함수 작성
  - `Pedal.kt:14-22`: color 필드가 이미 존재하므로 Pedal 모델 변경 불필요 확인

  **Acceptance Criteria**:

  **빌드 검증** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet
  # Assert: BUILD SUCCESSFUL
  # Assert: Exit code 0
  ```

  **코드 검증** (Bash):
  ```bash
  # PedalCategory enum 존재 확인
  grep -l "enum class PedalCategory" app/src/main/java/com/haero/tonestore/domain/model/PedalCategory.kt
  # Assert: 파일 경로 출력

  # 18개 페달 확인
  grep -c "create" app/src/main/java/com/haero/tonestore/data/preset/PresetPedals.kt
  # Assert: 18 이상
  ```

  **Commit**: YES
  - Message: `feat(pedal): add PedalCategory enum and expand presets to 18 types with default colors`
  - Files: `PedalCategory.kt`, `PresetPedals.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

- [x] 2. SavedPedalBoard에 expressionPedal 필드 추가 + Room Migration

  **What to do**:
  - `SavedPedalBoard.kt` 수정: `val expressionPedal: Pedal? = null` 필드 추가
  - `SavedPedalBoardEntity.kt` 수정: `val expressionPedalJson: String? = null` 필드 추가
  - Mapper 파일 수정 (있다면): expressionPedal ↔ expressionPedalJson 변환 로직
  - `ToneStoreDatabase.kt` 수정:
    - version 4 → 5
    - MIGRATION_4_5 추가: `ALTER TABLE saved_pedal_boards ADD COLUMN expressionPedalJson TEXT DEFAULT NULL`
    - DatabaseModule에 migration 등록

  **Must NOT do**:
  - 기존 slots 구조 변경 금지
  - 기존 Wah 페달 자동 마이그레이션 금지 (slots에 그대로 유지)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 필드 추가 + Room migration, 패턴 명확
  - **Skills**: [`git-master`]
    - `git-master`: 커밋 생성에 필요
  - **Skills Evaluated but Omitted**:
    - `frontend-ui-ux`: 데이터 레이어, UI 아님

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 3)
  - **Blocks**: Task 6
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/data/local/database/ToneStoreDatabase.kt:50-56` - MIGRATION_3_4 패턴: ALTER TABLE 사용 방식
  - `app/src/main/java/com/haero/tonestore/data/local/database/ToneStoreDatabase.kt:15-17` - @Database version 지정 위치

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/SavedPedalBoard.kt:15-23` - SavedPedalBoard data class 구조
  - `app/src/main/java/com/haero/tonestore/data/local/entity/SavedPedalBoardEntity.kt:9-19` - Entity 구조, slotsJson 패턴 참조

  **Mapper Location** (탐색 필요):
  - `app/src/main/java/com/haero/tonestore/data/local/mapper/` - Entity ↔ Domain 변환 로직 확인

  **WHY Each Reference Matters**:
  - `ToneStoreDatabase.kt:50-56`: MIGRATION_4_5 작성 시 ALTER TABLE 패턴 복사
  - `SavedPedalBoard.kt:15-23`: expressionPedal 필드 추가 위치
  - `SavedPedalBoardEntity.kt:9-19`: expressionPedalJson 필드 추가, slotsJson과 동일한 방식

  **Acceptance Criteria**:

  **빌드 검증** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet
  # Assert: BUILD SUCCESSFUL
  # Assert: Exit code 0
  ```

  **코드 검증** (Bash):
  ```bash
  # expressionPedal 필드 확인
  grep "expressionPedal" app/src/main/java/com/haero/tonestore/domain/model/SavedPedalBoard.kt
  # Assert: 출력 있음

  # DB version 5 확인
  grep "version = 5" app/src/main/java/com/haero/tonestore/data/local/database/ToneStoreDatabase.kt
  # Assert: 출력 있음

  # MIGRATION_4_5 확인
  grep "MIGRATION_4_5" app/src/main/java/com/haero/tonestore/data/local/database/ToneStoreDatabase.kt
  # Assert: 출력 있음
  ```

  **Commit**: YES
  - Message: `feat(data): add expressionPedal field to SavedPedalBoard with Room migration`
  - Files: `SavedPedalBoard.kt`, `SavedPedalBoardEntity.kt`, `ToneStoreDatabase.kt`, mapper files
  - Pre-commit: `./gradlew assembleDebug`

---

- [x] 3. PedalEditorBottomSheet 노브 추가/삭제 기능

  **What to do**:
  - `PedalEditorBottomSheet.kt` 수정:
    - 노브 목록을 `remember { mutableStateListOf(...) }`로 상태 관리
    - 각 노브 옆에 삭제 버튼 (X 아이콘) 추가
    - "노브 추가" 버튼 추가 (최대 6개 제한)
    - 최소 1개 노브 유지 (삭제 버튼 비활성화)
  - `PedalBoardIntent.kt` 수정: `UpdatePedalKnobs(slotIndex: Int, knobs: List<Knob>)` Intent 추가
  - `PedalBoardViewModel.kt` 수정: 해당 Intent 처리 로직
  - Callback 추가: `onKnobsChange: (List<Knob>) -> Unit`

  **Must NOT do**:
  - 색상 변경 로직 수정 금지 (이미 동작 중)
  - RotaryKnob 컴포넌트 자체 수정 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 기존 BottomSheet에 버튼/로직 추가, MVI 패턴 따름
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Compose UI 컴포넌트 수정
  - **Skills Evaluated but Omitted**:
    - `git-master`: 커밋은 Wave 1 완료 후 일괄

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2)
  - **Blocks**: Task 4
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalEditorBottomSheet.kt:86-112` - 현재 노브 표시 FlowRow 구조
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt:411-452` - CustomPedalDialog의 knobNames 상태 관리 패턴

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/Knob.kt` - Knob data class
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardIntent.kt` - Intent sealed class 구조

  **UI Component References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/components/RotaryKnob.kt` - 노브 표시에 사용 중인 컴포넌트

  **WHY Each Reference Matters**:
  - `PedalEditorBottomSheet.kt:86-112`: FlowRow 내부에 삭제 버튼 추가 위치
  - `PedalBoardScreen.kt:411-452`: mutableStateListOf 패턴으로 노브 상태 관리 방법
  - `PedalBoardIntent.kt`: 새 Intent 추가 패턴

  **Acceptance Criteria**:

  **빌드 검증** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet
  # Assert: BUILD SUCCESSFUL
  ```

  **코드 검증** (Bash):
  ```bash
  # 노브 추가 관련 코드 확인
  grep -E "onKnobsChange|UpdatePedalKnobs" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalEditorBottomSheet.kt
  # Assert: 출력 있음

  grep "UpdatePedalKnobs" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardIntent.kt
  # Assert: 출력 있음
  ```

  **Commit**: YES (grouped with Task 4)
  - Message: `feat(editor): add knob add/remove functionality in PedalEditorBottomSheet`
  - Files: `PedalEditorBottomSheet.kt`, `PedalBoardIntent.kt`, `PedalBoardViewModel.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

### Wave 2 (After Wave 1)

- [x] 4. PedalEditorBottomSheet 이름 변경 기능 (페달명 + 노브명)

  **What to do**:
  - `PedalEditorBottomSheet.kt` 수정:
    - 페달 이름 표시 부분을 `OutlinedTextField`로 변경 (편집 가능)
    - 각 노브의 label을 `OutlinedTextField`로 변경 (편집 가능)
    - 변경 시 `onPedalNameChange`, `onKnobNameChange` 콜백 호출
  - `PedalBoardIntent.kt` 추가:
    - `UpdatePedalName(slotIndex: Int, name: String)`
    - `UpdateKnobName(slotIndex: Int, knobIndex: Int, name: String)`
  - `PedalBoardViewModel.kt` 수정: 해당 Intent 처리

  **Must NOT do**:
  - 색상 변경 기존 로직 수정 금지
  - 노브 값(value) 편집 기능 추가 금지 (읽기 전용 유지)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Task 3의 연장선, 동일 파일 수정
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Compose TextField 추가
  - **Skills Evaluated but Omitted**:
    - `git-master`: 커밋은 Wave 2 완료 후

  **Parallelization**:
  - **Can Run In Parallel**: YES (after Wave 1)
  - **Parallel Group**: Wave 2 (with Tasks 5, 6)
  - **Blocks**: None
  - **Blocked By**: Task 3

  **References**:

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt:129-139` - OutlinedTextField 사용 패턴 (pedalboard name)
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalEditorBottomSheet.kt:66-74` - 현재 페달 이름 Text 표시 위치

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardIntent.kt` - Intent 추가 위치
  - `app/src/main/java/com/haero/tonestore/domain/model/Pedal.kt:14-22` - name 필드

  **WHY Each Reference Matters**:
  - `PedalBoardScreen.kt:129-139`: OutlinedTextField + onValueChange 패턴 복사
  - `PedalEditorBottomSheet.kt:66-74`: Text → OutlinedTextField 교체 위치

  **Acceptance Criteria**:

  **빌드 검증** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet
  # Assert: BUILD SUCCESSFUL
  ```

  **코드 검증** (Bash):
  ```bash
  # 이름 변경 Intent 확인
  grep -E "UpdatePedalName|UpdateKnobName" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardIntent.kt
  # Assert: 두 줄 이상 출력
  ```

  **Commit**: YES (grouped with Task 3)
  - Message: `feat(editor): add pedal and knob name editing in PedalEditorBottomSheet`
  - Files: `PedalEditorBottomSheet.kt`, `PedalBoardIntent.kt`, `PedalBoardViewModel.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

- [x] 5. PresetPedalSelectionDialog 카드 그리드 스타일 리디자인

  **What to do**:
  - `PresetPedalSelectionDialog.kt` 새 파일 생성 (또는 기존 AddPedalDialog 대체):
    - `ModalBottomSheet` 또는 `Dialog` 사용
    - `LazyVerticalGrid(columns = GridCells.Fixed(3))` 구조
    - 카테고리 탭 또는 필터 칩 (DRIVE, MODULATION, TIME_BASED, DYNAMICS, UTILITY, PITCH)
    - 각 페달 카드:
      - 페달 색상 배경 (또는 색상 뱃지)
      - 페달 이름
      - 노브 개수 표시 (선택사항)
    - 커스텀 페달 생성 버튼 포함
  - `PedalBoardScreen.kt` 수정:
    - 기존 `AddPedalDialog` 호출을 `PresetPedalSelectionDialog`로 교체

  **Must NOT do**:
  - 커스텀 페달 생성 로직 변경 금지 (기존 CustomPedalDialog 그대로 사용)
  - 페달 추가 후 슬롯 배치 로직 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: 새 UI 컴포넌트 설계, 카드 그리드 레이아웃
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: 세련된 카드 디자인, Material 3 활용
  - **Skills Evaluated but Omitted**:
    - `git-master`: 커밋은 Wave 2 완료 후

  **Parallelization**:
  - **Can Run In Parallel**: YES (after Wave 1)
  - **Parallel Group**: Wave 2 (with Tasks 4, 6)
  - **Blocks**: None
  - **Blocked By**: Task 1 (18개 페달 데이터 필요)

  **References**:

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt:371-409` - 기존 AddPedalDialog 구조 (교체 대상)
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt:61-70` - LazyVerticalGrid 사용 패턴
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt` - 페달 카드 스타일 참조

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/PedalCategory.kt` - 카테고리 enum (Task 1에서 생성)
  - `app/src/main/java/com/haero/tonestore/domain/model/Pedal.kt:21` - color: Long? 필드

  **Design References**:
  - 카드 크기: 약 100.dp x 100.dp
  - 색상 표시: 상단 stripe 또는 전체 배경 tint
  - 선택 시: elevation 증가 또는 border highlight

  **WHY Each Reference Matters**:
  - `PedalBoardScreen.kt:371-409`: 기존 다이얼로그를 새 컴포넌트로 교체할 위치
  - `PedalBoardGrid.kt:61-70`: LazyVerticalGrid + GridCells.Fixed 패턴
  - `PedalSlot.kt`: 기존 앱의 페달 시각 스타일 참조하여 일관성 유지

  **Acceptance Criteria**:

  **빌드 검증** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet
  # Assert: BUILD SUCCESSFUL
  ```

  **코드 검증** (Bash):
  ```bash
  # 새 다이얼로그 파일 존재 확인
  ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PresetPedalSelectionDialog.kt 2>/dev/null || ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PresetPedalSelectionDialog.kt 2>/dev/null
  # Assert: 파일 존재

  # LazyVerticalGrid 사용 확인
  grep "LazyVerticalGrid" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/**/PresetPedalSelectionDialog.kt 2>/dev/null || grep "LazyVerticalGrid" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PresetPedalSelectionDialog.kt 2>/dev/null
  # Assert: 출력 있음
  ```

  **Commit**: YES
  - Message: `feat(ui): redesign preset pedal selection as card grid dialog with categories`
  - Files: `PresetPedalSelectionDialog.kt`, `PedalBoardScreen.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

- [x] 6. ExpressionPedalZone 컴포넌트 생성

  **What to do**:
  - `ExpressionPedalZone.kt` 새 파일 생성 (`presentation/ui/pedalboard/components/`):
    - 세로로 긴 Box (1열 x 2행 느낌, 약 80.dp x 200.dp)
    - 내부:
      - 현재 선택된 Expression Pedal 표시 (Wah 또는 Whammy 또는 Empty)
      - 발판 형태 UI (위쪽 톱니/발판, 아래쪽 뒤꿈치)
      - 탭하여 Wah/Whammy 선택 다이얼로그 열기
    - Props: `expressionPedal: Pedal?`, `onSelectPedal: () -> Unit`, `onRemovePedal: () -> Unit`
  - `ExpressionPedalSelectionDialog.kt` 생성:
    - Wah, Whammy 중 선택하는 간단한 다이얼로그
  - ViewModel에 `expressionPedal` 상태 추가 (Task 2의 데이터 모델 사용)

  **Must NOT do**:
  - 이 컴포넌트를 PedalBoardGrid 내부에 넣지 않음 (별도 Box로 배치)
  - 드래그 기능 추가 금지 (고정 위치)

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: 발판 형태 커스텀 UI 디자인 필요
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: 실제 와우 페달 느낌의 UI 구현
  - **Skills Evaluated but Omitted**:
    - `git-master`: 커밋은 Wave 2 완료 후

  **Parallelization**:
  - **Can Run In Parallel**: YES (after Wave 1)
  - **Parallel Group**: Wave 2 (with Tasks 4, 5)
  - **Blocks**: Task 7
  - **Blocked By**: Task 2 (expressionPedal 데이터 모델 필요)

  **References**:

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalSlot.kt` - 페달 슬롯 UI 패턴
  - `app/src/main/java/com/haero/tonestore/presentation/ui/components/RotaryKnob.kt` - 커스텀 Canvas 드로잉 참조

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/Pedal.kt` - Pedal 모델
  - `app/src/main/java/com/haero/tonestore/data/preset/PresetPedals.kt:141-151` - createWah() 참조

  **Design Spec**:
  - 크기: 80.dp (너비) x 200.dp (높이)
  - 색상: 페달의 기본 색상 사용 (Wah: #212121, Whammy: #D32F2F)
  - 발판 형태: RoundedCornerShape(topStart=16.dp, topEnd=16.dp, bottomStart=4.dp, bottomEnd=4.dp)
  - Empty 상태: 점선 테두리 + "Wah/Whammy" 텍스트

  **WHY Each Reference Matters**:
  - `PedalSlot.kt`: 기존 페달 카드 스타일과 일관성 유지
  - `RotaryKnob.kt`: Canvas를 사용한 커스텀 드로잉 기법 참조

  **Acceptance Criteria**:

  **빌드 검증** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet
  # Assert: BUILD SUCCESSFUL
  ```

  **코드 검증** (Bash):
  ```bash
  # 새 컴포넌트 파일 확인
  ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt
  # Assert: 파일 존재

  # Composable 함수 확인
  grep "@Composable" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ExpressionPedalZone.kt
  # Assert: 출력 있음
  ```

  **Commit**: YES
  - Message: `feat(ui): create ExpressionPedalZone component for Wah/Whammy pedals`
  - Files: `ExpressionPedalZone.kt`, `ExpressionPedalSelectionDialog.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

### Wave 3 (After Wave 2)

- [x] 7. PedalBoardScreen에 Expression Zone 통합

  **What to do**:
  - `PedalBoardScreen.kt` 수정:
    - 기존 `PedalBoardGrid` 옆에 `ExpressionPedalZone` 배치
    - Row { PedalBoardGrid(weight(1f)) + ExpressionPedalZone } 구조
    - Expression 페달 선택/삭제 Intent 연결
  - `PedalBoardState.kt` 수정: `expressionPedal: Pedal?` 상태 추가
  - `PedalBoardIntent.kt` 수정:
    - `SelectExpressionPedal(pedal: Pedal)`
    - `RemoveExpressionPedal`
  - `PedalBoardViewModel.kt` 수정: 해당 Intent 처리 + 저장 시 expressionPedal 포함

  **Must NOT do**:
  - PedalBoardGrid 내부 로직 변경 금지
  - 기존 slots 저장 로직 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 기존 화면에 컴포넌트 배치 + Intent 연결
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Row 레이아웃 조정
  - **Skills Evaluated but Omitted**:
    - `git-master`: 커밋은 Wave 3 완료 후

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 (with Task 8)
  - **Blocks**: Task 9
  - **Blocked By**: Task 6

  **References**:

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt:209-224` - PedalBoardGrid 호출 위치
  - `app/src/main/java/com/haero/tonestore/presentation/viewmodel/PedalBoardViewModel.kt` - ViewModel 구조, handleIntent 패턴

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardState.kt` - State 구조
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardIntent.kt` - Intent sealed class

  **Layout Spec**:
  ```kotlin
  Row {
      PedalBoardGrid(modifier = Modifier.weight(1f), ...)
      Spacer(modifier = Modifier.width(12.dp))
      ExpressionPedalZone(...)
  }
  ```

  **WHY Each Reference Matters**:
  - `PedalBoardScreen.kt:209-224`: Row로 감싸서 ExpressionPedalZone 추가할 위치
  - `PedalBoardViewModel.kt`: Intent 처리 + expressionPedal 저장 로직 추가

  **Acceptance Criteria**:

  **빌드 검증** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet
  # Assert: BUILD SUCCESSFUL
  ```

  **코드 검증** (Bash):
  ```bash
  # ExpressionPedalZone import 확인
  grep "ExpressionPedalZone" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 출력 있음

  # Expression Intent 확인
  grep -E "SelectExpressionPedal|RemoveExpressionPedal" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardIntent.kt
  # Assert: 두 줄 출력
  ```

  **Commit**: YES
  - Message: `feat(pedalboard): integrate ExpressionPedalZone into PedalBoardScreen`
  - Files: `PedalBoardScreen.kt`, `PedalBoardState.kt`, `PedalBoardIntent.kt`, `PedalBoardViewModel.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

- [ ] 8. CableOverlay 케이블 시각화 컴포넌트

  **What to do**:
  - `CableOverlay.kt` 새 파일 생성 (`presentation/ui/pedalboard/components/`):
    - Box + Canvas 조합으로 케이블 그리기
    - Props:
      - `slots: List<Pedal?>` - 페달 목록
      - `slotPositions: Map<Int, Offset>` - 각 슬롯의 화면 좌표
      - `expressionPedal: Pedal?` - Expression 페달 (있으면 체인 끝에 연결)
    - 케이블 그리기 로직:
      1. ON 상태인 페달만 필터링
      2. order 순서대로 정렬
      3. 연속된 ON 페달 간 직선 그리기 (실선)
      4. OFF 페달은 점선으로 바이패스 표시
    - 잭 아이콘: 각 연결 끝점에 작은 원형 (1/4" 잭 심볼)
  - 신호 체인 번호:
    - 각 ON 페달 위에 순서 번호 Badge (1, 2, 3...)

  **Must NOT do**:
  - 애니메이션 추가 금지 (정적 라인만)
  - 곡선/베지어 케이블 금지 (직선만)

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: Canvas 드로잉, 좌표 계산 필요
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Canvas API 사용, drawLine, drawCircle
  - **Skills Evaluated but Omitted**:
    - `git-master`: 커밋은 Wave 3 완료 후

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 (with Task 7)
  - **Blocks**: Task 9
  - **Blocked By**: Task 1 (페달에 색상/카테고리 정보 필요)

  **References**:

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/components/RotaryKnob.kt` - Canvas 사용 패턴 참조
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt:88-91` - onGloballyPositioned로 좌표 수집 패턴

  **API/Type References**:
  - `app/src/main/java/com/haero/tonestore/domain/model/Pedal.kt:20` - isEnabled 필드 (ON/OFF 상태)
  - `app/src/main/java/com/haero/tonestore/domain/model/Pedal.kt:19` - order 필드 (신호 순서)

  **Canvas API**:
  ```kotlin
  Canvas(modifier = Modifier.fillMaxSize()) {
      // 실선 (ON 페달)
      drawLine(
          color = Color.White,
          start = Offset(x1, y1),
          end = Offset(x2, y2),
          strokeWidth = 3.dp.toPx()
      )
      // 점선 (OFF 바이패스)
      drawLine(
          color = Color.Gray,
          start = ...,
          end = ...,
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
      )
      // 잭 아이콘
      drawCircle(
          color = Color.White,
          radius = 6.dp.toPx(),
          center = Offset(x, y)
      )
  }
  ```

  **WHY Each Reference Matters**:
  - `RotaryKnob.kt`: Canvas 내에서 drawArc, drawCircle 패턴
  - `PedalBoardGrid.kt:88-91`: 슬롯 위치 수집하여 케이블 좌표 계산

  **Acceptance Criteria**:

  **빌드 검증** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet
  # Assert: BUILD SUCCESSFUL
  ```

  **코드 검증** (Bash):
  ```bash
  # CableOverlay 파일 확인
  ls app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/CableOverlay.kt
  # Assert: 파일 존재

  # Canvas 사용 확인
  grep "Canvas" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/CableOverlay.kt
  # Assert: 출력 있음

  # drawLine 사용 확인
  grep "drawLine" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/CableOverlay.kt
  # Assert: 출력 있음
  ```

  **Commit**: YES
  - Message: `feat(ui): create CableOverlay component for signal chain visualization`
  - Files: `CableOverlay.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

- [ ] 9. PedalBoardScreen에 케이블 오버레이 + 신호 번호 통합

  **What to do**:
  - `PedalBoardScreen.kt` 수정:
    - PedalBoardGrid와 ExpressionPedalZone을 감싸는 Box 추가
    - Box 내에 CableOverlay를 오버레이로 배치
    - 슬롯 좌표를 수집하여 CableOverlay에 전달
  - `PedalBoardGrid.kt` 수정:
    - 좌표 콜백 추가: `onSlotPositioned: (Int, Offset) -> Unit`
    - 각 슬롯의 중심 좌표를 부모에게 전달
  - 신호 번호 Badge:
    - CableOverlay 또는 PedalSlot에서 ON 페달에 번호 Badge 표시

  **Must NOT do**:
  - 드래그 로직 변경 금지
  - 성능 저하 유발하는 불필요한 recomposition 추가 금지

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: 여러 컴포넌트 조합, 좌표 전달 로직 복잡
  - **Skills**: [`frontend-ui-ux`]
    - `frontend-ui-ux`: Compose 레이아웃 통합, 좌표 계산
  - **Skills Evaluated but Omitted**:
    - `git-master`: 최종 커밋 담당

  **Parallelization**:
  - **Can Run In Parallel**: NO (final task)
  - **Parallel Group**: Sequential (after Tasks 7, 8)
  - **Blocks**: None (final)
  - **Blocked By**: Tasks 7, 8

  **References**:

  **Pattern References**:
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt:83-91` - onGloballyPositioned 사용 패턴
  - `app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt:209-224` - Grid + Zone 배치 위치 (Task 7에서 수정)

  **API/Type References**:
  - `androidx.compose.ui.layout.onGloballyPositioned` - 좌표 수집
  - `androidx.compose.ui.geometry.Offset` - 좌표 타입

  **Integration Spec**:
  ```kotlin
  Box(modifier = Modifier.fillMaxWidth()) {
      Row {
          PedalBoardGrid(
              onSlotPositioned = { index, offset ->
                  slotPositions[index] = offset
              },
              ...
          )
          ExpressionPedalZone(...)
      }
      CableOverlay(
          slots = state.slots,
          slotPositions = slotPositions,
          expressionPedal = state.expressionPedal,
          modifier = Modifier.matchParentSize()
      )
  }
  ```

  **WHY Each Reference Matters**:
  - `PedalBoardGrid.kt:83-91`: 기존 좌표 수집 로직 확장하여 부모에게 전달
  - `PedalBoardScreen.kt:209-224`: Box로 감싸서 오버레이 배치

  **Acceptance Criteria**:

  **빌드 검증** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet
  # Assert: BUILD SUCCESSFUL
  ```

  **코드 검증** (Bash):
  ```bash
  # CableOverlay import 확인
  grep "CableOverlay" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/PedalBoardScreen.kt
  # Assert: 출력 있음

  # onSlotPositioned 콜백 확인
  grep "onSlotPositioned" app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/PedalBoardGrid.kt
  # Assert: 출력 있음
  ```

  **Final Verification** (Bash):
  ```bash
  cd /Users/haero_kim/AndroidStudioProjects/ToneStore && ./gradlew assembleDebug --quiet && echo "FULL BUILD SUCCESS"
  # Assert: FULL BUILD SUCCESS
  ```

  **Commit**: YES
  - Message: `feat(pedalboard): integrate CableOverlay with signal chain numbers`
  - Files: `PedalBoardScreen.kt`, `PedalBoardGrid.kt`
  - Pre-commit: `./gradlew assembleDebug`

---

## Commit Strategy

| After Task | Message | Files | Verification |
|------------|---------|-------|--------------|
| 1 | `feat(pedal): add PedalCategory enum and expand presets to 18 types with default colors` | PedalCategory.kt, PresetPedals.kt | ./gradlew assembleDebug |
| 2 | `feat(data): add expressionPedal field to SavedPedalBoard with Room migration` | SavedPedalBoard.kt, Entity, Database, mapper | ./gradlew assembleDebug |
| 3+4 | `feat(editor): add knob and name editing in PedalEditorBottomSheet` | PedalEditorBottomSheet.kt, Intent, ViewModel | ./gradlew assembleDebug |
| 5 | `feat(ui): redesign preset pedal selection as card grid dialog` | PresetPedalSelectionDialog.kt, PedalBoardScreen.kt | ./gradlew assembleDebug |
| 6 | `feat(ui): create ExpressionPedalZone for Wah/Whammy` | ExpressionPedalZone.kt, Dialog | ./gradlew assembleDebug |
| 7 | `feat(pedalboard): integrate ExpressionPedalZone` | PedalBoardScreen.kt, State, Intent, ViewModel | ./gradlew assembleDebug |
| 8 | `feat(ui): create CableOverlay for signal chain visualization` | CableOverlay.kt | ./gradlew assembleDebug |
| 9 | `feat(pedalboard): integrate CableOverlay with signal chain numbers` | PedalBoardScreen.kt, PedalBoardGrid.kt | ./gradlew assembleDebug |

---

## Success Criteria

### Verification Commands
```bash
# 전체 빌드
./gradlew assembleDebug
# Expected: BUILD SUCCESSFUL

# 18개 프리셋 확인
grep -c "fun create" app/src/main/java/com/haero/tonestore/data/preset/PresetPedals.kt
# Expected: 18

# 새 파일들 존재 확인
ls -la app/src/main/java/com/haero/tonestore/presentation/ui/pedalboard/components/ | grep -E "CableOverlay|ExpressionPedal|PresetPedalSelection"
# Expected: 3개 파일 목록
```

### Final Checklist
- [ ] `./gradlew assembleDebug` 성공
- [ ] 18개 프리셋 페달 모두 색상 지정됨
- [ ] 카드 그리드 다이얼로그로 페달 선택 가능
- [ ] 노브 추가/삭제 동작 (1-6개 범위)
- [ ] 페달/노브 이름 변경 가능
- [ ] Expression Zone에 Wah/Whammy 배치 가능
- [ ] ON 페달 간 실선 케이블 표시
- [ ] OFF 페달 점선 바이패스 표시
- [ ] 신호 체인 번호 (1, 2, 3...) 표시
