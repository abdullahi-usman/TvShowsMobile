package com.dahham.tvshowmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dahham.tvshowmobile.LifeCycleObservers.AbstractShowLifeCycle
import com.dahham.tvshowmobile.Models.Show
import com.dahham.tvshowmobile.R
import kotlinx.android.synthetic.main.movie.view.*
import kotlinx.android.synthetic.main.show_contents_container.*

/**
 * Created by dahham on 4/3/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class AllShowsAdapter(lifecycle: AbstractShowLifeCycle<Show>) : TvAdapter<Show>(lifecycle, lifecycle.getAllTVShows(lifecycle.fragment)) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(lifecycle.fragment.context).inflate(R.layout.movie, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val show = data.value?.get(position)


        holder._itemView.movie_name.text = show?.name
            //holder._itemView.tv_show_description.text = show?.description

        glide.load(show?.poster).into(holder._itemView.movie_poster)

        holder._itemView.setOnClickListener(onItemClick)

    }

    private val onItemClick = object: View.OnClickListener{
        override fun onClick(v: View?) {
            val show = data.value?.get(lifecycle.fragment.recycler_shows_container.getChildAdapterPosition(v))

            lifecycle.fragment.onItemClick(show!!, v!!)
        }
    }

    override fun load() {
        lifecycle.showViewModel.loadAllTVShows(lifecycle.fragment)
    }
}