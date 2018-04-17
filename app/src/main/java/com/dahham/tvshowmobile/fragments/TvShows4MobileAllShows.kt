package com.dahham.tvshowmobile.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.dahham.tvshowmobile.Models.Show
import com.dahham.tvshowmobile.ShowDetailsActivity
import com.dahham.tvshowmobile.adapters.TvAdapter
import kotlinx.android.synthetic.main.movie.view.*
import kotlinx.android.synthetic.main.show_contents_container.*

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileAllShows: AbstractShowsFragment<Show>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_shows_container.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onStart() {
        super.onStart()

        if (recycler_shows_container.adapter == null || !(recycler_shows_container.adapter as TvAdapter<*>).hasViewHolders()) {

            //recycler_shows_container.adapter = AllShowsAdapter(TvShows4MobileLifeCycle(this))
        }
    }

    override fun onItemClick(show: Show, view: View) {
        super.onItemClick(show, view)

        val activityCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity as Activity, Pair(view.movie_poster, "poster"))

        val intent = Intent(activity, ShowDetailsActivity::class.java)
        intent.putExtra(ShowDetailsActivity.DETAILS, show)

        ActivityCompat.startActivity(context!!, intent, activityCompat.toBundle())
    }

}