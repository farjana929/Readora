package org.readora.readout.book.openbook.data.mappers

import org.readora.readout.book.openbook.data.database.OpenBookEntity
import org.readora.readout.book.openbook.data.dto.SearchedBookDto
import org.readora.readout.book.openbook.domain.entity.Book

fun SearchedBookDto.toBook(): Book {
    val authorNames2ndApproach = authors?.map { it.name }
    return Book(
        id = id.substringAfterLast("/"),
        title = title,
        imgUrl = if (coverKey != null) {
            "https://covers.openlibrary.org/b/olid/${coverKey}-L.jpg"
        } else {
            "https://covers.openlibrary.org/b/id/${coverAlternativeKey}-L.jpg"
        },
        authors = authorNames ?: authorNames2ndApproach ?: emptyList(),
        description = null,
        languages = languages ?: emptyList(),
        firstPublishYear = firstPublishYear.toString(),
        avgRating = ratingsAverage ?: 0.0,
        ratingCount = ratingsCount ?: 0,
        numPages = numPagesMedian ?: 0,
        numEditions = numEditions ?: 0,
        bookType = null,
        isSaved = null,
        isViewed = null,
        summaryText = null,
        summaryBase64 = null,
        timeStamp = null
    )
}

fun Book.toBookBookEntity(
    isSaved: Boolean? = null,
    isViewed: Boolean? = null,
    bookType: String? = null
): OpenBookEntity {
    return OpenBookEntity(
        id = id,
        title = title,
        imgUrl = imgUrl,
        authors = authors,
        description = description,
        languages = languages,
        firstPublishYear = firstPublishYear,
        avgRating = avgRating,
        ratingCount = ratingCount,
        numPages = numPages,
        numEditions = numEditions,
        bookType = bookType,
        isSaved = isSaved,
        isViewed = isViewed,
        summaryText = null,
        summaryBase64 = null,
        timeStamp = System.currentTimeMillis(),
    )
}

fun OpenBookEntity.toBook(): Book {
    return Book(
        id = id,
        title = title,
        imgUrl = imgUrl,
        authors = authors,
        description = description,
        languages = languages,
        firstPublishYear = firstPublishYear,
        avgRating = avgRating,
        ratingCount = ratingCount,
        numPages = numPages,
        numEditions = numEditions,
        bookType = bookType,
        isSaved = isSaved,
        isViewed = isViewed,
        summaryText = summaryText,
        summaryBase64 = summaryBase64,
        timeStamp = timeStamp
    )
}