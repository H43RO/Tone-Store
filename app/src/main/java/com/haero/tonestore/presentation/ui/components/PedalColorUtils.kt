package com.haero.tonestore.presentation.ui.components

import androidx.compose.ui.graphics.Color

/**
 * 페달 색상 관련 유틸리티 함수들
 */
object PedalColorUtils {

    /**
     * 밝은 색상인지 판단 (텍스트 색상 결정용)
     * @param color ARGB Long 값 (null이면 밝은 색으로 간주)
     * @return 밝은 색상이면 true
     */
    fun isLightColor(color: Long?): Boolean {
        if (color == null) return true
        val r = ((color shr 16) and 0xFF).toFloat()
        val g = ((color shr 8) and 0xFF).toFloat()
        val b = (color and 0xFF).toFloat()
        // 상대 휘도 계산
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
        return luminance > 0.5
    }

    /**
     * Color 객체로부터 밝은 색상인지 판단
     */
    fun isLightColor(color: Color): Boolean {
        val luminance = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
        return luminance > 0.5f
    }

    /**
     * 입체감을 위한 Border 색상 계산
     * - 밝은 색상: 약간 어둡게
     * - 어두운 색상: 약간 밝게 (회색 계열)
     * - 극단적인 색상(흰색/검정): 회색 계열
     */
    fun calculateBorderColor(backgroundColor: Color): Color {
        val r = backgroundColor.red
        val g = backgroundColor.green
        val b = backgroundColor.blue

        // 밝기 계산
        val luminance = 0.299f * r + 0.587f * g + 0.114f * b

        return when {
            // 매우 밝은 색 (흰색에 가까움) -> 회색 border
            luminance > 0.9f -> Color(0xFF9E9E9E)
            // 매우 어두운 색 (검정에 가까움) -> 밝은 회색 border
            luminance < 0.1f -> Color(0xFF616161)
            // 밝은 색상 -> 더 진하게 (25% 어둡게)
            luminance > 0.5f -> Color(
                red = (r * 0.75f).coerceIn(0f, 1f),
                green = (g * 0.75f).coerceIn(0f, 1f),
                blue = (b * 0.75f).coerceIn(0f, 1f),
                alpha = 1f
            )
            // 어두운 색상 -> 약간 밝게 (30% 밝게)
            else -> Color(
                red = (r + (1f - r) * 0.3f).coerceIn(0f, 1f),
                green = (g + (1f - g) * 0.3f).coerceIn(0f, 1f),
                blue = (b + (1f - b) * 0.3f).coerceIn(0f, 1f),
                alpha = 1f
            )
        }
    }

    /**
     * 배경색에 따른 콘텐츠 색상 결정 (텍스트, 아이콘 등)
     * @param backgroundColor 배경 색상
     * @param hasCustomColor 사용자 지정 색상 여부
     * @param defaultColor 기본 색상 (사용자 지정 색상이 없을 때)
     */
    fun getContentColor(
        backgroundColor: Color,
        hasCustomColor: Boolean,
        defaultColor: Color
    ): Color {
        return if (hasCustomColor) {
            if (isLightColor(backgroundColor)) Color.Black else Color.White
        } else {
            defaultColor
        }
    }

    /**
     * 색상을 어둡게 만듭니다 (Alpha Blending 방지 및 그라데이션 품질 향상용)
     * @param color 원본 색상
     * @param factor 어둡게 할 비율 (0.0f ~ 1.0f). 1.0f면 변화 없음.
     */
    fun darken(color: Color, factor: Float = 0.8f): Color {
        return Color(
            red = (color.red * factor).coerceIn(0f, 1f),
            green = (color.green * factor).coerceIn(0f, 1f),
            blue = (color.blue * factor).coerceIn(0f, 1f),
            alpha = color.alpha
        )
    }
}
