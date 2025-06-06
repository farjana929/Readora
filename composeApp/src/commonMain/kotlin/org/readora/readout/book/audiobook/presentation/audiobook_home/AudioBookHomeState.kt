package org.readora.readout.book.audiobook.presentation.audiobook_home

import org.readora.readout.book.audiobook.domain.entity.AudioBook
import org.readora.readout.core.utils.UiText

data class AudioBookHomeState(
    val searchQuery: String = "",
    val isSearchLoading: Boolean = false,
    val isSearchActive: Boolean = false,
    val searchErrorMsg: UiText? = null,
    val searchResult: List<AudioBook> = emptyList(),

    val isBrowseLoading: Boolean = false,
    val isEndReached: Boolean = false,
    val browseErrorMsg: UiText? = null,
    val browseAudioBooks: List<AudioBook> = emptyList(),
    val genre: String? = null,
    val offset: Int = 0,

    val savedBooks: List<AudioBook> = emptyList(),
    val showDialog: Boolean = false
)