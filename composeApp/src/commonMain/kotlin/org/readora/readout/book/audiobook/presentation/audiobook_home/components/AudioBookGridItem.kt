package org.readora.readout.book.audiobook.presentation.audiobook_home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import org.jetbrains.compose.resources.painterResource
import org.readora.readout.book.audiobook.domain.entity.AudioBook
import org.readora.readout.core.theme.Shapes
import org.readora.readout.core.theme.audioBookCoverAspectRatio
import org.readora.readout.core.theme.extraThin
import org.readora.readout.core.theme.small
import org.readora.readout.core.theme.thin
import org.readora.readout.core.ui.components.CustomAsyncImage
import readout.composeapp.generated.resources.Res
import readout.composeapp.generated.resources.audiobook_cover_error_img

@Composable
fun AudioBookGridItem(
    book: AudioBook,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(targetValue = if (isHovered) 1.05f else 1f)

    Surface(
        modifier = modifier
            .padding(thin)
            .clip(Shapes.small)
            .scale(scale)
            .clickable(onClick = onClick)
            .hoverable(interactionSource = interactionSource)
            .then(Modifier.fillMaxWidth()),
        tonalElevation = thin,
        shape = Shapes.small,
    ) {
        Column(
            modifier = Modifier
                .padding(small)
                .fillMaxWidth()
        ) {
            CustomAsyncImage(
                imageUrl = book.imgUrl,
                contentDescription = book.title,
                errorImage = painterResource(Res.drawable.audiobook_cover_error_img),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(audioBookCoverAspectRatio)
                    .clip(Shapes.small)
            )

            Spacer(modifier = Modifier.height(small))

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(extraThin))

            book.authors.firstOrNull()?.let { authorName ->
                Text(
                    text = "By $authorName",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(extraThin))

            book.totalTime?.let { totalTime ->
                Text(
                    text = "Total Play Time: $totalTime",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}