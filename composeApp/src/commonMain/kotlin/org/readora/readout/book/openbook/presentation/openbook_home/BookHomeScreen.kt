package org.readora.readout.book.openbook.presentation.openbook_home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.readora.readout.book.openbook.domain.entity.Book
import org.readora.readout.book.openbook.presentation.openbook_home.components.BookGridItem
import org.readora.readout.book.openbook.presentation.openbook_home.components.BookHorizontalGridList
import org.readora.readout.book.openbook.presentation.openbook_home.components.BookSearchResult
import org.readora.readout.core.theme.Shapes
import org.readora.readout.core.theme.compactFeedWidth
import org.readora.readout.core.theme.compactScreenPadding
import org.readora.readout.core.theme.expandedFeedWidth
import org.readora.readout.core.theme.expandedScreenPadding
import org.readora.readout.core.theme.mediumFeedWidth
import org.readora.readout.core.theme.mediumScreenPadding
import org.readora.readout.core.theme.small
import org.readora.readout.core.theme.thin
import org.readora.readout.core.theme.zero
import org.readora.readout.core.ui.components.EmbeddedSearchBar
import org.readora.readout.core.ui.components.ErrorView
import org.readora.readout.core.ui.components.FullScreenDialog
import org.readora.readout.core.ui.feed.FeedTitleWithButton
import org.readora.readout.core.ui.feed.FeedTitleWithDropdown
import org.readora.readout.core.ui.feed.Feed
import org.readora.readout.core.ui.feed.row
import org.readora.readout.core.ui.feed.title
import org.readora.readout.core.utils.WindowSizes
import org.readora.readout.core.utils.openLibrary_book_subject
import readout.composeapp.generated.resources.Res
import readout.composeapp.generated.resources.disclaimer_text
import readout.composeapp.generated.resources.info
import readout.composeapp.generated.resources.open_library
import readout.composeapp.generated.resources.saved_books
import readout.composeapp.generated.resources.search
import readout.composeapp.generated.resources.setting
import readout.composeapp.generated.resources.view_all

