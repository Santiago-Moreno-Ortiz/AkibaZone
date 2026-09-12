package com.example.akibazone.data.network

object AnilistQueries {

    private const val MEDIA_FIELDS = """
        id
        title {
          romaji
          english
          native
        }
        coverImage {
          extraLarge
          large
          color
        }
        bannerImage
        description
        averageScore
        format
        type
        status
        episodes
        duration
        season
        seasonYear
        genres
        studios(isMain: true) {
          nodes {
            name
          }
        }
    """

    fun getTrendingQuery(): String {
        return """
            query {
              Page(page: 1, perPage: 20) {
                media(sort: TRENDING_DESC, type: ANIME) {
                  $MEDIA_FIELDS
                }
              }
            }
        """.trimIndent()
    }

    fun getPopularQuery(): String {
        return """
            query {
              Page(page: 1, perPage: 20) {
                media(sort: POPULARITY_DESC, type: ANIME) {
                  $MEDIA_FIELDS
                }
              }
            }
        """.trimIndent()
    }

    fun getLatestReleasesQuery(): String {
        return """
            query {
              Page(page: 1, perPage: 20) {
                media(sort: UPDATED_AT_DESC, type: ANIME, status: RELEASING) {
                  $MEDIA_FIELDS
                }
              }
            }
        """.trimIndent()
    }

    fun getTopRatedQuery(): String {
        return """
            query {
              Page(page: 1, perPage: 20) {
                media(sort: SCORE_DESC, type: ANIME) {
                  $MEDIA_FIELDS
                }
              }
            }
        """.trimIndent()
    }

    fun getFilterQuery(
        search: String? = null,
        genre: String? = null,
        format: String? = null,
        sort: String = "TRENDING_DESC",
        page: Int = 1
    ): String {
        val params = mutableListOf("type: ANIME", "sort: $sort")
        if (!search.isNullOrBlank()) {
            val safeSearch = com.google.gson.Gson().toJson(search)
            params.add("search: $safeSearch")
        }
        if (!genre.isNullOrBlank() && genre != "Todos" && genre != "All") {
            params.add("""genre: "$genre"""")
        }
        if (!format.isNullOrBlank() && format != "Todos" && format != "All") {
            params.add("""format: $format""")
        }

        val paramString = params.joinToString(", ")

        return """
            query {
              Page(page: $page, perPage: 30) {
                pageInfo {
                  total
                  currentPage
                  lastPage
                  hasNextPage
                }
                media($paramString) {
                  $MEDIA_FIELDS
                }
              }
            }
        """.trimIndent()
    }

    fun getAnimeDetailQuery(id: Int): String {
        return """
            query {
              Media(id: $id, type: ANIME) {
                $MEDIA_FIELDS
                trailer {
                  id
                  site
                }
                nextAiringEpisode {
                  episode
                  timeUntilAiring
                }
              }
            }
        """.trimIndent()
    }
}
