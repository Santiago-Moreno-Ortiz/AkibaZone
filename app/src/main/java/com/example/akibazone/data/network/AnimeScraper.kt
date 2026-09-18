package com.example.akibazone.data.network

import com.example.akibazone.data.model.Anime
import com.example.akibazone.data.model.AnimeDetail
import com.example.akibazone.data.model.Episode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Exception

class AnimeScraper {
    private val baseUrl = "https://www3.animeflv.net"

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"

    fun getPopularAnime(): List<Anime> {
        val animeList = mutableListOf<Anime>()
        try {
            val doc = Jsoup.connect(baseUrl)
                .userAgent(userAgent)
                .timeout(15000)
                .get()
            // AnimeFLV popular list usually in "ListAnimes" class
            val elements = doc.select("ul.ListAnimes li article.Anime")
            for (element in elements) {
                val title = element.select("h3.Title").text()
                val link = element.select("a").attr("abs:href")
                val imageUrl = element.select("img").attr("abs:src")
                val id = link.substringAfterLast("/")
                
                animeList.add(Anime(id, title, imageUrl, link, null))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return animeList
    }

    fun getLatestReleases(): List<Anime> {
        val animeList = mutableListOf<Anime>()
        try {
            val doc = Jsoup.connect(baseUrl)
                .userAgent(userAgent)
                .timeout(15000)
                .get()
            // AnimeFLV latest episodes usually in "ListEpisodios" class
            val elements = doc.select("ul.ListEpisodios li")
            for (element in elements) {
                val title = element.select("strong.Title").text()
                val episode = element.select("span.Capi").text()
                val link = element.select("a").attr("abs:href")
                val imageUrl = element.select("img").attr("abs:src")
                // For episodes, the link is /ver/anime-slug-num, we want /anime/anime-slug
                // Or we can just store the episode link as is.
                val animeId = link.substringAfter("/ver/").substringBeforeLast("-")
                val animeLink = "/anime/$animeId"
                
                animeList.add(Anime(animeId, title, imageUrl, animeLink, episode))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return animeList
    }

    fun searchAnime(query: String): List<Anime> {
        val animeList = mutableListOf<Anime>()
        try {
            val doc = Jsoup.connect("$baseUrl/browse").data("q", query)
                .userAgent(userAgent)
                .timeout(15000)
                .get()
            val elements = doc.select("ul.ListAnimes li article.Anime")
            for (element in elements) {
                val title = element.select("h3.Title").text()
                val link = element.select("a").attr("abs:href")
                val imageUrl = element.select("img").attr("abs:src")
                val id = link.substringAfterLast("/")
                
                animeList.add(Anime(id, title, imageUrl, link))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return animeList
    }

    fun getAnimeDetail(animeLink: String): AnimeDetail? {
        try {
            val slug = animeLink.trimEnd('/').substringAfterLast("/")
            val fullUrl = when {
                animeLink.startsWith("http") -> animeLink
                animeLink.startsWith("/") -> "$baseUrl$animeLink"
                else -> "$baseUrl/anime/$slug"
            }
            val doc = Jsoup.connect(fullUrl)
                .userAgent(userAgent)
                .timeout(15000)
                .get()
            
            val title = doc.select("h1.Title").text()
            val synopsis = doc.select("div.Description p").text()
            val imageUrl = doc.select("div.AnimeCover div.Image img").attr("abs:src")
            val genres = doc.select("nav.Nvgnrs a").map { it.text() }
            val anime = Anime(slug, title, imageUrl, "/anime/$slug")
            
            // Episodes are often loaded via Javascript in AnimeFLV, but let's try to find the list in the script
            // or use the standard pattern.
            val episodes = mutableListOf<Episode>()
            val script = doc.select("script").firstOrNull { it.data().contains("var episodes") }
            
            if (script != null) {
                val data = script.data()
                val episodesData = Regex(
                    """var\s+episodes\s*=\s*(\[\s*\[.*?]])\s*;""",
                    setOf(RegexOption.DOT_MATCHES_ALL),
                ).find(data)?.groupValues?.getOrNull(1).orEmpty()
                val matches = Regex("""\[\s*(\d+(?:\.\d+)?)\s*,\s*\d+\s*]""").findAll(episodesData)
                for (match in matches) {
                    val num = match.groupValues[1]
                    episodes.add(Episode(slug, num, AnimeFlvPlaybackMapper.episodeLink(slug, num)))
                }
            }
            
            return AnimeDetail(anime, synopsis, genres, episodes.reversed())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getVideoLinks(episodeLink: String): List<String> {
        val videoLinks = mutableListOf<String>()
        try {
            val fullUrl = if (episodeLink.startsWith("http")) episodeLink else "$baseUrl$episodeLink"
            val doc = Jsoup.connect(fullUrl)
                .userAgent(userAgent)
                .timeout(15000)
                .get()
            
            // Only direct media URLs are returned. Provider pages or opaque codes are not streams.
            val script = doc.select("script").firstOrNull { it.data().contains("var videos") }
            if (script != null) {
                val data = script.data()
                val regex = Regex("\"(?:url|code)\"\\s*:\\s*\"([^\"]+)\"")
                val matches = regex.findAll(data)
                for (match in matches) {
                    val candidate = match.groupValues[1]
                        .replace("\\/", "/")
                        .replace("\\u0026", "&")
                        .replace("\\u003d", "=")
                    if (PlaybackSourceClassifier.classify(candidate) != null) {
                        videoLinks.add(candidate)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return videoLinks
    }
}
