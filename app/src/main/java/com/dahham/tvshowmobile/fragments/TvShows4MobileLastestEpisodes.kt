package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dahham.tvshowmobile.LifeCycleObservers.TvShows4MobileLifeCycle
import com.dahham.tvshowmobile.Models.LastestEpisode
import com.dahham.tvshowmobile.adapters.LastestEpisodesAdapter
import com.dahham.tvshowmobile.adapters.TvAdapter
import kotlinx.android.synthetic.main.show_contents_container.*

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileLastestEpisodes: AbstractShowsFragment<LastestEpisode>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_shows_container.layoutManager = LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()
        if (recycler_shows_container.adapter == null || !(recycler_shows_container.adapter as TvAdapter<*>).hasViewHolders()) {
            recycler_shows_container.adapter = LastestEpisodesAdapter(TvShows4MobileLifeCycle(this))
        }
    }

    override fun onFragmentSelected() {
        super.onFragmentSelected()

    }
}