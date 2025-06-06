package org.readora.readout.book.openbook.presentation.openbook_home

import org.readora.readout.book.openbook.domain.entity.Book
import org.readora.readout.core.utils.UiText

data class BookHomeState(
    val searchQuery: String = "",
    val searchResult: List<Book> = emptyList(),

    val isSearchActive: Boolean = false,
    val isSearchLoading: Boolean = false,
    val searchErrorMsg: UiText? = null,

    val browseBooks: List<Book> = emptyList(),
    val isBrowseLoading: Boolean = false,
    val browseErrorMsg: UiText? = null,
    val endReached: Boolean = false,
    val subject: String? = "english",
    val offset: Int = 0,
    val isBrowseShimmerEffectVisible: Boolean = true,

    val savedBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: UiText? = null,

    val showDialog: Boolean = false
)