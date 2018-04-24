package com.dahham.tvshowmobile

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.dahham.tvshowmobile.Models.TvShows4Mobile
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.dahham.tvshowmobile", appContext.packageName)
    }

    @Test
    fun testTvShowMobileLatestEpisodeLinks() {
        val show = TvShows4Mobile()
        val latest_episode = show.getLastestEpisodes()[0]
        show.getLastestEpisodesLinks(latest_episode)

        System.out.println(latest_episode.link)
    }

    @Test
    fun testTvShowMobileGetEpisodes() {
        val show = TvShows4Mobile()

        val all_shows = show.getAllShows()
        val _show = all_shows[330]

        show.getShowProperties(_show)

        val serie = _show.series?.get(_show.series!!.size - 1)!!
        serie.episodes = show.getEpisodes(serie)

        val links = show.getEpisodeLink(serie.episodes?.get(0)!!)


        System.out.println(links[0].link)
    }

    @Test
    fun testJsoup(){
        val html = Jsoup.connect("http://tvshows4mobile.com").execute().body()

        System.out.println(html)
    }

    @Test
    fun testDownloadLink(){
        val tvShows4Mobile = TvShows4Mobile()

        //"http://d12.tvshows4mobile.com/Silicon%20Valley/Season%2005/Silicon%20Valley%20-%20S05E05%20(TvShows4Mobile.Com).mp4"
        val lastest_episodes = tvShows4Mobile.getLastestEpisodes()

        tvShows4Mobile.getLastestEpisodesLinks(lastest_episodes.get(0))

        val document = Jsoup.connect(lastest_episodes[0].link?.get(0)?.link).userAgent("mobile").data().post()
        val loc = document.location()

        System.out.println(loc)


    }

    fun downloadLink(){
        val doc = Jsoup.connect("http://tvshows4mobile.com/areyouhuman.php?fid=48323").userAgent("Mozilla").post()

        System.out.println(doc.location())
    }
}
