package com.dahham.tvshowmobile

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.dahham.tvshowmobile.Models.TvShows4Mobile
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

        val _show = show.getAllShows()[0]

        show.getShowProperties(_show)

        val serie = _show.series?.get(0)!!
        serie.episodes = show.getEpisodes(serie)

        val links = show.getEpisodeLink(serie.episodes?.get(0)!!)


        System.out.println(links[0].link)
    }

}
