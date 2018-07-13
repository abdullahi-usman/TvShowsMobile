package com.dahham.tvshowmobile.fragments

import android.arch.lifecycle.LiveData
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.Models.LastestEpisode
import com.dahham.tvshowmobile.Models.Link
import com.dahham.tvshowmobile.R
import com.dahham.tvshowmobile.ViewModels.TvShows4MobileViewModel
import kotlinx.android.synthetic.main.lastest_episode.view.*
import kotlinx.android.synthetic.main.show_contents_container.*

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileLastestEpisodes : AbstractShowsFragment<LastestEpisode>() {


    override fun getLiveData(): LiveData<List<LastestEpisode>> {
        return lifecycle.showViewModel.lastestEpisodes
    }

    val LATEST_EPISODE = "lastest_episode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null && savedInstanceState.containsKey(LATEST_EPISODE)) {

            val saved_latest_episodes = savedInstanceState.getParcelableArray(LATEST_EPISODE)

            if (saved_latest_episodes != null) {
                lifecycle.showViewModel.lastestEpisodes.value = saved_latest_episodes.toList() as List<LastestEpisode>
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (recycler_shows_container.adapter == null) {
            val layout_manager = LinearLayoutManager(context)

            recycler_shows_container.layoutManager = layout_manager
            recycler_shows_container.adapter = LastestEpisodesAdapter()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArray(LATEST_EPISODE, data.value?.toTypedArray())
    }

    override fun load() {
        lifecycle.showViewModel.loadLastestEpisodes(this)
    }

    override fun state(): TvShows4MobileViewModel.STATE {
        return lifecycle.showViewModel.getState(TvShows4MobileViewModel.TYPE.LASTEST_TV_EPISODES)
    }

    override fun getDownloadLink(episode: Episode): List<Link>? {
        lifecycle.showViewModel.getLastestEpisodesLinks(episode as LastestEpisode)

        return episode.download_links
    }

    private inner class LastestEpisodesAdapter : RecyclerView.Adapter<ViewHolder>() {

        override fun getItemCount(): Int {
            return data.value?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.lastest_episode, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val lastestEpisode = data.value?.get(position)

            holder.episode = lastestEpisode

            holder._itemView.lastest_episode_name.text = lastestEpisode?.show_name

            holder._itemView.lastest_episode_episode.text = "${lastestEpisode?.season_name} ${lastestEpisode?.episode_name}"

            holder._itemView.lastest_date_added.text = getString(R.string.show_added_date, lastestEpisode?.dateAdded)

//            holder._itemView.lastest_episode_download.setOnClickListener {}

            holder._itemView.lastest_episode_container.setOnClickListener {
                if (holder.episode != null) {
                    download (holder.episode!!)
                }
            }

        }

        override fun onViewRecycled(holder: ViewHolder) {
            super.onViewRecycled(holder)
            val pos = recycler_shows_container.getChildAdapterPosition(holder._itemView)

            if (pos != -1) {

            }
        }

        fun change_download_icon(holder: ViewHolder, id: Int) {
            while (holder._itemView.lastest_episode_download_container.isFlipping.not() && holder._itemView.lastest_episode_download_container.currentView.id != id) {

                holder._itemView.lastest_episode_download_container.showNext()

            }
        }
    }
}