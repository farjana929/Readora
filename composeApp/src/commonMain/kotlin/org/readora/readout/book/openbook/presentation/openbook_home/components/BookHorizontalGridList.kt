package org.readora.readout.book.openbook.presentation.openbook_home.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import org.readora.readout.book.openbook.domain.entity.Book
import org.readora.readout.core.theme.horizontalGridMaxHeight
import org.readora.readout.core.theme.horizontalScrollGridMaxWidth
import org.readora.readout.core.theme.zero

@Composable
fun BookHorizontalGridList(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {

    val rowState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val bookSize by mutableStateOf(books.size)
    LaunchedEffect(books) { rowState.scrollToItem(0) }

    LazyRow(
        state = rowState,
        modifier = modifier
            .heightIn(max = horizontalGridMaxHeight)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        rowState.scrollBy(-dragAmount)
                    }
                }
            },
        horizontalArrangement = Arrangement.spacedBy(zero)
    ) {
        items(bookSize, key = { books[it].id }, contentType = { "book" }) {
            BookGridItem(
                book = books[it],
                onClick = {
                    onBookClick(books[it])
                },
                modifier = Modifier.width(horizontalScrollGridMaxWidth)
            )
        }
    }
}