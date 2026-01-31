# 커밋 메세지 한국어 변환 및 AGENTS.md 룰 추가

## TL;DR

> **Quick Summary**: Push되지 않은 11개 커밋의 영어 메세지를 한국어로 변환하고, AGENTS.md에 "커밋 메세지는 한국어로 작성" 룰 추가
> 
> **Deliverables**:
> - 10개 영어 커밋 메세지 → 한국어로 rebase
> - AGENTS.md에 Git Commit 가이드라인 섹션 추가
> 
> **Estimated Effort**: Quick (~15분)
> **Parallel Execution**: NO - sequential (rebase 후 AGENTS.md 수정)
> **Critical Path**: Task 1 → Task 2

---

## Context

### Original Request
사용자: "지금까지 커밋한 내용들, 만약 커밋 메세지가 영어로 적혀있는 것들은 전부 한국어로 바꿔줘. 그리고 앞으로 '커밋 메세지는 한국어로 작성'을 룰로 추가해주면 좋겠어"

### Interview Summary
**핵심 포인트**:
- Push 안 된 11개 커밋만 변경 (안전한 범위)
- 혼자 사용하는 레포지토리 → force push 가능
- AGENTS.md에 한국어 커밋 룰 추가 요청

**상황 분석**:
- 현재 `origin/main`보다 11 commits ahead
- 11개 중 10개가 영어, 1개는 이미 한국어
- `git rebase -i`로 메세지 변경 필요

---

## Work Objectives

### Core Objective
영어 커밋 메세지를 한국어로 변환하고, 향후 커밋 메세지 작성 규칙을 문서화

### Concrete Deliverables
1. 10개 영어 커밋 메세지 → 한국어로 변환 (rebase)
2. AGENTS.md에 "Git Commit Guidelines" 섹션 추가

### Definition of Done
- [x] `git log --oneline -11` 결과가 모두 한국어
- [x] AGENTS.md에 커밋 메세지 가이드라인 존재
- [x] 빌드 및 코드에 영향 없음 (커밋 메세지만 변경)

### Must Have
- 커밋 메세지 형식 유지: `type(scope): 설명`
- 기존 커밋 순서 유지
- AGENTS.md에 한국어 커밋 룰 명시

### Must NOT Have (Guardrails)
- ❌ NO 코드 변경 (커밋 메세지만 변경)
- ❌ NO 커밋 순서 변경
- ❌ NO squash 또는 커밋 합치기
- ❌ NO 이미 push된 커밋 변경

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: N/A (git 작업)
- **User wants tests**: NO
- **Framework**: N/A

### Automated Verification Only

```bash
# Task 1 완료 후 - 모든 커밋이 한국어인지 확인
git log --oneline -11 | grep -E "^[a-f0-9]+ (feat|refactor|fix|chore|docs)\([^)]+\): [가-힣]"
# Assert: 11개 모두 매칭 (한국어 설명)

# Task 2 완료 후 - AGENTS.md에 커밋 가이드라인 존재
grep -n "커밋 메세지" AGENTS.md
# Assert: 매칭 존재

grep -n "한국어" AGENTS.md
# Assert: 매칭 존재
```

---

## Execution Strategy

### Sequential Execution

```
Task 1: Git rebase로 커밋 메세지 변환
    ↓
Task 2: AGENTS.md에 커밋 가이드라인 추가
```

### Dependency Matrix

| Task | Depends On | Blocks |
|------|------------|--------|
| 1 | None | 2 |
| 2 | 1 | None |

---

## TODOs

