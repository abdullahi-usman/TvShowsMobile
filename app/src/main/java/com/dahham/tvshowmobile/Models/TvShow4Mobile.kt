package com.dahham.tvshowmobile.Models

import org.jsoup.Jsoup
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

        getDataList(Jsoup.connect(series.link).get().toString()) { name, link ->

            val episode = Episode(name = name, link = link)

            episodes.add(episode)

        }

        return episodes
    }

    fun getLastestEpisodes(): List<LastestEpisode> {

        val jsoup = Jsoup.connect(HOME_URL).get()
        val elements = jsoup.select("div.data_list")
        val latest_episodes = ArrayList<LastestEpisode>()
        for (element in elements) {

            for (child_element in element.children()) {
                if (child_element.`is`("div[class=\"data main\"]")) {
                    val splits = child_element.text().split("-")

                    val lastest_episode = LastestEpisode(splits[0].trim(), season = splits[1].trim(), episode = splits[2].trim(), dateAdded = formatDate(splits[3]))

                    latest_episodes.add(lastest_episode)
                }
            }
        }

        return latest_episodes
    }


    fun getAllShows(): List<Show> {

        val jsoup = Jsoup.connect(ALL_SHOW_URL).get()
        val all_shows = ArrayList<Show>()
        for ((name, link) in getDataList(jsoup.toString(), null)) {
            all_shows.add(Show(name = name, link = link))
        }

        return all_shows
    }


    fun getShowProperties(show: Show): Show {
        val jsoup = Jsoup.connect(show.link).get()

        show.poster = jsoup.select("div.img").select("img[src^=http://tvshows4mobile.com/res/tv_serials]").attr("src")

        show.description = jsoup.select("div.serial_desc").text()

        val other_info = jsoup.select("div.other_info").select("div.row")

        for (info in other_info) {
            val splits = info.text().split(":")

            when (splits[0]) {
                "Casts" -> show.casts = splits[1].split(",")
                "Genres" -> show.genre = splits[1].split(",")
                "Run Time" -> show.runtime = intPart(splits[1])
                "Views" -> show.views = intPart(splits[1])
                "Rating" -> show.rating = removeLetters(splits[1]).toFloat()
                "Seasons" -> show.series = ArrayList(intPart(splits[1]))
            }
        }

        val series = ArrayList<Series>()

        getDataList(jsoup.toString()) { name, link ->
            val serie = Series(season = name, link = link)

            series.add(serie)
        }

        show.series = series

        return show

    }

    fun getEpisodeLink(episodes: Episode): List<Link> {
        return getDownloadLink(episodes.link)
    }


    private var all_shows = Hashtable<String, String>()

    fun getLastestEpisodesLinks(vararg latest_episodes: LastestEpisode) {

        if (this.all_shows.isEmpty) {
            val jsoup_all_shows = Jsoup.connect(ALL_SHOW_URL).get()

            all_shows = getDataList(jsoup_all_shows.toString(), null)

        }

        for (latest_episode in latest_episodes) {

            if (all_shows.containsKey(latest_episode.name)) {

                val _show = Show(name = latest_episode.name, link = all_shows[latest_episode.name])
                getShowProperties(_show)

                if (_show.series != null) {

                    for (_serie in _show.series!!) {

                        if (_serie.season == latest_episode.season) {

                            val all_episodes = getDataList(Jsoup.connect(_serie.link).get().toString(), null)

                            if (all_episodes.containsKey(latest_episode.episode)) {

                                latest_episode.link = getDownloadLink(all_episodes[latest_episode.episode]!!)
                            }
                        }
                    }
                }
            }
        }
    }


    private fun getDownloadLink(url: String): List<Link> {
        val links = getDataList(Jsoup.connect(url).get().toString(), null)

        val _links = ArrayList<Link>()

        for ((_name, _link) in links) {

            if (_name.contains("HD", true) && _name.endsWith("mp4", true)) {
                _links.add(Link(Link.Type.HD, _link))
            } else if (_name.endsWith("3gp", true)) {
                _links.add(Link(Link.Type.GP3, _link))
            } else if (_name.endsWith("mp4", true)) {
                _links.add(Link(Link.Type.MP4, _link))
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
}
