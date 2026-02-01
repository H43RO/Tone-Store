package com.haero.tonestore.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.presentation.ui.components.PedalColorUtils
import com.haero.tonestore.ui.components.LocalEmberGlassTheme

/**
 * 톤 세팅 카드에서 페달 미리보기를 표시하는 Glass 스타일 컴포넌트
 * 최대 4개의 페달 아이콘을 가로로 표시하고, 초과 페달은 "+N" 표시
 * 페달 고유 색상은 유지하면서 Glassmorphism 효과 적용
 */
@Composable
fun PedalIconsRow(
    pedals: List<Pedal>,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    if (pedals.isEmpty()) {
        Text(
            text = stringResource(R.string.no_pedals_short),
            fontSize = 12.sp,
            color = theme.textSecondary,
            modifier = modifier
        )
        return
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 최대 4개 페달 아이콘 표시
        pedals.take(4).forEach { pedal ->
            GlassPedalIcon(pedal = pedal)
        }

        // 4개 초과 페달의 "+N" 표시
        if (pedals.size > 4) {
            Text(
                text = "+${pedals.size - 4}",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = theme.textSecondary
            )
        }
    }
}

/**
 * 개별 페달 아이콘 - Glassmorphism + 페달 고유 색상 유지
 */
@Composable
private fun GlassPedalIcon(pedal: Pedal, modifier: Modifier = Modifier) {
    val theme = LocalEmberGlassTheme.current

    val pedalColor = if (pedal.color != null) {
        Color(pedal.color)
    } else {
        theme.primary
    }

    val isLightBackground = PedalColorUtils.isLightColor(pedal.color)
    val contentColor = if (pedal.color != null) {
        if (isLightBackground) Color.Black.copy(alpha = 0.8f) else Color.White
    } else {
        Color.White
    }

    // 페달 이름의 첫 3글자
    val abbreviation = pedal.name.take(3).uppercase()

    Box(
        modifier = modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        pedalColor.copy(alpha = 0.5f),
                        pedalColor.copy(alpha = 0.3f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        pedalColor.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = abbreviation,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
