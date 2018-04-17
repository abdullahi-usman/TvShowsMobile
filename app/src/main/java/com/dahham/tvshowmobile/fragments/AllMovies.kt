package com.dahham.tvshowmobile.fragments

import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.dahham.tvshowmobile.LifeCycleObservers.TvmdbShowLifeCycle
import com.dahham.tvshowmobile.Models.Movie
import com.dahham.tvshowmobile.adapters.AllMoviesAdapter
import com.dahham.tvshowmobile.adapters.TvAdapter
import kotlinx.android.synthetic.main.show_contents_container.*

/**
 * Created by dahham on 4/3/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class AllMovies : AbstractShowsFragment<Movie>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_shows_container.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        if (recycler_shows_container.adapter == null || !(recycler_shows_container.adapter as TvAdapter<*>).hasViewHolders()) {

            recycler_shows_container.adapter = AllMoviesAdapter(TvmdbShowLifeCycle(this))
        }

    }

}
