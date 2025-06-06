package org.readora.readout.book.openbook.presentation.openbook_home

import org.readora.readout.book.openbook.domain.entity.Book

sealed interface BookHomeAction {
    data class OnSearchQueryChange(val query: String) : BookHomeAction
    data class OnBookClick(val book: Book) : BookHomeAction
    data object OnSettingClick : BookHomeAction
    data object OnViewAllClick : BookHomeAction

    data object ActivateSearchMode : BookHomeAction
    data object DeactivateSearchMode : BookHomeAction

    data object OnLoadBrowseBooks : BookHomeAction
    data class OnSubjectChange(val subject: String) : BookHomeAction

    data object OnShowInfoDialog : BookHomeAction
    data object OnHideInfoDialog : BookHomeAction
}