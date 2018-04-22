package com.dahham.tvshowmobile.fragments

import android.arch.lifecycle.LiveData
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
import kotlinx.android.synthetic.main.episode.view.*
import kotlinx.android.synthetic.main.show_contents_container.*

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

            recycler_shows_container.layoutManager = LinearLayoutManager(context)

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

    override fun getDownloadLink(episode: Episode): List<Link>? {
        return lifecycle.showViewModel.getEpisodeLink(episode)
    }

    private inner class SeasonTabAdapter : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.episode, parent, false))
        }

        override fun getItemCount(): Int {
            return series.episodes?.size ?: 0
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val episode = series.episodes?.get(position)
            holder._itemView.episode_name.text = episode?.name

            holder._itemView.episode_link.setOnClickListener {
                download(show_name, series.season, episode?.name!!, episode)
            }
        }

    }
}