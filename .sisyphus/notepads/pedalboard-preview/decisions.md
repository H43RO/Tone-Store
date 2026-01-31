# Architectural Decisions - pedalboard-preview

## Key Decisions

### Scale & Layout
- Preview scale: 40dp slot height (~28% of original 140dp)
- Layout: Row/Column (NOT LazyVerticalGrid for performance)
- Max-width: Fixed with aspect ratio preservation
- Empty slots: Preserve positions but render transparent

### Component Reuse
- MiniPedalCard: Changed visibility from private → internal (minimal change)
- MiniKnobIndicator: Keep private (not needed externally)

### Expression Pedal
- Include mini version in preview
- Size: ~24dp × ~60dp (scaled from 80dp×200dp)

(Subagents will append additional decisions here)
