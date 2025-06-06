package org.readora.readout.book.audiobook.presentation.audiobook_detail

import org.readora.readout.book.audiobook.domain.entity.AudioBook
import org.readora.readout.book.audiobook.domain.entity.AudioBookTrack
import org.readora.readout.core.utils.UiText

data class AudioBookDetailState(
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val audioBook: AudioBook? = null,
    val selectedTabIndex: Int = 0,

    val audioBookTracksErrorMsg: UiText? = null,
    val isAudioBookTracksLoading: Boolean = false,
    val audioBookTracks: List<AudioBookTrack>? = null,

    val isSummaryRequest: Boolean = false,
    val isSummaryLoading: Boolean = false,
    val isSummaryAvailable: Boolean = true,
    val summaryErrorMsg: UiText? = null,
    val summary: String? = null,
    val shortSummary: String? = null
)