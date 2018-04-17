package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dahham.tvshowmobile.LifeCycleObservers.TvShows4MobileLifeCycle
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.Models.Series
import com.dahham.tvshowmobile.adapters.SeasonTabAdapter
import com.dahham.tvshowmobile.adapters.TvAdapter
import kotlinx.android.synthetic.main.show_contents_container.*

/**
 * Created by dahham on 4/16/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class SeasonTabFragment: AbstractShowsFragment<Episode>(){

    lateinit var series : Series

    companion object {
        val SEASON = "season"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        series = arguments?.getParcelable(SEASON) as Series
    }

    override fun onStart() {
        super.onStart()
        if (recycler_shows_container.adapter == null || !(recycler_shows_container.adapter as TvAdapter<*>).hasViewHolders()) {

            recycler_shows_container.adapter = SeasonTabAdapter(series, TvShows4MobileLifeCycle(this))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_shows_container.layoutManager = LinearLayoutManager(context)
    }

}