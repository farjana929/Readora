package org.readora.readout.book.audiobook.data.repository

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.readora.readout.book.audiobook.data.database.AudioBookDao
import org.readora.readout.book.audiobook.data.mappers.toAudioBook
import org.readora.readout.book.audiobook.data.mappers.toAudioBookEntity
import org.readora.readout.book.audiobook.data.mappers.toAudioBookTracks
import org.readora.readout.book.audiobook.data.network.RemoteAudioBookDataSource
import org.readora.readout.book.audiobook.domain.entity.AudioBook
import org.readora.readout.book.audiobook.domain.entity.AudioBookTrack
import org.readora.readout.book.audiobook.domain.repository.AudioBookRepository
import org.readora.readout.core.utils.DataError
import org.readora.readout.core.utils.EmptyResult
import org.readora.readout.core.utils.Result
import org.readora.readout.core.utils.map
import org.readora.readout.core.utils.onSuccess

class AudioBookRepositoryImpl(
    private val remoteAudioBookDataSource: RemoteAudioBookDataSource,
    private val audioBookDao: AudioBookDao
) : AudioBookRepository {

    override suspend fun searchAudioBooks(query: String): Result<List<AudioBook>, DataError.Remote> {
        return remoteAudioBookDataSource.searchAudioBooks(query).map { dto ->
            dto.results.map {
                it.toAudioBook()
            }
        }
    }

    override suspend fun getBrowseAudioBooks(
        genre: String?,
        offset: Int?,
        limit: Int
    ): Result<List<AudioBook>, DataError.Remote> {
        return remoteAudioBookDataSource.fetchBrowseAudioBooks(
            genre = genre,
            offset = offset,
            limit = limit
        )
            .map { dto ->
                dto.results.map {
                    it.toAudioBook()
                }
            }
    }

    override suspend fun getAudioBookTracks(audioBookId: String): Result<List<AudioBookTrack>, DataError.Remote> {
        return remoteAudioBookDataSource.fetchAudioBookTracks(audioBookId).map { dto ->
            dto.audioBookTracks.map {
                it.toAudioBookTracks()
            }
        }
    }

    override suspend fun saveBook(book: AudioBook): EmptyResult<DataError.Local> {
        return try {
            audioBookDao.upsert(
                book.toAudioBookEntity(
                    isSaved = true,
                    bookType = book.bookType
                )
            )
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun getBookSummary(
        prompt: String,
        bookId: String
    ): Result<String?, DataError> {

        val localResult = audioBookDao.getSavedBook(bookId)
        if (localResult?.summaryText != null) {
            return Result.Success(localResult.summaryText)
        }
        return remoteAudioBookDataSource.fetchBookSummary(prompt)
            .map { it.candidates.first().content.parts.first().text }
            .onSuccess { summary ->
                audioBookDao.updateSummary(id = bookId, summary = summary)
            }
    }


    override suspend fun insertBookIntoDB(book: AudioBook): EmptyResult<DataError.Local> {
        return try {
            audioBookDao.upsert(
                book.toAudioBookEntity(
                    isSaved = book.isSaved,
                    isViewed = true,
                    bookType = book.bookType
                )
            )
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteFromSaved(id: String) {
        audioBookDao.deleteSavedBook(id)
    }

    override fun getSavedBooks(): Flow<List<AudioBook>> {
        return audioBookDao.getSavedBooks().map { audioBookEntities ->
            audioBookEntities.map { it.toAudioBook() }
        }
    }

    override fun getBookById(id: String): Flow<Pair<AudioBook?, Boolean>> {
        return audioBookDao.getSavedBookById(id).map { bookEntity ->
            val audioBook = bookEntity?.toAudioBook()
            audioBook to (bookEntity?.isSaved == true)
        }
    }
}