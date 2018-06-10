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
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.Models.Link
import com.dahham.tvshowmobile.Models.Series
import com.dahham.tvshowmobile.R
import com.dahham.tvshowmobile.ViewModels.TvShows4MobileViewModel
import com.dahham.tvshowmobile.utils.DownloadStore
import kotlinx.android.synthetic.main.episode.view.*
import kotlinx.android.synthetic.main.lastest_episode.view.*
import kotlinx.android.synthetic.main.show_contents_container.*
import java.io.File

/**
 * Created by dahham on 4/16/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class SeasonTabFragment : AbstractShowsFragment<Episode>() {

    lateinit var show_name: String
    lateinit var series: Series

    companion object {
        val SEASON = "season"
        val SHOW_NAME = "show_name"
    }

    override fun getLiveData(): LiveData<List<Episode>> {
        return lifecycle.showViewModel.episodes
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            show_name = arguments?.getString(SHOW_NAME)!!
            series = arguments?.getParcelable(SEASON) as Series
        } else {
            show_name = savedInstanceState.getString(SHOW_NAME)!!
            series = savedInstanceState.getParcelable(SEASON) as Series
        }

        if (series.episodes != null) {
            lifecycle.showViewModel.episodes.value = series.episodes
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (recycler_shows_container.adapter == null) {

            val layout_manager = LinearLayoutManager(context)

            recycler_shows_container.layoutManager = layout_manager
            recycler_shows_container.adapter = SeasonTabAdapter()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SEASON, series)
        outState.putString(SHOW_NAME, show_name)
    }

    override fun load() {
        lifecycle.showViewModel.loadEpisodes(series, this)
    }

    override fun state(): TvShows4MobileViewModel.STATE {
        return lifecycle.showViewModel.getState(TvShows4MobileViewModel.TYPE.EPISODE)
    }

    override fun getDownloadLink(episode: Episode): List<Link>? {
        return lifecycle.showViewModel.getEpisodeLink(episode)
    }

    private inner class SeasonTabAdapter : RecyclerView.Adapter<ViewHolder>() {

        val downloadStore = DownloadStore.instnace(context!!)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.episode, parent, false))
        }

        override fun getItemCount(): Int {
            return series.episodes?.size ?: 0
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val episode = series.episodes?.get(position)

            holder.episode = episode
            holder._itemView.episode_name.text = episode?.episode_name

            holder._itemView.episode_download_status.text = getString(R.string.download)

            fun registerDownloadListener(episode: Episode) {
                //if (downloadStore.hasRegisteredListener(episode)) return

                downloadStore.registerOnDownloadChanged(episode, object : DownloadStore.DownloadStoreListener {
                    override fun onDownloadStateChanged(episode: Episode, status: Int) {

                        var episode_holder: ViewHolder? = null
                        for (pos in 0..recycler_shows_container.childCount){

                            val view_child = recycler_shows_container.getChildAt(pos) ?: continue

                            val _holder = (recycler_shows_container.getChildViewHolder(view_child) as ViewHolder)
                            if (_holder.episode?.equals(episode) == true){
                                episode_holder = _holder
                            }

                        }

                        if (episode_holder == null) return

                        //if (episode_holder.episode?.equals(episode) == true) {
                            if (status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_PAUSED) {
                                change_download_icon(episode_holder, R.id.episode_progressbar)
                                episode_holder._itemView.episode_download_status.text = getString(R.string.downloading)
                            } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                change_download_icon(episode_holder, R.id.episode_play)
                                holder._itemView.episode_download_status.text = getString(R.string.play)
                                holder._itemView.episode_download_container.setOnClickListener {

                                    val downloaded_episode = downloadStore.getDownloadedFile(episode)
                                            ?: return@setOnClickListener

                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloaded_episode))

                                    try {
                                        startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                    }

                                }

                            } else if (status == DownloadManager.STATUS_FAILED || status == -1) {
                                change_download_icon(episode_holder, R.id.episode_download)
                            }
                        //}

                    }
                })

            }

            val downloaded_index = downloaded_episodes.indexOf(episode)
            val download_id = downloadStore.getDownloadIdForEpisode(episode!!)

            if (download_id != -1L) {
                registerDownloadListener(episode)
                downloadStore.postDownloadStatus(episode, download_id)
            } else if (downloaded_index != -1 && File(downloaded_episodes[downloaded_index].link).exists()) {

                change_download_icon(holder, R.id.episode_play)

                holder._itemView.lastest_episode_play.setOnClickListener {

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloaded_episodes[downloaded_index].link))

                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                    }

                }


            } else {
                change_download_icon(holder, R.id.episode_download)
            }

            holder._itemView.episode_download.setOnClickListener {
                registerDownloadListener(episode)

                download(episode)
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
            while (holder._itemView.episode_link_container.isFlipping.not() && holder._itemView.episode_link_container.currentView.id != id) {

                holder._itemView.episode_link_container.showNext()

            }
        }

    }
}