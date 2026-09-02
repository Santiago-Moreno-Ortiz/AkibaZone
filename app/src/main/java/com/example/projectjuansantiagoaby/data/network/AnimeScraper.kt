package com.example.projectjuansantiagoaby.data.network

import com.example.projectjuansantiagoaby.data.model.Anime
import com.example.projectjuansantiagoaby.data.model.AnimeDetail
import com.example.projectjuansantiagoaby.data.model.Episode
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
                val link = element.select("a").attr("href")
                val imageUrl = element.select("img").attr("src")
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
                val link = element.select("a").attr("href")
                val imageUrl = element.select("img").attr("src")
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
            val doc = Jsoup.connect("$baseUrl/browse?q=$query")
                .userAgent(userAgent)
                .timeout(15000)
                .get()
            val elements = doc.select("ul.ListAnimes li article.Anime")
            for (element in elements) {
                val title = element.select("h3.Title").text()
                val link = element.select("a").attr("href")
                val imageUrl = element.select("img").attr("src")
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
            val fullUrl = if (animeLink.startsWith("http")) animeLink else "$baseUrl$animeLink"
            val doc = Jsoup.connect(fullUrl)
                .userAgent(userAgent)
                .timeout(15000)
                .get()
            
            val title = doc.select("h1.Title").text()
            val synopsis = doc.select("div.Description p").text()
            val imageUrl = doc.select("div.AnimeCover div.Image img").attr("src")
            val genres = doc.select("nav.Nvgnrs a").map { it.text() }
            val id = animeLink.substringAfterLast("/")
            
            val anime = Anime(id, title, imageUrl, animeLink)
            
            // Episodes are often loaded via Javascript in AnimeFLV, but let's try to find the list in the script
            // or use the standard pattern.
            val episodes = mutableListOf<Episode>()
            val script = doc.select("script").filter { it.data().contains("var episodes =") }.firstOrNull()
            
            if (script != null) {
                // Parse the episodes from the Javascript variable
                // Pattern: var episodes = [[1,1],[2,2],...];
                val data = script.data()
                val episodesData = data.substringAfter("var episodes = [").substringBefore("];")
                // This needs more robust parsing, but for now let's assume simple pattern
                // [1, 100] -> episode 1, id 100
                val regex = Regex("\\[(\\d+),(\\d+)\\]")
                val matches = regex.findAll(episodesData)
                for (match in matches) {
                    val num = match.groupValues[1]
                    val epId = match.groupValues[2]
                    // Link pattern: /ver/slug-num
                    val epLink = "/ver/$id-$num"
                    episodes.add(Episode(id, num, epLink))
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
            
            // AnimeFLV stores video servers in a Javascript variable 'videos'
            val script = doc.select("script").filter { it.data().contains("var videos =") }.firstOrNull()
            if (script != null) {
                val data = script.data()
                // The 'videos' variable contains a JSON-like structure with server links
                // Example: var videos = {"SUB":[{"server":"stape","code":"...","url":"..."}]}
                // We'll extract URLs using regex for simplicity
                val regex = Regex("\"code\":\"([^\"]+)\"")
                val matches = regex.findAll(data)
                for (match in matches) {
                    val code = match.groupValues[1]
                    if (code.contains("http")) {
                        videoLinks.add(code.replace("\\/", "/"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return videoLinks
    }
}
