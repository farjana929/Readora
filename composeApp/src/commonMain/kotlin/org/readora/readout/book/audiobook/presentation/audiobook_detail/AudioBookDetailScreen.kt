package org.readora.readout.book.audiobook.presentation.audiobook_detail

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.readora.readout.book.audiobook.domain.entity.AudioBook
import org.readora.readout.book.audiobook.presentation.audiobook_detail.components.audioTrackList
import org.readora.readout.core.player.presentation.PlayerAction
import org.readora.readout.core.player.presentation.PlayerViewModel
import org.readora.readout.core.theme.Shapes
import org.readora.readout.core.theme.compactScreenPadding
import org.readora.readout.core.theme.expandedScreenPadding
import org.readora.readout.core.theme.large
import org.readora.readout.core.theme.maxWidthIn
import org.readora.readout.core.theme.medium
import org.readora.readout.core.theme.mediumScreenPadding
import org.readora.readout.core.theme.small
import org.readora.readout.core.ui.components.BookCoverImage
import org.readora.readout.core.ui.components.ErrorView
import org.readora.readout.core.utils.WindowSizes
import readout.composeapp.generated.resources.Res
import readout.composeapp.generated.resources.about_book
import readout.composeapp.generated.resources.audiobook_cover_error_img
import readout.composeapp.generated.resources.book_details
import readout.composeapp.generated.resources.book_summary
import readout.composeapp.generated.resources.bookmark
import readout.composeapp.generated.resources.browse
import readout.composeapp.generated.resources.description_unavailable
import readout.composeapp.generated.resources.go_back
import readout.composeapp.generated.resources.ic_bookmark_filled
import readout.composeapp.generated.resources.ic_bookmark_outlined
import readout.composeapp.generated.resources.ic_browse
import readout.composeapp.generated.resources.ic_headphones
import readout.composeapp.generated.resources.ic_notes
import readout.composeapp.generated.resources.summary_generated_with_ai

