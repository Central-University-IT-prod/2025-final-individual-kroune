package io.github.kroune.superfinancer.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsFeedModel(
    val status: String,
    val copyright: String,
    @SerialName("num_results")
    val numResults: Int,
    val results: List<ArticlesFeed>
)

@Serializable
data class ArticlesFeed(
    val section: String,
    val subsection: String? = null,
    val title: String,
    val abstract: String,
    val url: String,
    val byline: String,
    @SerialName("item_type")
    val itemType: String? = null,
    val source: String,
    @SerialName("updated_date")
    val updatedDate: String,
    @SerialName("created_date")
    val createdDate: String,
    @SerialName("published_date")
    val publishedDate: String,
    @SerialName("material_type_facet")
    val materialTypeFacet: String? = null,
    val kicker: String? = null,
    @SerialName("des_facet")
    val desFacet: List<String>,
    @SerialName("org_facet")
    val orgFacet: List<String>,
    @SerialName("per_facet")
    val perFacet: List<String>,
    @SerialName("geo_facet")
    val geoFacet: List<String>,
    val multimedia: List<MultimediaModel>?
)

@Serializable
data class MultimediaModel(
    val url: String,
    val format: String? = null,
    val height: Int,
    val width: Int,
    val type: String? = null,
    val subtype: String? = null,
    val caption: String? = null,
    val copyright: String? = null
)
