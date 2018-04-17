package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dahham.tvshowmobile.R


/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileFragment : AbstractShowsContainerFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (adapter == null) {
            adapter = ShowAdapter(childFragmentManager)
        }
    }

    override fun childSelected(position: Int) {
        super.childSelected(position)
        (adapter?.getItem(position) as AbstractShowsFragment<*>).onFragmentSelected()
    }

    override var adapter: ShowsAdapter? = null

    private inner class ShowAdapter(fm: FragmentManager) : AbstractShowsContainerFragment.ShowsAdapter(fm) {

        val latestEpisodes = TvShows4MobileLastestEpisodes()
        val allShows = TvShows4MobileAllShows()

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> latestEpisodes
                1 -> allShows
                else -> latestEpisodes
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {

                0 -> {
                    getString(R.string.tvmdb_category_lastest_tvshow)
                }

                1 -> {
                    getString(R.string.tvmdb_category_tvshows)
                }

                else -> {
                    getString(R.string.tvmdb_category_lastest_tvshow)
                }
            }
        }

    }
}