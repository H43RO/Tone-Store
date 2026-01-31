package com.haero.tonestore.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.presentation.ui.components.PedalColorUtils

/**
 * 톤 세팅 카드에서 페달 미리보기를 표시하는 컴포넌트
 * 최대 4개의 페달 아이콘을 가로로 표시하고, 초과 페달은 "+N" 표시
 *
 * @param pedals 표시할 페달 목록
 * @param modifier Compose Modifier
 */
@Composable
fun PedalIconsRow(
    pedals: List<Pedal>,
    modifier: Modifier = Modifier
) {
    if (pedals.isEmpty()) {
        Text(
            text = "No pedals",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
        return
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 최대 4개 페달 아이콘 표시
        pedals.take(4).forEach { pedal ->
            PedalIcon(pedal = pedal)
        }

        // 4개 초과 페달의 "+N" 표시
        if (pedals.size > 4) {
            Text(
                text = "+${pedals.size - 4}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 개별 페달 아이콘 (24.dp 크기의 컬러 스퀘어 + 3글자 약자)
 */
@Composable
private fun PedalIcon(pedal: Pedal, modifier: Modifier = Modifier) {
    val backgroundColor = if (pedal.color != null) {
        Color(pedal.color)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val isLightBackground = PedalColorUtils.isLightColor(pedal.color)
    val contentColor = if (pedal.color != null) {
        if (isLightBackground) Color.Black else Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    // 페달 이름의 첫 3글자
    val abbreviation = pedal.name.take(3).uppercase()

    Row(
        modifier = modifier
            .size(24.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = abbreviation,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 7.sp,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun PedalIconsRowPreview() {
    MaterialTheme {
        // 5개 페달로 오버플로우 테스트
        PedalIconsRow(
            pedals = listOf(
                Pedal(
                    id = "1",
                    name = "Overdrive",
                    type = PedalType.PRESET,
                    knobs = emptyList(),
                    order = 0,
                    isEnabled = true,
                    color = 0xFFFF6B6B // 빨강
                ),
                Pedal(
                    id = "2",
                    name = "Distortion",
                    type = PedalType.PRESET,
                    knobs = emptyList(),
                    order = 1,
                    isEnabled = true,
                    color = 0xFF4ECDC4 // 청록
                ),
                Pedal(
                    id = "3",
                    name = "Delay",
                    type = PedalType.PRESET,
                    knobs = emptyList(),
                    order = 2,
                    isEnabled = true,
                    color = 0xFFFFE66D // 노랑
                ),
                Pedal(
                    id = "4",
                    name = "Reverb",
                    type = PedalType.PRESET,
                    knobs = emptyList(),
                    order = 3,
                    isEnabled = true,
                    color = 0xFF95E1D3 // 민트
                ),
                Pedal(
                    id = "5",
                    name = "Chorus",
                    type = PedalType.PRESET,
                    knobs = emptyList(),
                    order = 4,
                    isEnabled = true,
                    color = 0xFF9B59B6 // 보라
                )
            )
        )
    }
}

@Preview
@Composable
private fun PedalIconsRowEmptyPreview() {
    MaterialTheme {
        // 빈 페달 리스트
        PedalIconsRow(
            pedals = emptyList()
        )
    }
}
