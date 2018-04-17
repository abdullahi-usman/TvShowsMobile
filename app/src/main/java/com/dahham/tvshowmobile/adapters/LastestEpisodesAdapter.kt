package com.dahham.tvshowmobile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dahham.tvshowmobile.LifeCycleObservers.AbstractShowLifeCycle
import com.dahham.tvshowmobile.Models.LastestEpisode
import com.dahham.tvshowmobile.R
import kotlinx.android.synthetic.main.lastest_episode.view.*

/**
 * Created by dahham on 4/3/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class LastestEpisodesAdapter(lifeCycle: AbstractShowLifeCycle<LastestEpisode>) :  TvAdapter<LastestEpisode>(lifeCycle, lifeCycle.getLastestEpisodes(lifeCycle.fragment))  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(lifecycle.fragment.context).inflate(R.layout.lastest_episode, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lastestEpisode = data.value?.get(position)


        holder._itemView.lastest_episode_name.text = lastestEpisode?.name

        holder._itemView.lastest_episode_episode.text = "Season ${lastestEpisode?.season} Episode ${lastestEpisode?.episode}"

        holder._itemView.lastest_date_added.text = lastestEpisode?.dateAdded

        glide.load(lastestEpisode?.poster).into(holder._itemView.lastest_episode_poster)
    }

    override fun load() {
        lifecycle.showViewModel.loadLastestEpisodes(lifecycle.fragment)
    }
}