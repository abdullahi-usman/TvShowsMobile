package com.dahham.tvshowmobile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dahham.tvshowmobile.LifeCycleObservers.AbstractShowLifeCycle
import com.dahham.tvshowmobile.Models.Movie
import com.dahham.tvshowmobile.R
import kotlinx.android.synthetic.main.movie.view.*

/**
 * Created by dahham on 4/3/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */

class AllMoviesAdapter(lifecycle: AbstractShowLifeCycle<Movie>): TvAdapter<Movie>(lifecycle, lifecycle.getAllMovies(lifecycle.fragment))  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(lifecycle.fragment.context).inflate(R.layout.movie, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = data.value?.get(position)


            holder._itemView.movie_name.text = movie?.name
            //holder._itemView.movie_description.text = movie?.description

            glide.load(movie?.poster).into(holder._itemView.movie_poster)
    }

    override fun load() {
        lifecycle.showViewModel.loadAllMovies(lifecycle.fragment)
    }
}