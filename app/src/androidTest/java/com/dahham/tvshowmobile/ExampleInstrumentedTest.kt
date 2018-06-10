package com.dahham.tvshowmobile

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.Models.TvShows4Mobile
import com.dahham.tvshowmobile.utils.DownloadStore
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

        //val document = Jsoup.connect(lastest_episodes[0].link?.get(0)?.link).userAgent("mobile").data().post()
        //val loc = document.location()

        //System.out.println(loc)


    }

    fun downloadLink(){
        val doc = Jsoup.connect("http://tvshows4mobile.com/areyouhuman.php?fid=48323").userAgent("Mozilla").post()

        System.out.println(doc.location())
    }

    @Test
    fun testJsonStore(){
        val appContext = InstrumentationRegistry.getTargetContext()

        val loc = "/sdcrad/b.mp3"
        DownloadStore.writeOutDownloadedFile(appContext, Episode("Blindspot", "Season 1", "Episode 10", loc),
                Episode("Blindspot", "Season 1", "Episode 11", link = loc),
                Episode("Blindspot", "Season 1", "Episode 12", link = loc),
                Episode("Blindspot", "Season 2", "Episode 1", link = loc),
                Episode("The Flash", "Season 1", "Episode 11", link = loc),
                Episode("The Flash", "Season 1", "Episode 12", link = loc),
                Episode("The Flash", "Season 2", "Episode 1", link = loc),
                Episode("Vince", "Season 1", "Episode 12", link = loc),
                Episode("Vince", "Season 2", "Episode 1", link = loc),
                Episode("Vince", "Season 1", "Episode 11", link = loc))
    }


    @Test
    fun testNotification(){

        val context = InstrumentationRegistry.getTargetContext()
        val notificationBuilder = NotificationCompat.Builder(context)
        val show_name = "Hello WOrld"
        val season_episode = "Abdllahi Usman"

        notificationBuilder.setContentIntent(PendingIntent.getActivity(context, 0, Intent(Intent.ACTION_VIEW, Uri.parse("file:///sdcard/tv.mp3")), PendingIntent.FLAG_ONE_SHOT))
        notificationBuilder.setSmallIcon(R.drawable.notification_icon)
        notificationBuilder.setContentTitle(show_name).setContentText(season_episode)

        NotificationManagerCompat.from(context).notify(null, 57549, notificationBuilder.build())
    }

    @Test
    fun testSorageIds(){
        val context = InstrumentationRegistry.getTargetContext()

        val downloadStore = DownloadStore.instnace(context)

        assert(downloadStore.removeFromStore(93))

    }
}
