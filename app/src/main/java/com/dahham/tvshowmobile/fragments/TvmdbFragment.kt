package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dahham.tvshowmobile.R

/**
 * A placeholder fragment containing a simple view.
 */
class TvmdbFragment : AbstractShowsContainerFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && view == null) {
            adapter = ShowsAdapter(childFragmentManager)

        }
    }

    override var adapter: AbstractShowsContainerFragment.ShowsAdapter? = null

    private inner class ShowsAdapter(fm: FragmentManager) : AbstractShowsContainerFragment.ShowsAdapter(fm) {

        private val latestEpisodes = LastestEpisodes()
        private val latestMovies = LastestMovies()
        private val allMovies = AllMovies()
        private val allShows = AllShows()

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    latestMovies
                }

                1 -> {
                    allMovies
                }

                2 -> {
                    latestEpisodes
                }

                3 -> {
                    allShows
                }

                else -> {
                    latestEpisodes
                }
            }
        }

        override fun getCount(): Int {
            return 4
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> {
                    getString(R.string.tvmdb_category_lastest_movies)
                }

                1 -> {
                    getString(R.string.tvmdb_category_all_movies)
                }

                2 -> {
                    getString(R.string.tvmdb_category_lastest_tvshow)
                }

                3 -> {
                    getString(R.string.tvmdb_category_tvshows)
                }

                else -> {
                    getString(R.string.tvmdb_category_lastest_movies)
                }
            }
        }
    }
}
