package com.haero.tonestore.presentation.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.AmpSetting
import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.domain.model.GuitarSetting
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalBoard
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.domain.model.PickupPosition
import com.haero.tonestore.domain.model.ToneSetting

@Composable
fun GridToneSettingCard(
    toneSetting: ToneSetting,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    sharedElementKey: String,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val favoriteColor by animateColorAsState(
        targetValue = if (toneSetting.isFavorite) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "gridFavoriteColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "gridCardScale"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = toneSetting.songName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                PedalIconsRow(
                    pedals = toneSetting.pedalBoard.pedals,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = if (toneSetting.isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = stringResource(R.string.favorite),
                    tint = favoriteColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun GridToneSettingCardPreview() {
    MaterialTheme {
        GridToneSettingCard(
            toneSetting = ToneSetting(
                id = "preview-1",
                songName = "Sweet Child O' Mine",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                pedalBoard = PedalBoard(
                    pedals = listOf(
                        Pedal(
                            id = "1",
                            name = "Overdrive",
                            type = PedalType.PRESET,
                            knobs = emptyList(),
                            order = 0,
                            isEnabled = true,
                            color = 0xFFFF6B6B
                        ),
                        Pedal(
                            id = "2",
                            name = "Delay",
                            type = PedalType.PRESET,
                            knobs = emptyList(),
                            order = 1,
                            isEnabled = true,
                            color = 0xFFFFE66D
                        ),
                        Pedal(
                            id = "3",
                            name = "Reverb",
                            type = PedalType.PRESET,
                            knobs = emptyList(),
                            order = 2,
                            isEnabled = true,
                            color = 0xFF95E1D3
                        )
                    )
                ),
                ampSetting = AmpSetting(
                    gain = 5f,
                    bass = 5f,
                    middle = 5f,
                    treble = 5f,
                    presence = 5f,
                    reverb = 3f,
                    masterVolume = 4f,
                    ampModel = "Marshall JCM800"
                ),
                guitarSetting = GuitarSetting(
                    pickupSelector = PickupPosition.BRIDGE,
                    toneKnob = 7f,
                    volumeKnob = 10f,
                    guitarModel = "Gibson Les Paul"
                ),
                isFavorite = true,
                tags = listOf(GenreTag.ROCK)
            ),
            onClick = {},
            onFavoriteClick = {},
            sharedElementKey = "preview-1"
        )
    }
}
