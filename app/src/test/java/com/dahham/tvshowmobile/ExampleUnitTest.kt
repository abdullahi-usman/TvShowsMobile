package com.dahham.tvshowmobile

import com.dahham.tvshowmobile.Models.LastestEpisode
import com.dahham.tvshowmobile.ViewModels.TvShows4MobileViewModel
import info.movito.themoviedbapi.TmdbApi
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun testTmdb(){
        val tvmdb = TmdbApi ("4806d249fa0741001ec6d3c098021c87")
        val movie = tvmdb.movies.latestMovie
        print(movie.title)
    }


    @Test
    fun testTvShowMobile(){
        val tvShows4Mobile = TvShows4MobileViewModel()

        tvShows4Mobile.loadLastestEpisodes(listner = object: AbstractShowViewModel.ShowsViewModelListener<LastestEpisode>{
            override fun onStarted() {

            }

            override fun onNext(item: LastestEpisode, msg: String) {
                System.out.println(item.name)
            }

            override fun onError(throwable: Throwable) {
                System.out.println(throwable.message)
                throwable.printStackTrace()
            }

            override fun onCompleted() {

            }

        })
    }
}