@Composable
fun AudioBookDetailScreenRoot(
    viewModel: AudioBookDetailViewModel,
    onBackClick: () -> Unit,
    windowSize: WindowSizes,
    innerPadding: PaddingValues
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val playerViewModel = koinViewModel<PlayerViewModel>()

    AudioBookDetailScreen(
        state = state,
        innerPadding = innerPadding,
        windowSize = windowSize,
        onAction = { action ->
            when (action) {
                is AudioBookDetailAction.OnBackClick -> onBackClick()
                is AudioBookDetailAction.OnPlayAllClick -> playerViewModel.onAction(
                    PlayerAction.OnPlayAllClick(
                        audioUrls = action.allUrls,
                        nowPlaying = state.audioBook?.title ?: "Now Playing Unknown"
                    )
                )

                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioBookDetailScreen(
    state: AudioBookDetailState,
    innerPadding: PaddingValues,
    windowSize: WindowSizes,
    onAction: (AudioBookDetailAction) -> Unit
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

    val urlHandler = LocalUriHandler.current
    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(AudioBookDetailAction.OnTabSelected(pagerState.currentPage))
    }

    Surface(modifier = Modifier.padding(animatedPadding)) {
        Scaffold(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.book_details),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onAction(AudioBookDetailAction.OnBackClick)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(Res.string.go_back),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            state.audioBook?.libriVoxUrl?.let { urlHandler.openUri(it) }
                        }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_browse),
                                contentDescription = stringResource(Res.string.browse),
                            )
                        }
                        IconButton(onClick = {
                            onAction(AudioBookDetailAction.OnSaveClick)
                        }) {
                            Icon(
                                painter = if (state.isSaved) {
                                    painterResource(Res.drawable.ic_bookmark_filled)
                                } else {
                                    painterResource(Res.drawable.ic_bookmark_outlined)
                                },
                                contentDescription = stringResource(Res.string.bookmark),
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->

            val animatedStartPadding by animateDpAsState(
                targetValue = if (windowSize.isExpandedScreen) expandedScreenPadding
                else if (windowSize.isMediumScreen) mediumScreenPadding
                else compactScreenPadding + small,
                animationSpec = tween(durationMillis = 300)
            )

            val animatedEndPadding by animateDpAsState(
                targetValue = if (windowSize.isExpandedScreen) expandedScreenPadding
                else if (windowSize.isMediumScreen) mediumScreenPadding
                else compactScreenPadding + small,
                animationSpec = tween(durationMillis = 300)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(
                        start = animatedStartPadding,
                        top = innerPadding.calculateTopPadding(),
                        end = animatedEndPadding,
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    state.audioBook?.let { book ->
                        if (windowSize.isCompactScreen) {
                            AudioBookDetailCompactLayout(
                                book = book,
                                state = state,
                                onAction = onAction
                            )
                        } else {
                            AudioBookDetailExpandedLayout(
                                book = book,
                                state = state,
                                onAction = onAction
                            )
                        }
                    }
                }

                item {
                    if (state.isSummaryRequest) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(Res.string.book_summary),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .fillMaxWidth()
                                    .padding(top = small)
                            )
                            Text(
                                text = stringResource(Res.string.summary_generated_with_ai),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .fillMaxWidth()
                                    .padding(bottom = medium)
                            )

                            if (state.isSummaryLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(medium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.padding(medium)
                                    )
                                }
                            } else {
                                state.summary?.let { summary ->
                                    Text(
                                        text = summary,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    TabRow(
                        modifier = Modifier
                            .padding(horizontal = small, vertical = small)
                            .clip(Shapes.medium)
                            .widthIn(max = maxWidthIn)
                            .fillMaxWidth(),
                        selectedTabIndex = state.selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        divider = {},
                        indicator = {}
                    ) {
                        Tab(
                            modifier = Modifier,
                            selected = state.selectedTabIndex == 0,
                            unselectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                            onClick = {
                                onAction(AudioBookDetailAction.OnTabSelected(0))
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .fillMaxSize()
                                    .background(
                                        color = if (state.selectedTabIndex == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(vertical = 12.dp),
                                    text = "Audio Tracks"
                                )
                            }
                        }
                        Tab(
                            modifier = Modifier,
                            selected = state.selectedTabIndex == 1,
                            selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = {
                                onAction(AudioBookDetailAction.OnTabSelected(1))
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .fillMaxSize()
                                    .background(
                                        color = if (state.selectedTabIndex == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(vertical = 12.dp),
                                    text = "About Book"
                                )
                            }
                        }
                    }
                }
                if (state.selectedTabIndex == 0) {
                    if (state.audioBookTracksErrorMsg != null) {
                        item {
                            ErrorView(
                                errorMsg = state.audioBookTracksErrorMsg,
                                onRetryClick = {
                                    onAction(AudioBookDetailAction.OnLoadAudioTracks)
                                }
                            )
                        }
                    } else if (state.isAudioBookTracksLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize().animateContentSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(medium)
                                )
                            }
                        }
                    } else {
                        state.audioBookTracks?.let {
                            audioTrackList(
                                audioTracks = it,
                                onPlayClick = { allUrls ->
                                    onAction(AudioBookDetailAction.OnPlayAllClick(allUrls))
                                }
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = stringResource(Res.string.about_book),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.fillMaxWidth().padding(
                                top = large,
                                bottom = small
                            )
                        )

                        if (state.audioBook?.description.isNullOrBlank()) {
                            stringResource(Res.string.description_unavailable)
                        } else {
                            state.audioBook?.description
                        }?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Justify,
                            )
                            Spacer(modifier = Modifier.height(medium))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookTitleAndAuthors(
    title: String,
    authors: List<String>
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = small)
    )
    Text(
        text = "By ${authors.joinToString()}",
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun BookDetailButton(
    state: AudioBookDetailState,
    onAction: (AudioBookDetailAction) -> Unit
) {
    Button(
        onClick = {
            onAction(AudioBookDetailAction.OnSummaryClick)
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_notes),
                contentDescription = "Read Summary",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Read Summary")
        }
    }
    Spacer(modifier = Modifier.width(small))
    Button(
        onClick = {
            onAction(AudioBookDetailAction.OnPlayAllClick(state.audioBookTracks?.mapNotNull { it.listenUrl }
                ?: emptyList()))
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_headphones),
                contentDescription = "Listen",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Play All", maxLines = 1)
        }
    }
}

@Composable
private fun AudioBookDetailExpandedLayout(
    book: AudioBook,
    state: AudioBookDetailState,
    onAction: (AudioBookDetailAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = medium)
            .clip(Shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(large)
    ) {
        book.imgUrl?.let {
            BookCoverImage(
                imgUrl = it,
                height = 200.dp,
                aspectRatio = 1f,
                errorImg = Res.drawable.audiobook_cover_error_img
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(start = large),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(medium))

            BookTitleAndAuthors(
                title = book.title,
                authors = book.authors
            )

            Spacer(modifier = Modifier.height(medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                BookDetailButton(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}


@Composable
private fun AudioBookDetailCompactLayout(
    book: AudioBook,
    state: AudioBookDetailState,
    onAction: (AudioBookDetailAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = small)
            .clip(Shapes.medium)
            .padding(large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        book.imgUrl?.let {
            BookCoverImage(
                imgUrl = it,
                height = 200.dp,
                aspectRatio = 1f,
                errorImg = Res.drawable.audiobook_cover_error_img
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(large))

            BookTitleAndAuthors(
                title = book.title,
                authors = book.authors
            )

            Spacer(modifier = Modifier.height(medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                BookDetailButton(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}