- [x] 1. Git rebase로 영어 커밋 메세지를 한국어로 변환

  **What to do**:
  - `git rebase -i origin/main` 실행
  - 10개 영어 커밋의 메세지를 한국어로 변경 (reword)
  - 변환 매핑:
    ```
    26a7435 refactor(pedalboard): remove redundant Mini* preview components
    → refactor(pedalboard): 중복 Mini* 프리뷰 컴포넌트 제거
    
    42fa9ff refactor(pedalboard): use PedalBoardPreview in PedalBoardListScreen
    → refactor(pedalboard): PedalBoardListScreen에서 PedalBoardPreview 사용
    
    0068367 feat(pedalboard): create PedalBoardPreview using original components
    → feat(pedalboard): 원본 컴포넌트를 사용하는 PedalBoardPreview 생성
    
    60fbd38 refactor(pedalboard): add isEditable and size params to ExpressionPedalZone
    → refactor(pedalboard): ExpressionPedalZone에 isEditable, 크기 파라미터 추가
    
    9dcdc6b feat(pedalboard): increase preview size and limit to 2 rows max
    → feat(pedalboard): 프리뷰 크기 증가 및 최대 2행 제한
    
    fad4149 refactor(pedalboard): add dynamic height parameter to MiniExpressionPedalZone
    → refactor(pedalboard): MiniExpressionPedalZone에 동적 높이 파라미터 추가
    
    4893712 feat(pedalboard): add visual preview to PedalBoardCard
    → feat(pedalboard): PedalBoardCard에 시각적 프리뷰 추가
    
    f25cd40 feat(pedalboard): add MiniPedalBoardPreview for visual card preview
    → feat(pedalboard): 카드 프리뷰용 MiniPedalBoardPreview 추가
    
    b09106e feat(pedalboard): add MiniExpressionPedalZone for card preview
    → feat(pedalboard): 카드 프리뷰용 MiniExpressionPedalZone 추가
    
    a1d8187 refactor(pedalboard): make MiniPedalCard internal for preview reuse
    → refactor(pedalboard): 프리뷰 재사용을 위해 MiniPedalCard internal로 변경
    ```
  - 이미 한국어인 커밋(`feat(pedal): 디테일 추가`)은 그대로 유지

  **Must NOT do**:
  - 커밋 순서 변경 금지
  - 커밋 합치기(squash) 금지
  - 코드 내용 변경 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `["git-master"]`
    - `git-master`: rebase 작업에 필수
  - Reason: Git rebase 전문 지식 필요

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocks**: Task 2
  - **Blocked By**: None

  **References**:
  - Git history: `git log --oneline -11`
  - Remote tracking: `git status` (11 commits ahead)

  **Execution Method**:
  
  Interactive rebase는 자동화가 어려우므로, `git filter-branch` 또는 `git rebase --exec` 대신
  **순차적 `git commit --amend`** 접근법 사용:
  
  ```bash
  # 방법: git rebase -i 대신 스크립트로 처리
  # 각 커밋을 checkout하고 amend하는 방식은 복잡하므로
  # git filter-repo 또는 수동 rebase 필요
  
  # 권장: git rebase -i origin/main 후 수동으로 reword
  # 또는: GIT_SEQUENCE_EDITOR 환경변수 활용
  ```

  **Acceptance Criteria**:
  ```bash
  # 모든 커밋이 한국어 설명을 가지는지 확인
  git log --oneline -11
  # Assert: 모든 커밋 메세지가 한국어 (type(scope): 한국어설명 형식)
  
  # 커밋 개수 유지 확인
  git rev-list --count origin/main..HEAD
  # Assert: 11
  ```

  **Commit**: NO (rebase 작업이므로 별도 커밋 불필요)

---

- [x] 2. AGENTS.md에 Git Commit 가이드라인 추가

  **What to do**:
  - AGENTS.md의 "Code Style Guidelines" 섹션 뒤에 "Git Commit Guidelines" 섹션 추가
  - 한국어 커밋 메세지 룰 명시
  - Conventional Commits 형식 유지하면서 설명만 한국어로

  **Must NOT do**:
  - 기존 AGENTS.md 내용 삭제 금지
  - 다른 섹션 수정 금지

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `[]`
  - Reason: 단순 문서 추가

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Blocked By**: Task 1

  **References**:
  - `AGENTS.md:72-96` - Code Style Guidelines 섹션 (이 뒤에 추가)

  **Code to Add** (line 96 이후):

  ```markdown
  ### Git Commit Guidelines
  
  **커밋 메세지는 한국어로 작성**
  
  형식: `type(scope): 한국어 설명`
  
  | Type | 용도 | 예시 |
  |------|------|------|
  | `feat` | 새로운 기능 추가 | `feat(home): 홈 화면에 검색 기능 추가` |
  | `fix` | 버그 수정 | `fix(pedal): 노브 값 저장 안 되는 문제 수정` |
  | `refactor` | 리팩토링 (기능 변화 없음) | `refactor(data): Repository 패턴 적용` |
  | `style` | 코드 포맷팅, 세미콜론 등 | `style(ui): ktlint 포맷 적용` |
  | `docs` | 문서 수정 | `docs(readme): 설치 방법 업데이트` |
  | `test` | 테스트 코드 | `test(usecase): GetAllToneSettingsUseCase 테스트 추가` |
  | `chore` | 빌드, 설정 등 | `chore(gradle): 의존성 버전 업데이트` |
  
  **규칙**:
  - scope는 영어로 유지 (패키지/모듈명)
  - 설명은 한국어로 작성
  - 마침표 없이 작성
  - 명령형으로 작성 ("추가", "수정", "제거" 등)
  ```

  **Acceptance Criteria**:
  ```bash
  grep -n "Git Commit Guidelines" AGENTS.md
  # Assert: 섹션 제목 존재
  
  grep -n "커밋 메세지는 한국어로 작성" AGENTS.md
  # Assert: 핵심 룰 존재
  
  grep -n "type(scope): 한국어" AGENTS.md
  # Assert: 형식 설명 존재
  ```

  **Commit**: YES
  - Message: `docs(agents): 커밋 메세지 한국어 작성 가이드라인 추가`
  - Files: `AGENTS.md`

---

## Commit Strategy

| After Task | Message | Files |
|------------|---------|-------|
| 1 | (rebase - 기존 커밋 메세지 변경) | N/A |
| 2 | `docs(agents): 커밋 메세지 한국어 작성 가이드라인 추가` | AGENTS.md |

---

## Success Criteria

### Verification Commands
```bash
# 모든 로컬 커밋이 한국어인지 확인
git log --oneline -12  # 11개 기존 + 1개 AGENTS.md 커밋
# Expected: 모든 설명이 한국어

# AGENTS.md 가이드라인 확인
grep "한국어" AGENTS.md
# Expected: 커밋 메세지 관련 내용 존재
```

### Final Checklist
- [x] 10개 영어 커밋 → 한국어로 변환됨
- [x] 1개 기존 한국어 커밋 유지됨
- [x] AGENTS.md에 Git Commit Guidelines 섹션 추가됨
- [x] 코드 변경 없음 (커밋 메세지만 변경)
