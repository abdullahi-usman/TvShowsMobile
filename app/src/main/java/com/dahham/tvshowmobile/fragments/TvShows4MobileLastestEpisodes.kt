package com.dahham.tvshowmobile.fragments

import android.app.DownloadManager
import android.arch.lifecycle.LiveData
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.Models.LastestEpisode
import com.dahham.tvshowmobile.Models.Link
import com.dahham.tvshowmobile.R
import com.dahham.tvshowmobile.ViewModels.TvShows4MobileViewModel
import com.dahham.tvshowmobile.utils.DownloadStore
import kotlinx.android.synthetic.main.lastest_episode.view.*
import kotlinx.android.synthetic.main.show_contents_container.*
import java.io.File

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

        val download_observer = object : DownloadStore.DownloadStoreListener {
            override fun onDownloadStateChanged(episode: Episode, status: Int) {

                val latest_episodes = lifecycle.showViewModel.lastestEpisodes.value
                latest_episodes?.forEach {
                    if (it.show_name.matches(Regex(episode.show_name)) && it.season_name?.matches(Regex(episode.season_name!!)) == true && it.episode_name?.matches(Regex(episode.episode_name!!)) == true) {

                        val view_holder = recycler_shows_container.getChildViewHolder(recycler_shows_container.getChildAt(latest_episodes.indexOf(it))) as ViewHolder

                        recycler_shows_container.adapter.notifyItemChanged(recycler_shows_container.getChildAdapterPosition(view_holder._itemView))
                    }
                }
            }
        }

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

            fun registerDownloadListener(lastestEpisode: LastestEpisode) {
                //if (downloadStore.hasRegisteredListener(lastestEpisode)) return

                downloadStore.registerOnDownloadChanged(lastestEpisode, object : DownloadStore.DownloadStoreListener {
                    override fun onDownloadStateChanged(episode: Episode, status: Int) {

                        Toast.makeText(context!!, "updating progress for ${episode.season_name}", Toast.LENGTH_LONG).show()

                        var episode_holder = holder

                        if (episode_holder.episode?.equals(episode) == false) {
                            for (pos in 0..recycler_shows_container.childCount) {

                                val view_child = recycler_shows_container.getChildAt(pos)
                                        ?: continue

                                val _holder = (recycler_shows_container.getChildViewHolder(view_child) as ViewHolder)
                                if (_holder.episode?.equals(episode) == true) {
                                    episode_holder = _holder
                                }

                            }
                        }

                        if (episode_holder == null) return

                        //if (episode_holder.episode?.equals(episode) == true) {

                            if (status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_PAUSED) {
                                change_download_icon(episode_holder, R.id.lastest_episode_progressbar)
                            } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                change_download_icon(episode_holder, R.id.lastest_episode_play)
                                episode_holder._itemView.lastest_episode_play.setOnClickListener {
                                    val downloaded_episode = downloadStore.getDownloadedFile(episode)
                                            ?: return@setOnClickListener

                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloaded_episode))

                                    try {
                                        startActivity(intent)
                                    } catch (e: ActivityNotFoundException) { }
                                }
                            } else if (status == DownloadManager.STATUS_FAILED || status == -1) {
                                change_download_icon(episode_holder, R.id.lastest_episode_download)
                            }

                        //}

                    }
                })

            }


            val downloaded_index = downloaded_episodes.indexOf(lastestEpisode!!)
            val download_id = downloadStore.getDownloadIdForEpisode(lastestEpisode)

            if (downloaded_index != -1 || download_id != -1L){
                Toast.makeText(context, "download_id $download_id &&  downloaded_index $downloaded_index", Toast.LENGTH_LONG).show()
            }

            if (download_id != -1L) {

                registerDownloadListener(lastestEpisode)
                downloadStore.postDownloadStatus(lastestEpisode, download_id)

            } else if (downloaded_index != -1 && File(downloaded_episodes[downloaded_index].link).exists()){

                change_download_icon(holder, R.id.lastest_episode_play)

                holder._itemView.lastest_episode_play.setOnClickListener {

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloaded_episodes[downloaded_index].link))

                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) { }

                }

            }else {
                change_download_icon(holder, R.id.lastest_episode_download)
            }

            holder._itemView.lastest_episode_download.setOnClickListener {
                registerDownloadListener(lastestEpisode)

                download(lastestEpisode)
            }

        }

        override fun onViewRecycled(holder: ViewHolder) {
            super.onViewRecycled(holder)
            val pos = recycler_shows_container.getChildAdapterPosition(holder._itemView)

            if (pos != -1) {
                downloadStore.removeOnDownloadChanged(data.value?.get(pos)!!)
            }
        }

        fun change_download_icon(holder: ViewHolder, id: Int) {
            while (holder._itemView.lastest_episode_download_container.isFlipping.not() && holder._itemView.lastest_episode_download_container.currentView.id != id) {

                holder._itemView.lastest_episode_download_container.showNext()

            }
        }
    }
}