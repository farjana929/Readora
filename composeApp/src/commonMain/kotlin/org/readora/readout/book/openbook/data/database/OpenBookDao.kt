package org.readora.readout.book.openbook.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface OpenBookDao {
    @Upsert
    suspend fun upsert(openBook: OpenBookEntity)

    @Query("SELECT * FROM OpenBookEntity")
    fun getAllBooks(): Flow<List<OpenBookEntity>>

    @Query("SELECT * FROM OpenBookEntity WHERE isSaved = 1")
    fun getSavedBooks(): Flow<List<OpenBookEntity>>

    @Query("SELECT * FROM OpenBookEntity WHERE id = :id")
    suspend fun getSavedBook(id: String): OpenBookEntity?

    @Query("SELECT * FROM OpenBookEntity WHERE id = :id LIMIT 1")
    fun getSavedBookById(id: String): Flow<OpenBookEntity?>

    @Query("SELECT * FROM OpenBookEntity WHERE bookType = :bookType")
    suspend fun getSavedBooksByType(bookType: String): List<OpenBookEntity>

    @Query("DELETE FROM OpenBookEntity WHERE id = :id AND isSaved = 1")
    suspend fun deleteSavedBook(id: String)

    @Query("DELETE FROM OpenBookEntity WHERE bookType = :bookType AND (isSaved IS NULL OR isSaved = 0)")
    suspend fun deleteBooksByType(bookType: String)

    @Query("UPDATE OpenBookEntity SET isSaved = :isSaved, timeStamp = :timeStamp WHERE id = :id")
    suspend fun updateIsSaved(id: String, isSaved: Boolean, timeStamp: Long)

    @Query("UPDATE OpenBookEntity SET summaryText = :summary WHERE id = :id")
    suspend fun updateSummary(id: String, summary: String)

    @Query("UPDATE OpenBookEntity SET summaryBase64 = :summaryBase64 WHERE id = :id")
    suspend fun updateSummaryBase64(id: String, summaryBase64: String)


    @Query("DELETE FROM OpenBookEntity")
    suspend fun clearAll()
}