package com.haero.tonestore.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.ui.designsystem.Obsidian

// ============================================================================================
// DATA MOCKS
// ============================================================================================

data class MockToneItem(
    val id: String,
    val title: String,
    val tags: List<String>,
    val date: String,
    val isFavorite: Boolean
)

val mockItems = listOf(
    MockToneItem("1", "Neon Blues Solo", listOf("Blues", "Rock"), "2023.10.24", true),
    MockToneItem("2", "Heavy Metal Riff", listOf("Metal", "Djent", "Heavy"), "2023.10.23", false),
    MockToneItem("3", "Ambient Space", listOf("Ambient", "Clean"), "2023.10.22", true),
    MockToneItem("4", "Funky Rhythm", listOf("Funk", "Pop"), "2023.10.20", false)
)

// ============================================================================================
// OPTION 1: Minimalist & Clean
// - Focus on typography and whitespace
// - Subtle dividers
// - Minimal icons
// ============================================================================================

@Composable
fun MinimalistToneItem(
    item: MockToneItem,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading Icon (Abstract placeholder)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Obsidian.colors.surfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                tint = Obsidian.colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = Obsidian.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Obsidian.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                item.tags.forEachIndexed { index, tag ->
                    Text(
                        text = "#$tag",
                        style = Obsidian.typography.labelSmall,
                        color = Obsidian.colors.textSecondary
                    )
                    if (index < item.tags.lastIndex) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }

        // Action
        IconButton(onClick = {}) {
            Icon(
                imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Filled.MoreVert,
                contentDescription = null,
                tint = if (item.isFavorite) Obsidian.colors.favorite else Obsidian.colors.textMuted
            )
        }
    }
    Divider(color = Obsidian.colors.border, thickness = 0.5.dp)
}

// ============================================================================================
// OPTION 2: Modern Card (Glassmorphic / Gradient)
// - Card based
// - Gradient accents
// - Prominent visuals
// ============================================================================================

@Composable
fun ModernCardToneItem(
    item: MockToneItem,
    onClick: () -> Unit = {}
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Obsidian.colors.surfaceElevated,
            Obsidian.colors.surface
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(Obsidian.radius.lg))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .background(gradientBrush)
                .border(1.dp, Obsidian.colors.border.copy(alpha = 0.5f), RoundedCornerShape(Obsidian.radius.lg))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Indicator
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Obsidian.colors.primary.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.dp, Obsidian.colors.primary.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq, // Alternative icon
                    contentDescription = null,
                    tint = Obsidian.colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = Obsidian.typography.titleMedium,
                    color = Obsidian.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item.tags.take(2).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Obsidian.colors.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Obsidian.colors.border)
                        ) {
                            Text(
                                text = tag,
                                style = Obsidian.typography.labelSmall,
                                color = Obsidian.colors.textSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (item.tags.size > 2) {
                        Text(
                            text = "+${item.tags.size - 2}",
                            style = Obsidian.typography.labelSmall,
                            color = Obsidian.colors.textMuted,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (item.isFavorite) Obsidian.colors.error else Obsidian.colors.textMuted
                )
            }
        }
    }
}

// ============================================================================================
// OPTION 3: Compact Tech
// - High density
// - Technical look
// - Left accent bar
// ============================================================================================

@Composable
fun CompactTechToneItem(
    item: MockToneItem,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Obsidian.colors.surface)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Accent Bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(if (item.isFavorite) Obsidian.colors.primary else Obsidian.colors.border)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Icon
        Icon(
            imageVector = Icons.Rounded.Equalizer,
            contentDescription = null,
            tint = Obsidian.colors.textSecondary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title.uppercase(),
                style = Obsidian.typography.labelLarge.copy(letterSpacing = 1.sp),
                color = Obsidian.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${item.date} | ${item.tags.joinToString(", ")}",
                style = Obsidian.typography.labelSmall,
                color = Obsidian.colors.textMuted
            )
        }

        // Actions
        Row(modifier = Modifier.padding(end = 8.dp)) {
            IconButton(
                onClick = {},
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Obsidian.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ============================================================================================
// PREVIEW SHOWCASE
// ============================================================================================

@Preview(showBackground = true, backgroundColor = 0xFF121212, heightDp = 800)
@Composable
fun HomeListDesignShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)) // Obsidian Dark BG
            .padding(16.dp)
    ) {
        Text(
            text = "Option 1: Minimalist",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Obsidian.colors.bgPrimary, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            items(mockItems) { item ->
                MinimalistToneItem(item = item)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Option 2: Modern Card",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Obsidian.colors.bgPrimary, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            items(mockItems) { item ->
                ModernCardToneItem(item = item)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Option 3: Compact Tech",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Obsidian.colors.bgPrimary, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            items(mockItems) { item ->
                CompactTechToneItem(item = item)
            }
        }
    }
}
