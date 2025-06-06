package org.readora.readout.book.openbook.data.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.readora.readout.book.openbook.data.dto.BookWorkDto
import org.readora.readout.book.openbook.data.dto.BrowseResponseDto
import org.readora.readout.book.openbook.data.dto.SearchResponseDto
import org.readora.readout.core.cloudtts.dto.AudioConfig
import org.readora.readout.core.cloudtts.dto.CloudTextToSpeechRequestDto
import org.readora.readout.core.cloudtts.dto.CloudTextToSpeechResponseDto
import org.readora.readout.core.cloudtts.dto.Input
import org.readora.readout.core.cloudtts.dto.Voice
import org.readora.readout.core.data.network.safeCall
import org.readora.readout.core.utils.DataError
import org.readora.readout.core.utils.Result
import org.readora.readout.core.gemini.dto.ContentItem
import org.readora.readout.core.gemini.dto.GeminiResponseDto
import org.readora.readout.core.gemini.dto.RequestBody
import org.readora.readout.core.gemini.dto.RequestPart
import org.readora.readout.core.utils.CLOUD_TEXT_TO_SPEECH_API_KEY
import org.readora.readout.core.utils.CLOUD_TEXT_TO_SPEECH_BASE_URL
import org.readora.readout.core.utils.GEMINI_API_KEY
import org.readora.readout.core.utils.GEMINI_BASE_URL
import org.readora.readout.core.utils.GEMINI_FLASH
import org.readora.readout.core.utils.OPEN_LIBRARY_BASE_URL
import org.readora.readout.core.utils.USER_AGENT

class RemoteBookDataSourceImpl(
    private val httpClient: HttpClient
) : RemoteBookDataSource {

    override suspend fun searchBooks(
        query: String,
        resultLimit: Int?
    ): Result<SearchResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "${OPEN_LIBRARY_BASE_URL}/search.json"
            ) {
                header("User-Agent", USER_AGENT)
                parameter("q", query)
                parameter("limit", resultLimit)
                parameter(
                    "fields",
                    "key,title,author_name,author_key,cover_edition_key,cover_i,ratings_average,ratings_count,first_publish_year,language,number_of_pages_median,edition_count"
                )
            }
        }
    }


    override suspend fun fetchBookSummary(prompt: String): Result<GeminiResponseDto, DataError.Remote> {
        return safeCall<GeminiResponseDto> {
            val requestBody = RequestBody(
                contents = listOf(
                    ContentItem(
                        parts = listOf(RequestPart(text = prompt))
                    )
                )
            )
            httpClient.post(
                urlString = "${GEMINI_BASE_URL}/v1beta/models/${GEMINI_FLASH}:generateContent"
            ) {
                parameter("key", GEMINI_API_KEY)
                setBody(
                    Json.encodeToString(requestBody)
                )
            }
        }
    }

    override suspend fun fetchBookSummaryAudio(summary: String): Result<CloudTextToSpeechResponseDto, DataError.Remote> {
        return safeCall<CloudTextToSpeechResponseDto> {

            val requestBody = CloudTextToSpeechRequestDto(
                input = Input(
                    text = summary
                ),
                audioConfig = AudioConfig(
                    audioEncoding = "MP3",
                    pitch = 0,
                    speakingRate = 1,
                ),
                voice = Voice(
                    languageCode = "en-US",
                    name = "en-US-Studio-Q"
                )
            )

            httpClient.post(
                urlString = "${CLOUD_TEXT_TO_SPEECH_BASE_URL}/v1beta1/text:synthesize"
            ) {
                header("Accept", "application/json")
                header("Content-Type", "application/json")
                parameter("key", CLOUD_TEXT_TO_SPEECH_API_KEY)
                setBody(
                    Json.encodeToString(requestBody)
                )
            }
        }
    }


    override suspend fun fetchBookDescription(bookWorkId: String): Result<BookWorkDto, DataError.Remote> {
        return safeCall<BookWorkDto> {
            httpClient.get(
                urlString = "${OPEN_LIBRARY_BASE_URL}/works/$bookWorkId.json"
            ) {
                header("User-Agent", USER_AGENT)
            }
        }
    }

    override suspend fun fetchBrowseBooks(
        subject: String?,
        offset: Int?,
        limit: Int
    ): Result<BrowseResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "${OPEN_LIBRARY_BASE_URL}/subjects/$subject.json"
            ) {
                header("User-Agent", USER_AGENT)
                parameter("offset", offset)
                parameter("limit", limit)
            }
        }
    }
}