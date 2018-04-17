package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dahham.tvshowmobile.R
import kotlinx.android.synthetic.main.fragment_source.*

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
abstract class AbstractShowsContainerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tmdb_tab_container.adapter = adapter

        tmdb_tab_container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                childSelected(position)
            }

        })

        //tmdb_tab_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tvmdb_tab_categories))
        //tvmdb_tab_categories.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(tmdb_tab_container))
    }

    abstract var adapter: ShowsAdapter?

    abstract inner class ShowsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm)

    open fun childSelected(position: Int){}
}