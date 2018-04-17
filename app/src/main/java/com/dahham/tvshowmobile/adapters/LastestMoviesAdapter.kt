package com.dahham.tvshowmobile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dahham.tvshowmobile.LifeCycleObservers.TvmdbShowLifeCycle
import com.dahham.tvshowmobile.Models.LastestMovie
import com.dahham.tvshowmobile.R
import kotlinx.android.synthetic.main.lastest_movie.view.*

/**
 * Created by dahham on 4/3/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class LastestMoviesAdapter(lifeCycle: TvmdbShowLifeCycle<LastestMovie>) : TvAdapter<LastestMovie>(lifeCycle, lifeCycle.getLastestMovies(lifeCycle.fragment)) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(lifecycle.fragment.context).inflate(R.layout.lastest_movie, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = data.value?.get(position)

        if (movie != null) {
            holder._itemView.lastest_movie_name.text = movie.name
            holder._itemView.lastest_movie_description.text = movie.description

            val cast = StringBuilder()

            if (movie.casts != null) {
                for (_cast in movie.casts) {
                    cast.append("$cast\n")
                }
            }


            holder._itemView.lastest_movie_cast.text = cast

            glide.load(movie.poster).into(holder._itemView.lastest_movie_poster)
        }
    }

    override fun load() {
        lifecycle.showViewModel.loadLastestMovies(lifecycle.fragment)
    }
}