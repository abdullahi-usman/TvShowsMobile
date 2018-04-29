package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dahham.tvshowmobile.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_source.*


/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileFragment : Fragment() {

    private var adapter: ShowAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (adapter == null) {
            adapter = ShowAdapter(childFragmentManager)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (show_tab_container.adapter == null) {
            show_tab_container.adapter = adapter

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
    }

    override fun onStart() {
        super.onStart()
        adView.loadAd(AdRequest.Builder().build())
    }

    private inner class ShowAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

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