package org.readora.readout.book.openbook.presentation.openbook_detail

import org.readora.readout.book.openbook.domain.entity.Book

sealed interface BookDetailAction {
    data object OnBackClick : BookDetailAction
    data object OnSaveClick : BookDetailAction
    data object OnSummaryClick : BookDetailAction
    data object OnSummaryPlayClick: BookDetailAction
    data class OnSelectedBookChange(val book: Book) : BookDetailAction
}