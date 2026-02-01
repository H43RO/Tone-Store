package com.haero.tonestore.ui.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ToneStore Obsidian Tangerine Design System
 *
 * 프리미엄 다크 테마 with 밝은 오렌지-앰버 액센트
 * (Amber #F59E0B 와 Copper #BF5A36 사이의 밝은 톤)
 */

// ============================================================
// COLOR SYSTEM
// ============================================================

object ObsidianColors {
    // Background (깊은 다크)
    val bgPrimary = Color(0xFF0C0C0E)
    val bgSecondary = Color(0xFF141416)
    val bgTertiary = Color(0xFF1A1A1F)

    // Surface (카드, 컨테이너)
    val surface = Color(0xFF1C1C21)
    val surfaceElevated = Color(0xFF232328)
    val surfaceHighlight = Color(0xFF2A2A30)

    // Border
    val border = Color(0xFF2A2A30)
    val borderSubtle = Color(0xFF222226)
    val borderFocus = Color(0xFFE88A3C)

    // Primary (Tangerine 계열 - Amber와 Copper 사이 밝은 톤)
    val primary = Color(0xFFE88A3C) // 메인 탱저린 오렌지
    val primaryLight = Color(0xFFF5A54E) // 밝은 앰버 오렌지
    val primaryLighter = Color(0xFFFFBB70) // 더 밝은 피치 오렌지
    val primaryDark = Color(0xFFD4752E) // 약간 어두운 오렌지
    val primaryMuted = Color(0xFFE88A3C).copy(alpha = 0.15f)

    // Secondary (따뜻한 보조 색상)
    val secondary = Color(0xFFC9956A)
    val secondaryLight = Color(0xFFDAAA80)

    // Text
    val textPrimary = Color(0xFFF4F4F5)
    val textSecondary = Color(0xFFA1A1AA)
    val textMuted = Color(0xFF52525B)
    val textDisabled = Color(0xFF3F3F46)

    // Semantic Colors
    val success = Color(0xFF10B981)
    val successMuted = Color(0xFF10B981).copy(alpha = 0.15f)
    val error = Color(0xFFEF4444)
    val errorMuted = Color(0xFFEF4444).copy(alpha = 0.15f)
    val warning = Color(0xFFF59E0B)
    val warningMuted = Color(0xFFF59E0B).copy(alpha = 0.15f)
    val info = Color(0xFF3B82F6)
    val infoMuted = Color(0xFF3B82F6).copy(alpha = 0.15f)

    // Special
    val favorite = Color(0xFFB91C1C)
    val favoriteMuted = Color(0xFFB91C1C).copy(alpha = 0.15f)

    // Pedal Type Colors (조화롭게 조정된 버전)
    object Pedal {
        val overdrive = Color(0xFF27AE60) // 녹색
        val distortion = Color(0xFFE74C3C) // 빨강
        val fuzz = Color(0xFFE67E22) // 주황
        val boost = Color(0xFFF1C40F) // 노랑
        val compressor = Color(0xFF9B59B6) // 보라
        val chorus = Color(0xFF3498DB) // 파랑
        val flanger = Color(0xFF1ABC9C) // 청록
        val phaser = Color(0xFFE91E63) // 핑크
        val tremolo = Color(0xFF00BCD4) // 시안
        val vibrato = Color(0xFF8BC34A) // 연두
        val delay = Color(0xFF673AB7) // 딥퍼플
        val reverb = Color(0xFF2196F3) // 블루
        val looper = Color(0xFF795548) // 갈색
        val eq = Color(0xFF607D8B) // 그레이블루
        val noisegate = Color(0xFF455A64) // 다크그레이
        val tuner = Color(0xFF78909C) // 라이트그레이
        val volumepedal = Color(0xFF37474F) // 차콜
        val wahwah = Color(0xFFFF5722) // 딥오렌지
        val pitchshifter = Color(0xFF9C27B0) // 퍼플
        val octave = Color(0xFF00897B) // 틸
        val harmonizer = Color(0xFFAB47BC) // 라이트퍼플
        val synth = Color(0xFF7C4DFF) // 딥퍼플
        val filter = Color(0xFFFF4081) // 핑크
        val modulation = Color(0xFF26A69A) // 틸
        val expression = Color(0xFF546E7A) // 블루그레이
        val other = Color(0xFF757575) // 그레이
    }
}

// ============================================================
// SPACING SYSTEM
// ============================================================

object ObsidianSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp

    // Component specific
    val cardPadding = 16.dp
    val cardPaddingSmall = 12.dp
    val screenPadding = 16.dp
    val sectionGap = 24.dp
    val itemGap = 12.dp
}