@Composable
fun BookHomeScreenRoot(
    viewModel: BookHomeViewModel = koinViewModel(),
    onBookClick: (Book) -> Unit,
    onSettingClick: () -> Unit,
    onViewAllClick: () -> Unit,
    innerPadding: PaddingValues,
    windowSize: WindowSizes
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BookHomeScreen(
        state = state,
        innerPadding = innerPadding,
        windowSize = windowSize,
        onAction = { action ->
            when (action) {
                is BookHomeAction.OnBookClick -> onBookClick(action.book)
                is BookHomeAction.OnSettingClick -> onSettingClick()
                is BookHomeAction.OnViewAllClick -> onViewAllClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )

    if (state.showDialog) {
        ShowDialog(
            onDismiss = {
                viewModel.onAction(BookHomeAction.OnHideInfoDialog)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookHomeScreen(
    state: BookHomeState,
    innerPadding: PaddingValues,
    windowSize: WindowSizes,
    onAction: (BookHomeAction) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val startPadding by animateDpAsState(
        targetValue = innerPadding.calculateStartPadding(
            LayoutDirection.Ltr
        )
    )
    val topPadding by animateDpAsState(targetValue = innerPadding.calculateTopPadding())
    val endPadding by animateDpAsState(
        targetValue = innerPadding.calculateEndPadding(
            LayoutDirection.Ltr
        )
    )
    val bottomPadding by animateDpAsState(targetValue = innerPadding.calculateBottomPadding())
    val animatedPadding = PaddingValues(
        start = startPadding,
        top = topPadding,
        end = endPadding,
        bottom = bottomPadding
    )

    val gridState = rememberLazyGridState()
    val bookSize by mutableStateOf(state.browseBooks.size)
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(modifier = Modifier.padding(animatedPadding)) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.open_library),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onAction(BookHomeAction.OnShowInfoDialog)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(Res.string.info),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            onAction(BookHomeAction.ActivateSearchMode)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(Res.string.search),
                            )
                        }
                        if (windowSize.isCompactScreen) {
                            IconButton(onClick = {
                                onAction(BookHomeAction.OnSettingClick)
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = stringResource(Res.string.setting),
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
                if (state.isSearchActive) {
                    EmbeddedSearchBar(
                        query = state.searchQuery,
                        onQueryChange = { query ->
                            onAction(BookHomeAction.OnSearchQueryChange(query))
                        },
                        onSearch = {
                            keyboardController?.hide()
                        },
                        content = {
                            BookSearchResult(
                                state = state,
                                onBookClick = { book ->
                                    keyboardController?.hide()
                                    onAction(BookHomeAction.OnBookClick(book))
                                }
                            )
                        },
                        onBack = {
                            onAction(BookHomeAction.DeactivateSearchMode)
                        },
                        isActive = true
                    )
                }
            }
        ) { innerPadding ->

            val columns = when {
                windowSize.isExpandedScreen -> GridCells.Adaptive(expandedFeedWidth)
                windowSize.isMediumScreen -> GridCells.Adaptive(mediumFeedWidth)
                else -> GridCells.Adaptive(compactFeedWidth)
            }

            val contentPadding = PaddingValues(horizontal = thin, vertical = thin)
            val verticalArrangement =
                if (!windowSize.isCompactScreen) Arrangement.spacedBy(small) else Arrangement.spacedBy(
                    zero
                )
            val horizontalArrangement =
                if (!windowSize.isCompactScreen) Arrangement.spacedBy(small) else Arrangement.spacedBy(
                    zero
                )

            val animatedStartPadding by animateDpAsState(
                targetValue = if (windowSize.isExpandedScreen) expandedScreenPadding
                else if (windowSize.isMediumScreen) mediumScreenPadding
                else compactScreenPadding,
                animationSpec = tween(durationMillis = 300)
            )

            val animatedEndPadding by animateDpAsState(
                targetValue = if (windowSize.isExpandedScreen) expandedScreenPadding
                else if (windowSize.isMediumScreen) mediumScreenPadding
                else compactScreenPadding,
                animationSpec = tween(durationMillis = 300)
            )

            Feed(
                modifier = Modifier.padding(
                    start = animatedStartPadding,
                    top = innerPadding.calculateTopPadding(),
                    end = animatedEndPadding,
                    bottom = innerPadding.calculateBottomPadding()
                ).fillMaxSize(),
                columns = columns,
                state = gridState,
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
                horizontalArrangement = horizontalArrangement
            ) {

                if (state.savedBooks.isNotEmpty()) {
                    title(contentType = "saved-book-title") {
                        FeedTitleWithButton(
                            title = stringResource(Res.string.saved_books),
                            btnText = stringResource(Res.string.view_all),
                            onClick = {
                                onAction(BookHomeAction.OnViewAllClick)
                            }
                        )
                    }
                    row(contentType = "saved-books") {
                        BookHorizontalGridList(
                            books = state.savedBooks,
                            onBookClick = {
                                onAction(BookHomeAction.OnBookClick(it))
                            }
                        )
                    }
                }

                title(contentType = "browse-title") {
                    FeedTitleWithDropdown(
                        title = "Browse",
                        dropDownList = openLibrary_book_subject,
                        onItemSelected = { selectedItem ->
                            onAction(BookHomeAction.OnSubjectChange(selectedItem.lowercase()))
                        }
                    )
                }

                items(
                    count = bookSize,
                    key = { index -> state.browseBooks[index].id }
                ) { index ->
                    val book = state.browseBooks[index]

                    if (index == bookSize - 1 && !state.isBrowseLoading && !state.endReached && state.browseErrorMsg == null) {
                        onAction(BookHomeAction.OnLoadBrowseBooks)
                    }
                    BookGridItem(
                        book = book,
                        onClick = {
                            onAction(BookHomeAction.OnBookClick(book))
                        }
                    )
                }

                if (state.browseErrorMsg != null) {
                    row(contentType = "error") {
                        ErrorView(
                            errorMsg = state.browseErrorMsg,
                            onRetryClick = {
                                onAction(BookHomeAction.OnLoadBrowseBooks)
                            }
                        )
                    }
                } else if (state.isBrowseLoading) {
                    row(contentType = "loading") {
                        Box(
                            modifier = Modifier.fillMaxSize().animateContentSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(small)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ShowDialog(
    onDismiss: () -> Unit,
) {
    FullScreenDialog(
        onDismissRequest = { onDismiss() },
        title = "Disclaimer",
        modifier = Modifier.padding(16.dp).clip(Shapes.medium),
        actions = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { onDismiss() }) {
                    Text("Close")
                }
            }
        },
        content = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier.padding(vertical = small),
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(Res.string.disclaimer_text)
                )
            }
        }
    )
}