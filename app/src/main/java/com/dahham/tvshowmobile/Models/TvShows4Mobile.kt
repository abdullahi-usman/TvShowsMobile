package com.dahham.tvshowmobile.Models

import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.io.StringWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by dahham on 4/17/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4Mobile {

    private val HOME_URL = "http://tvshows4mobile.com"
    private val ALL_SHOW_URL = "http://tvshows4mobile.com/search/list_all_tv_series/?sort=a-z"

    fun getEpisodes(series: Series): List<Episode> {

        val episodes = ArrayList<Episode>()
        series.episodes = episodes

        var series_link = series.link

        Outside@ while (true) {

            val html = getHtmlString(series_link!!)

            getDataList(html) { name, link ->

                val episode = Episode(show_name = series.show_name, season_name = series.season, episode_name = name,  link = link)

                episodes.add(episode)
            }

            for (child in Jsoup.parse(html).select("div.page_nav").select("a[href^=http://tvshows4mobile.com]")) {
                if (child.text().contains("Next", true)) {
                    series_link = child.attr("href")
                    continue@Outside
                }
            }

            break

        }

        return episodes
    }

    fun getLastestEpisodes(): List<LastestEpisode> {

        val jsoup = Jsoup.parse(getHtmlString(HOME_URL))

        val elements = jsoup.select("div.data_list")
        val latest_episodes = ArrayList<LastestEpisode>()
        for (element in elements) {

            for (child_element in element.children()) {
                if (child_element.`is`("div[class=\"data main\"]")) {
                    val splits = child_element.text().split("-")

                    val lastest_episode = LastestEpisode(show_name = splits[0].trim(), season_name = splits[1].trim(), episode_name = if (splits.size > 4) (splits[2] + "-" + splits[3]).trim() else splits[2].trim(), dateAdded = formatDate(splits[splits.size - 1]))

                    latest_episodes.add(lastest_episode)
                }
            }
        }

        return latest_episodes
    }


    fun getAllShows(): List<Show> {

        val jsoup = Jsoup.parse(getHtmlString(ALL_SHOW_URL))

        val all_shows = ArrayList<Show>()

        for ((name, link) in getDataList(jsoup.toString(), null)) {
            all_shows.add(Show(name = name, link = link))
        }


        Collections.sort(all_shows)
        return all_shows
    }


    fun getShowProperties(show: Show): Show {

        val jsoup = Jsoup.parse(getHtmlString(show.link!!))

        show.poster = jsoup.select("div.img").select("img[src^=http://tvshows4mobile.com/res/tv_serials]").attr("src")

        show.description = jsoup.select("div.serial_desc").text()

        val other_info = jsoup.select("div.other_info").select("div.row")

        for (info in other_info) {
            val splits = info.text().split(":")

            when (splits[0]) {
                "Casts" -> show.casts = splits[1].split(",")
                "Genres" -> show.genre = splits[1].split(",")
                "Run Time" -> show.runtime = splits[1]
                "Views" -> show.views = intPart(splits[1])
                "Rating" -> show.rating = removeLetters(splits[1]).toFloat()
                "Seasons" -> show.series = ArrayList(intPart(splits[1]))
            }
        }

        val series = ArrayList<Series>()

        getDataList(jsoup.toString()) { name, link ->
            val serie = Series(show_name = show.name, season = name, link = link)

            series.add(serie)
        }

        show.series = series

        return show

    }

    fun getEpisodeLink(episodes: Episode): List<Link> {
        return getDownloadLink(episodes.link!!)
    }

    private var all_shows = Hashtable<String, String>()

    fun getLastestEpisodesLinks(vararg latest_episodes: LastestEpisode) {

        if (this.all_shows.isEmpty) {
            val jsoup_all_shows = getHtmlString(ALL_SHOW_URL)

            all_shows = getDataList(jsoup_all_shows, null)

        }

        for (latest_episode in latest_episodes) {

            if (all_shows.containsKey(latest_episode.show_name)) {

                val _show = Show(name = latest_episode.show_name, link = all_shows[latest_episode.show_name])
                getShowProperties(_show)

                latest_episode.poster = _show.poster

                if (_show.series != null) {

                    for (_serie in _show.series!!) {

                        if (_serie.season == latest_episode.season_name) {

                            val html = getHtmlString(_serie.link!!)

                            val all_episodes = getDataList(html, null)

                            if (all_episodes.containsKey(latest_episode.episode_name)) {

                                latest_episode.download_links = getDownloadLink(all_episodes[latest_episode.episode_name]!!)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getDownloadLinkDirect(name: String, season: String, episode: String, ext: String, isHD: Boolean = false): String{

//        var _season = season
//        var season_finale = ""
//        if (season.contains("Season Finale")){
//            _season = season.replace("Season Finale","", true)
//            season_finale = "Season Finale"
//        }

        val url = "http://d${Random().nextInt(12)}.tvshows4mobile.com/$name/${season}/$name - S${removeLetters(season)}E${removeLetters(episode)}%20${if(isHD) "HD%20" else ""}(TvShows4Mobile.Com).$ext"
                .replace(" - Season Finale", "", true).replace(" ", "%20").trim()


        return url
    }

    private fun getDownloadLink(url: String): List<Link> {
        val html = getHtmlString(url)

        val links = getDataList(html, null)

        val _links = ArrayList<Link>()

        for ((_name, _link) in links) {

            if (_name.contains("HD", true) && _name.endsWith("mp4", true)) {
                _links.add(Link(Link.HD, _link))
            } else if (_name.endsWith("3gp", true)) {
                _links.add(Link(Link.GP3, _link))
            } else if (_name.endsWith("mp4", true)) {
                _links.add(Link(Link.MP4, _link))
            }

        }

        return _links
    }


    private fun getDataList(html: String, listener: ((a: String, b: String) -> Unit)?): Hashtable<String, String> {
        val jsoup = Jsoup.parse(html)
        val elements = jsoup.select("div.data_list")
        val data = Hashtable<String, String>()

        for (element in elements) {

            for (child_element in element.children().select("a[href^=http://tvshows4mobile.com]")) {
                val link = child_element.attr("href").trim()
                val name = child_element.text().trim()

                data.put(name, link)
                listener?.invoke(name, link)
            }
        }

        return data

    }

    private fun intPart(str: String): Int {
        return Integer.parseInt(removeLetters(str))
    }

    private fun removeLetters(str: String): String {
        return str.replace(Regex("[a-zA-Z]"), "").trim()
    }

    private fun formatDate(str: String): String {
        return str.replace("[", "").replace("]", "").trim()
    }

    private fun getHtmlString(url: String): String{
        return try {
            Jsoup.connect(url).execute().body()
        } catch (e: Exception) {
            getDocumentUsingHttp(url)
        }
    }

    private fun getDocumentUsingHttp(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection

        val stream = BufferedInputStream(connection.inputStream)

        var i = 0
        val writer = StringWriter()
        while (true){
            i = stream.read()
            if (i != -1){
                writer.write(i)
            }else {
                break
            }
        }

        writer.flush()
        writer.close()
        stream.close()


        return writer.toString()
    }
}