// ============================================================
// RADIUS SYSTEM
// ============================================================

object ObsidianRadius {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val full = 999.dp // For circular shapes

    // Component specific
    val card = 16.dp
    val button = 12.dp
    val input = 12.dp
    val chip = 8.dp
    val bottomNav = 20.dp
    val dialog = 24.dp
}

// ============================================================
// ELEVATION / SHADOW
// ============================================================

object ObsidianElevation {
    val none = 0.dp
    val sm = 2.dp
    val md = 4.dp
    val lg = 8.dp
    val xl = 16.dp
    val xxl = 24.dp

    val card = 8.dp
    val dialog = 24.dp
    val bottomNav = 16.dp
}

// ============================================================
// TYPOGRAPHY
// ============================================================

object ObsidianTypography {
    // Display
    val displayLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    )

    val displayMedium = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 36.sp,
        letterSpacing = (-0.25).sp
    )

    val displaySmall = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp
    )

    // Headline
    val headlineLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp
    )

    val headlineMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp
    )

    val headlineSmall = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.sp
    )

    // Title
    val titleLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 22.sp
    )

    val titleMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp
    )

    val titleSmall = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 18.sp
    )

    // Body
    val bodyLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    )

    val bodyMedium = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp
    )

    val bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp
    )

    // Label
    val labelLarge = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 18.sp
    )

    val labelMedium = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 16.sp
    )

    val labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 14.sp
    )

    // Caption
    val caption = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 14.sp
    )
}

// ============================================================
// COMPOSITION LOCAL
// ============================================================

data class ObsidianThemeData(
    val colors: ObsidianColorsData = ObsidianColorsData(),
    val spacing: ObsidianSpacingData = ObsidianSpacingData(),
    val radius: ObsidianRadiusData = ObsidianRadiusData(),
    val elevation: ObsidianElevationData = ObsidianElevationData(),
    val typography: ObsidianTypographyData = ObsidianTypographyData()
)

data class ObsidianColorsData(
    val bgPrimary: Color = ObsidianColors.bgPrimary,
    val bgSecondary: Color = ObsidianColors.bgSecondary,
    val bgTertiary: Color = ObsidianColors.bgTertiary,
    val surface: Color = ObsidianColors.surface,
    val surfaceElevated: Color = ObsidianColors.surfaceElevated,
    val surfaceHighlight: Color = ObsidianColors.surfaceHighlight,
    val border: Color = ObsidianColors.border,
    val borderSubtle: Color = ObsidianColors.borderSubtle,
    val borderFocus: Color = ObsidianColors.borderFocus,
    val primary: Color = ObsidianColors.primary,
    val primaryLight: Color = ObsidianColors.primaryLight,
    val primaryLighter: Color = ObsidianColors.primaryLighter,
    val primaryDark: Color = ObsidianColors.primaryDark,
    val primaryMuted: Color = ObsidianColors.primaryMuted,
    val secondary: Color = ObsidianColors.secondary,
    val secondaryLight: Color = ObsidianColors.secondaryLight,
    val textPrimary: Color = ObsidianColors.textPrimary,
    val textSecondary: Color = ObsidianColors.textSecondary,
    val textMuted: Color = ObsidianColors.textMuted,
    val textDisabled: Color = ObsidianColors.textDisabled,
    val success: Color = ObsidianColors.success,
    val successMuted: Color = ObsidianColors.successMuted,
    val error: Color = ObsidianColors.error,
    val errorMuted: Color = ObsidianColors.errorMuted,
    val warning: Color = ObsidianColors.warning,
    val warningMuted: Color = ObsidianColors.warningMuted,
    val info: Color = ObsidianColors.info,
    val infoMuted: Color = ObsidianColors.infoMuted,
    val favorite: Color = ObsidianColors.favorite,
    val favoriteMuted: Color = ObsidianColors.favoriteMuted
)

