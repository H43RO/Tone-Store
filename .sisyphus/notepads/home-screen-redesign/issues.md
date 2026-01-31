
## Task 9: SharedElement Transition Blocker

### Issue Identified
SharedTransitionLayout infrastructure (Task 8) was set up, but practical implementation of sharedElement modifiers faces architectural constraints:

1. **HomeScreen Scope Limitation**:
   - HomeScreen is inside HorizontalPager in MainTabScreen
   - HorizontalPager does not provide AnimatedVisibilityScope
   - HomeScreen receives nullable scopes: `sharedTransitionScope: SharedTransitionScope? = null`
   - Cannot apply sharedElement modifiers when scopes are null

2. **DetailScreen Has Scopes**:
   - DetailScreen wrapped in SharedTransitionLayout in NavGraph
   - Has non-null scopes available
   - Can apply shared element modifiers

3. **Mismatch Problem**:
   - Source (HomeScreen card) has no scope → Cannot apply sharedElement
   - Destination (DetailScreen) has scope → Can apply sharedElement
   - **Shared element transitions require BOTH source and destination to have matching sharedElement modifiers**
   - One-sided sharedElement modifier won't create the transition effect

### Root Cause
HorizontalPager composition context doesn't support SharedTransitionLayout scopes. Would need to:
- Restructure MainTabScreen to wrap each page in SharedTransitionLayout
- OR move away from HorizontalPager for tab navigation
- OR accept that shared transitions won't work from Home tab

### Decision
Marking Task 9 as **blocked by architecture constraint**. Possible solutions:
1. **Restructure navigation** (high risk, out of scope)
2. **Accept limitation** - shared transitions only work from Detail→Create or other direct nav routes
3. **Defer to future refactor** when tab navigation can be redesigned

Documented as technical debt. Core redesign (glassmorphism, list/grid, sorting) is complete and functional.
