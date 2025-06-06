package org.readora.readout.book.audiobook.presentation.audiobook_detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import org.readora.readout.book.audiobook.domain.entity.AudioBookTrack
import org.readora.readout.core.theme.maxWidthIn
import org.readora.readout.core.theme.medium

fun LazyListScope.audioTrackList(
    audioTracks: List<AudioBookTrack>,
    onPlayClick: (List<String>) -> Unit,
) {
    items(
        items = audioTracks,
        key = { it.id }
    ) { audioTrack ->

        val currentIndex = audioTracks.indexOf(audioTrack)
        AudioTrackListItem(
            audioTrack = audioTrack,
            onPlayClick = {
                val urlsFromIndex = audioTracks.drop(currentIndex).mapNotNull { it.listenUrl }
                onPlayClick(urlsFromIndex)
            },
            modifier = Modifier
                .widthIn(max = maxWidthIn)
                .fillMaxWidth()
                .padding(horizontal = medium),
        )
    }
}