data class ObsidianSpacingData(
    val xs: Dp = ObsidianSpacing.xs,
    val sm: Dp = ObsidianSpacing.sm,
    val md: Dp = ObsidianSpacing.md,
    val lg: Dp = ObsidianSpacing.lg,
    val xl: Dp = ObsidianSpacing.xl,
    val xxl: Dp = ObsidianSpacing.xxl,
    val xxxl: Dp = ObsidianSpacing.xxxl,
    val cardPadding: Dp = ObsidianSpacing.cardPadding,
    val cardPaddingSmall: Dp = ObsidianSpacing.cardPaddingSmall,
    val screenPadding: Dp = ObsidianSpacing.screenPadding,
    val sectionGap: Dp = ObsidianSpacing.sectionGap,
    val itemGap: Dp = ObsidianSpacing.itemGap
)

data class ObsidianRadiusData(
    val xs: Dp = ObsidianRadius.xs,
    val sm: Dp = ObsidianRadius.sm,
    val md: Dp = ObsidianRadius.md,
    val lg: Dp = ObsidianRadius.lg,
    val xl: Dp = ObsidianRadius.xl,
    val xxl: Dp = ObsidianRadius.xxl,
    val full: Dp = ObsidianRadius.full,
    val card: Dp = ObsidianRadius.card,
    val button: Dp = ObsidianRadius.button,
    val input: Dp = ObsidianRadius.input,
    val chip: Dp = ObsidianRadius.chip,
    val bottomNav: Dp = ObsidianRadius.bottomNav,
    val dialog: Dp = ObsidianRadius.dialog
)

data class ObsidianElevationData(
    val none: Dp = ObsidianElevation.none,
    val sm: Dp = ObsidianElevation.sm,
    val md: Dp = ObsidianElevation.md,
    val lg: Dp = ObsidianElevation.lg,
    val xl: Dp = ObsidianElevation.xl,
    val xxl: Dp = ObsidianElevation.xxl,
    val card: Dp = ObsidianElevation.card,
    val dialog: Dp = ObsidianElevation.dialog,
    val bottomNav: Dp = ObsidianElevation.bottomNav
)

data class ObsidianTypographyData(
    val displayLarge: TextStyle = ObsidianTypography.displayLarge,
    val displayMedium: TextStyle = ObsidianTypography.displayMedium,
    val displaySmall: TextStyle = ObsidianTypography.displaySmall,
    val headlineLarge: TextStyle = ObsidianTypography.headlineLarge,
    val headlineMedium: TextStyle = ObsidianTypography.headlineMedium,
    val headlineSmall: TextStyle = ObsidianTypography.headlineSmall,
    val titleLarge: TextStyle = ObsidianTypography.titleLarge,
    val titleMedium: TextStyle = ObsidianTypography.titleMedium,
    val titleSmall: TextStyle = ObsidianTypography.titleSmall,
    val bodyLarge: TextStyle = ObsidianTypography.bodyLarge,
    val bodyMedium: TextStyle = ObsidianTypography.bodyMedium,
    val bodySmall: TextStyle = ObsidianTypography.bodySmall,
    val labelLarge: TextStyle = ObsidianTypography.labelLarge,
    val labelMedium: TextStyle = ObsidianTypography.labelMedium,
    val labelSmall: TextStyle = ObsidianTypography.labelSmall,
    val caption: TextStyle = ObsidianTypography.caption
)

val LocalObsidianTheme = staticCompositionLocalOf { ObsidianThemeData() }

@Composable
fun ObsidianTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalObsidianTheme provides ObsidianThemeData()
    ) {
        content()
    }
}

// Convenience accessor
object Obsidian {
    val colors: ObsidianColorsData
        @Composable
        @ReadOnlyComposable
        get() = LocalObsidianTheme.current.colors

    val spacing: ObsidianSpacingData
        @Composable
        @ReadOnlyComposable
        get() = LocalObsidianTheme.current.spacing

    val radius: ObsidianRadiusData
        @Composable
        @ReadOnlyComposable
        get() = LocalObsidianTheme.current.radius

    val elevation: ObsidianElevationData
        @Composable
        @ReadOnlyComposable
        get() = LocalObsidianTheme.current.elevation

    val typography: ObsidianTypographyData
        @Composable
        @ReadOnlyComposable
        get() = LocalObsidianTheme.current.typography
}
