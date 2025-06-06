package org.readora.readout.book.audiobook.presentation.audiobook_detail.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.readora.readout.book.audiobook.domain.entity.AudioBookTrack
import org.readora.readout.core.theme.Shapes
import org.readora.readout.core.theme.extraSmall
import org.readora.readout.core.theme.medium
import org.readora.readout.core.theme.thin
import readout.composeapp.generated.resources.Res
import readout.composeapp.generated.resources.ic_play_lesson
import readout.composeapp.generated.resources.play

@Composable
fun AudioTrackListItem(
    audioTrack: AudioBookTrack,
    onPlayClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(targetValue = if (isHovered) 1.03f else 1f)

    Surface(
        modifier = modifier
            .padding(extraSmall)
            .clip(Shapes.small)
            .scale(scale)
            .hoverable(interactionSource = interactionSource)
            .clickable(
                onClick = {
                    audioTrack.listenUrl?.let { onPlayClick(it) }
                }
            ),
        tonalElevation = thin,
        shape = Shapes.small
    ) {
        Row(
            modifier = Modifier
                .padding(medium)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(medium)
        ) {
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(100)).background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                audioTrack.sectionNumber?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                audioTrack.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column  {
                    Text(
                        text = "Play Time: ${audioTrack.playtime?.toIntOrNull()?.let { formatPlayTime(it) } ?: "00:00"}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Language : ${audioTrack.language}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }

            IconButton(
                onClick = {
                    audioTrack.listenUrl?.let { onPlayClick(it) }
                },
                content = {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(Res.drawable.ic_play_lesson),
                        contentDescription = stringResource(Res.string.play),
                        tint = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f)
                    )
                }
            )
        }
    }
}

@Composable
private fun formatPlayTime(seconds: Int?): String {
    if (seconds == null) return "00:00"
    val minutes = (seconds % 3600) / 60
    val hours = seconds / 3600
    val remainingSeconds = seconds % 60
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, remainingSeconds)
    } else {
        "00:%02d:%02d".format(minutes, remainingSeconds)
    }
}