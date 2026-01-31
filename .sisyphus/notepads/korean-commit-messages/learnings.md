# Learnings - korean-commit-messages

## Conventions & Patterns
(To be populated during execution)

## Task 1 Completion: Git Rebase with filter-branch

### Execution Summary (2026-02-01)
- **Method Used**: `git filter-branch --msg-filter` with Python 3 script
- **Approach**: Programmatic message rewriting (not interactive rebase)
- **Reason**: Interactive rebase (`git rebase -i`) requires manual input unsuitable for headless agents
- **Status**: ✅ SUCCESSFUL

### Key Learnings
1. **Shell script limitations**: Initial bash filter script failed because multi-line commit messages weren't handled correctly. Shifted to Python for robust string handling.

2. **Filter-branch idempotency**: 
   - First attempt failed (empty messages) → needed script revision
   - Recovery via `git reflog`: Can restore to previous state without losing work
   - Clean up with `git update-ref -d refs/original/refs/heads/main` before retry

3. **Message filter script requirements**:
   - Must read entire message from stdin (not just first line)
   - Must preserve multi-line format (body + metadata)
   - Mapping must match first-line (subject) exactly
   - Keep original for non-mapped messages (Korean commits)

### Python Filter Script Pattern
```python
#!/usr/bin/env python3
import sys

msg = sys.stdin.read()
lines = msg.split('\n')
subject = lines[0]

# Map subject → new subject
if subject in mappings:
    lines[0] = mappings[subject]
    output = '\n'.join(lines)
else:
    output = msg  # Keep original

sys.stdout.write(output)
```

### Execution Steps (Final)
1. Stash working directory changes (`git stash push -m "pre-rebase"`)
2. Clean up old backup refs (`git update-ref -d refs/original/refs/heads/main`)
3. Create Python filter script with exact mapping
4. Execute: `FILTER_BRANCH_SQUELCH_WARNING=1 git filter-branch --msg-filter 'python3 /tmp/rewrite_msg.py' origin/main..HEAD`
5. Restore stashed changes (`git stash pop`)

### Results
- ✅ 11 commits processed (10 English → Korean, 1 already Korean)
- ✅ All commit hashes changed (expected from filter-branch)
- ✅ Commit order preserved
- ✅ Code content unchanged
- ✅ No merge conflicts

### Verification
```bash
git log --oneline -11
# All 11 commits show Korean descriptions
# ✅ feat(pedal): 디테일 추가 (kept as-is)
# ✅ refactor(pedalboard): 중복 Mini* 프리뷰 컴포넌트 제거
# ... (8 more Korean commits)

git rev-list --count origin/main..HEAD
# Returns 11 (commit count preserved)
```

### Translation Mappings Applied
1. `remove redundant Mini* preview components` → `중복 Mini* 프리뷰 컴포넌트 제거`
2. `use PedalBoardPreview in PedalBoardListScreen` → `PedalBoardListScreen에서 PedalBoardPreview 사용`
3. `create PedalBoardPreview using original components` → `원본 컴포넌트를 사용하는 PedalBoardPreview 생성`
4. `add isEditable and size params to ExpressionPedalZone` → `ExpressionPedalZone에 isEditable, 크기 파라미터 추가`
5. `increase preview size and limit to 2 rows max` → `프리뷰 크기 증가 및 최대 2행 제한`
6. `add dynamic height parameter to MiniExpressionPedalZone` → `MiniExpressionPedalZone에 동적 높이 파라미터 추가`
7. `add visual preview to PedalBoardCard` → `PedalBoardCard에 시각적 프리뷰 추가`
8. `add MiniPedalBoardPreview for visual card preview` → `카드 프리뷰용 MiniPedalBoardPreview 추가`
9. `add MiniExpressionPedalZone for card preview` → `카드 프리뷰용 MiniExpressionPedalZone 추가`
10. `make MiniPedalCard internal for preview reuse` → `프리뷰 재사용을 위해 MiniPedalCard internal로 변경`

### Pattern: Filter-branch vs Interactive Rebase
| Approach | Pros | Cons | Use Case |
|----------|------|------|----------|
| `git filter-branch` | Fully automated, scriptable | Slower, creates backup refs | Headless agents, batch operations |
| `git rebase -i` | Manual control, interactive | Requires user input | Interactive developers |
| `git filter-repo` | Modern, faster | External tool | When available |

### Post-Rebase Actions Remaining
- Task 2: Add "Git Commit Guidelines" section to AGENTS.md
- No push required yet (local commits only)

## Task 2 Completion: Add Git Commit Guidelines to AGENTS.md

### Execution Summary (2026-02-01)
- **Method Used**: Direct edit with Edit tool
- **Location**: After "### Testing" section (line 216 → new section at line 218)
- **Status**: ✅ SUCCESSFUL

### Changes Made
1. ✅ Inserted "### Git Commit Guidelines" section
2. ✅ Added Korean language requirement: **커밋 메세지는 한국어로 작성**
3. ✅ Included format specification: `type(scope): 한국어 설명`
4. ✅ Added commit type table with 7 types (feat, fix, refactor, style, docs, test, chore)
5. ✅ Added 4 rules (scope in English, description in Korean, no period, imperative voice)

### Verification Results
- ✅ Section exists at line 218: `grep -n "Git Commit Guidelines" AGENTS.md`
- ✅ Korean rule exists at line 220: `grep -n "커밋 메세지는 한국어로 작성" AGENTS.md`
- ✅ Commit created: `docs(agents): 커밋 메세지 한국어 작성 가이드라인 추가`
- ✅ Commit hash: `3d416ae`
- ✅ Working directory clean

### Commit Details
```
Commit: 3d416ae
Author: <current user>
Date: 2026-02-01
Message: docs(agents): 커밋 메세지 한국어 작성 가이드라인 추가
Files changed: 2 (AGENTS.md + .sisyphus/boulder.json auto-update)
```

### Key Learnings
1. **AGENTS.md Structure**: Documentation sections follow standard markdown with ### subsections
2. **Commit Sequencing**: First task (Task 1) converted all existing commits to Korean → Task 2 adds the guideline explaining this convention
3. **Korean Documentation**: All technical documentation can follow Korean styling while maintaining English variable/class names (scope)
4. **Clean separation**: docs(agents) for documentation changes keeps commit concerns isolated

### All Tasks Completed
- ✅ Task 1: Convert all 11 commits to Korean messages
- ✅ Task 2: Add Git Commit Guidelines section to AGENTS.md

