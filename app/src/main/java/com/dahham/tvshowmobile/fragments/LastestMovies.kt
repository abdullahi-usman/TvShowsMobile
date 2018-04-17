package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.view.View
import com.dahham.tvshowmobile.LifeCycleObservers.TvmdbShowLifeCycle
import com.dahham.tvshowmobile.Models.LastestMovie
import com.dahham.tvshowmobile.adapters.LastestMoviesAdapter
import com.dahham.tvshowmobile.adapters.TvAdapter
import kotlinx.android.synthetic.main.show_contents_container.*

/**
 * Created by dahham on 4/3/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class LastestMovies : AbstractShowsFragment<LastestMovie>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (recycler_shows_container.adapter == null || !(recycler_shows_container.adapter as TvAdapter<*>).hasViewHolders()) {

            recycler_shows_container.adapter = LastestMoviesAdapter(TvmdbShowLifeCycle(this))
        }

    }

}