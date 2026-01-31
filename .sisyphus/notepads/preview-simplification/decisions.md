# Architectural Decisions - Preview Simplification

## [2026-01-31] Decision to add parameters instead of new Mini* components
- **Context**: User tested preview and found original size acceptable
- **Decision**: Add isEditable, width, height params to ExpressionPedalZone with defaults
- **Rationale**: Simpler codebase, reuse existing components, backwards compatible
- **Consequences**: ExpressionPedalZone becomes more flexible for preview use

## [2026-01-31] Keep MiniPedalCard
- **Context**: MiniPedalCard is used by both PedalSlot and preview
- **Decision**: Keep as internal in PedalSlot.kt
- **Rationale**: Still needed by PedalSlot, accessible from same package
