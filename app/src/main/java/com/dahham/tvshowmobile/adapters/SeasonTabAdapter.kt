package com.dahham.tvshowmobile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dahham.tvshowmobile.LifeCycleObservers.AbstractShowLifeCycle
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.Models.Series
import com.dahham.tvshowmobile.R
import kotlinx.android.synthetic.main.episode.view.*

/**
 * Created by dahham on 4/16/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class SeasonTabAdapter(val series: Series, lifecycle: AbstractShowLifeCycle<Episode>): TvAdapter<Episode>(lifecycle, lifecycle.getEpisodes(series, lifecycle.fragment)){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(lifecycle.fragment.context).inflate(R.layout.episode, parent, false))
    }

    override fun getItemCount(): Int {
        return series.episodes?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = data.value?.get(position)
        holder._itemView.episode_name.text = episode?.name
    }

    override fun load() {
        lifecycle.showViewModel.loadEpisodes(series, lifecycle.fragment)
    }
}