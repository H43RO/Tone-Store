package com.haero.tonestore.presentation.ui.preset_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.haero.tonestore.ui.designsystem.*

@Composable
fun CommentInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean,
    isEditing: Boolean,
    onCancelEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    ObsidianSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp), // 하단 고정형이므로 상단만 곡률을 주거나 평평하게
        elevation = Obsidian.elevation.lg,
        hasBorder = true
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditing) {
                ObsidianIconButton(
                    onClick = onCancelEdit,
                    icon = Icons.Default.Close,
                    size = 40.dp,
                    iconSize = 20.dp,
                    tint = Obsidian.colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(Obsidian.radius.input))
                    .background(Obsidian.colors.bgSecondary)
                    .border(
                        1.dp,
                        Obsidian.colors.border,
                        RoundedCornerShape(Obsidian.radius.input)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = Obsidian.typography.bodyMedium.copy(
                        color = Obsidian.colors.textPrimary
                    ),
                    cursorBrush = SolidColor(Obsidian.colors.primary),
                    maxLines = 4,
                    decorationBox = { innerTextField ->
                        Box {
                            if (text.isEmpty()) {
                                Text(
                                    text = if (isEditing) "댓글 수정..." else "댓글을 입력하세요...",
                                    style = Obsidian.typography.bodyMedium,
                                    color = Obsidian.colors.textMuted
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            ObsidianIconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank() && !isLoading,
                icon = Icons.AutoMirrored.Filled.Send,
                size = 48.dp,
                iconSize = 24.dp,
                tint = if (text.isNotBlank()) Obsidian.colors.primary else Obsidian.colors.textDisabled
            )
        }
    }
}
