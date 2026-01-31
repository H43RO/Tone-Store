
## Task 1: PedalboardInfoEditor Surface 제거

### 변경 내용
- Surface 컴포저블을 Column으로 교체 (lines 37-80)
- Surface import 제거
- RoundedCornerShape(topStart/topEnd) 제거
- padding 값 유지: `padding(horizontal = 24.dp, vertical = 20.dp)`
- 내부 컨텐츠 구조 변경 없음

### 패턴 학습
**인라인 스타일로의 전환**:
- 모달 스타일 (Surface + 라운드 코너) → 그리드 스타일 (평탄한 Column)
- modifier 체이닝: `modifier.fillMaxWidth().padding(...)`
- 중첩된 Column의 padding을 부모 Column으로 통합

### 빌드 검증
- `./gradlew assembleDebug` ✅
- `grep -c "Surface("` → 0 ✅
- `grep -c "RoundedCornerShape(topStart"` → 0 ✅

### 커밋
- `refactor(pedalboard): PedalboardInfoEditor Surface 제거, 인라인 스타일로 변경`


## Task 2: PedalSlot 삭제 버튼 정중앙 배치 및 색상 변경

### 변경 내용
- `Alignment.TopEnd` → `Alignment.Center` (라인 137)
- `.padding(4.dp)` 제거 (중앙 배치에서 불필요)
- `MaterialTheme.colorScheme.error` → `Color.Black.copy(alpha = 0.5f)` (라인 141)
- `MaterialTheme.colorScheme.onError` → `Color.White` (라인 151)

### 시각적 효과
- 삭제 버튼이 페달 정중앙에 위치하여 더 눈에 띄움
- 반투명 검은색 배경으로 우아한 오버레이 효과
- 흰색 아이콘으로 명확한 대비

### 빌드 검증
- `./gradlew assembleDebug` ✅ (성공, 빌드 시간 ~1초)
- 그렙 확인:
  - `Alignment.Center` (라인 137) ✅
  - `Color.Black.copy(alpha = 0.5f)` (라인 141) ✅
  - `tint = Color.White` (라인 151) ✅

### 패턴 학습
**Compose 정렬과 패딩**:
- `Alignment.TopEnd` + `padding(4.dp)` = 우상단 오프셋 배치
- `Alignment.Center` = 패딩 없이 정확한 중앙 정렬
- 부모 Box의 `contentAlignment`와 자식 `align()` 수정자의 조합으로 상대 위치 제어

