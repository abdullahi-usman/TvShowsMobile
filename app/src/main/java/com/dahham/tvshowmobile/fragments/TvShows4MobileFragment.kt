package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dahham.tvshowmobile.R
import com.google.android.gms.ads.AdListener
import kotlinx.android.synthetic.main.fragment_source.*


/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (show_tab_container.adapter == null) {
            show_tab_container.adapter = ShowAdapter(childFragmentManager)

            show_tab_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(shows_categories_tab))
            shows_categories_tab.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(show_tab_container))
        }

        adView.visibility = View.GONE

        adView.adListener = object : AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
                adView?.visibility = View.VISIBLE
            }
        }
        adView.loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
    }

    override fun onStart() {
        super.onStart()
        //adView.loadAd(AdRequest.Builder().build())
    }

    private inner class ShowAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var latestEpisodes = TvShows4MobileLastestEpisodes()
        var allShows = TvShows4MobileAllShows()

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

        override fun saveState(): Parcelable? {
            val bundle = Bundle()
            if (childFragmentManager.fragments.contains(latestEpisodes)) {
                childFragmentManager.putFragment(bundle, "latest_episode", latestEpisodes)
            }

            if(childFragmentManager.fragments.contains(allShows)) {
                childFragmentManager.putFragment(bundle, "all_shows", allShows)
            }

            return bundle
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
            super.restoreState(state, loader)
            if (childFragmentManager.fragments.size > 0 && state is Bundle) {
                latestEpisodes = childFragmentManager.getFragment(state, "latest_episode") as TvShows4MobileLastestEpisodes
                allShows = childFragmentManager.getFragment(state, "all_shows") as TvShows4MobileAllShows
            }

        }

    }
}