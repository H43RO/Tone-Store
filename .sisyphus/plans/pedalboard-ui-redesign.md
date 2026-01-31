# Pedalboard UI Redesign Plan

**Goal**: Complete UI redesign of PedalBoard editing screen per user feedback

**Plan Name**: pedalboard-ui-redesign
**Created**: 2026-02-01T18:02:06Z
**Status**: Complete

---

## Tasks

- [x] ui-1: 페달보드 정보 편집 UI를 그리드 아래로 이동
- [x] ui-2: 페달 선택 시 CrossFade로 페달 편집 UI 전환 구현
- [x] ui-3: 페달 편집 UI 개선 (TextField 너비, X 아이콘, 라벨 제거, 닫기 버튼 하이라이트)
- [x] ui-4: X 버튼으로 페달보드 정보 편집 UI로 복귀 (CrossFade)
- [x] ui-5: 버그 수정: 다른 페달 선택 시 이름/노브 정보 갱신 안 되는 문제
- [x] ui-6: 레이아웃 축소 시 페달 유실 방지 툴팁 구현

---

## Implementation Details

### UI Architecture Changes
- Move name TextField + LayoutStepper below grid
- Replace AnimatedVisibility with Crossfade for smooth transitions
- Create PedalboardInfoEditor component to encapsulate info UI

### Component Modifications
1. **PedalBoardScreen.kt**: Restructure layout hierarchy
2. **InlinePedalEditor.kt**: Improve styling (wider TextField, X icon, no labels, circular close button)
3. **LayoutStepper.kt**: Add pedal loss validation

### Bug Fixes
1. Fix pedal data not refreshing when switching selection
2. Prevent layout resize that would cause pedal loss

---

## Verification Criteria
- [x] Build passes with zero errors
- [x] UI layout matches requirements
- [x] CrossFade transitions smooth
- [x] Bug fixes verified
- [x] No regressions
