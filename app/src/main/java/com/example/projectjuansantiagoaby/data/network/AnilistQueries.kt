package com.example.projectjuansantiagoaby.data.network

object AnilistQueries {
    fun getTrendingQuery(): String {
        return """
            query {
              Page(page: 1, perPage: 20) {
                media(sort: TRENDING_DESC, type: ANIME) {
                  id
                  title {
                    romaji
                    english
                  }
                  coverImage {
                    extraLarge
                    large
                  }
                  description
                  averageScore
                  type
                  episodes
                }
              }
            }
        """.trimIndent()
    }
    
    fun getSearchQuery(search: String): String {
        return """
            query {
              Page(page: 1, perPage: 20) {
                media(search: "$search", type: ANIME) {
                  id
                  title {
                    romaji
                    english
                  }
                  coverImage {
                    extraLarge
                    large
                  }
                  description
                  averageScore
                  type
                  episodes
                }
              }
            }
        """.trimIndent()
    }
}
