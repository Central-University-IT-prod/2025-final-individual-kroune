package io.github.kroune.superfinancer.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface ArticlesSearchApiResponse {
    data class Success(val data: ArticlesSearchModel) : ArticlesSearchApiResponse
    data object Unauthorized : ArticlesSearchApiResponse
    data object TooManyRequests : ArticlesSearchApiResponse
    data object UnknownError : ArticlesSearchApiResponse
    data object NetworkError : ArticlesSearchApiResponse
}

@Serializable
data class ArticlesSearchModel(
    val status: String,
    val copyright: String,
    val response: Response
)

@Serializable
data class Response(
    val docs: List<SearchArticle>
)


@Serializable
data class SearchArticle(
    @SerialName("abstract")
    val abstract: String,
    @SerialName("web_url")
    val webUrl: String,
    val snippet: String,
    @SerialName("print_page")
    val printPage: Int? = null,
    @SerialName("print_section")
    val printSection: String? = null,
    val source: String? = null,
    val multimedia: List<SearchMultimediaModel>,
    val headline: HeadLine,
    val keywords: List<Keyword>,
    @SerialName("pub_date")
    val publishDate: String,
    @SerialName("document_type")
    val documentType: String,
    @SerialName("news_desk")
    val newsDesk: String,
    @SerialName("section_name")
    val sectionName: String? = null,
    @SerialName("byline")
    val byLine: ByLine,
    @SerialName("type_of_material")
    val typeOfMaterial: String? = null,
    @SerialName("_id")
    val id: String,
    @SerialName("word_count")
    val wordCount: Int,
    val uri: String,
)

@Serializable
data class ByLine(
    val original: String?,
    val person: List<Person>,
    val organization: String?
)

@Serializable
data class Person(
    val firstname: String?,
    val middlename: String?,
    val lastname: String?,
    val qualifier: String?,
    val title: String?,
    val role: String,
    val organization: String,
    val rank: Int
)

@Serializable
data class Keyword(
    val name: String,
    val value: String,
    val rank: Int,
    val major: String
)

@Serializable
data class HeadLine(
    val main: String,
    val kicker: String? = null,
    @SerialName("content_kicker")
    val contentKicker: String? = null,
    @SerialName("print_headline")
    val printHeadline: String?,
    val name: String?,
    val seo: String?,
    val sub: String?
)

@Serializable
data class SearchMultimediaModel(
    val rank: Int,
    val subtype: String,
    val caption: String?,
    val type: String,
    val url: String,
    val height: Int,
    val width: Int,
    val legacy: LegacyMultimediaModel,
    @SerialName("crop_name")
    val cropName: String
)

@Serializable
data class LegacyMultimediaModel(
    @SerialName("xlarge")
    val xlarge: String? = null,
    @SerialName("xlargewidth")
    val xlargeWidth: Int? = null,
    @SerialName("xlargeheight")
    val xlargeHeight: Int? = null
)
