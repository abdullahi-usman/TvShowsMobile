package com.dahham.tvshowmobile.fragments

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dahham.tvshowmobile.Models.Link
import com.dahham.tvshowmobile.Models.Show
import com.dahham.tvshowmobile.R
import com.dahham.tvshowmobile.ShowDetailsActivity
import com.dahham.tvshowmobile.ViewModels.TvShows4MobileViewModel
import kotlinx.android.synthetic.main.movie.view.*
import kotlinx.android.synthetic.main.show_contents_container.*
import java.io.File

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileAllShows: AbstractShowsFragment<Show>() {


    val SHOWS = "shows"

    override fun getLiveData(): LiveData<List<Show>> {
        return lifecycle.showViewModel.shows
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null && savedInstanceState.containsKey(SHOWS)){

            val saved_all_shows = savedInstanceState.getParcelableArray(SHOWS)

            if (saved_all_shows != null) {
                lifecycle.showViewModel.shows.value = saved_all_shows.toList() as? List<Show>
            }
        }
    }

    fun getDownloadPath(): String?{
        return context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (recycler_shows_container.adapter == null) {
            var layout_manager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

            if (resources.displayMetrics.widthPixels >= 2560f){
                layout_manager = GridLayoutManager(context, 6, GridLayoutManager.VERTICAL, false)
            } else if (resources.displayMetrics.widthPixels >= 1920f){
                layout_manager = GridLayoutManager(context, 5, GridLayoutManager.VERTICAL, false)
            } else if (resources.displayMetrics.widthPixels >= 1280f){
                layout_manager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
            }else if (resources.displayMetrics.widthPixels >= 768f){
                layout_manager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            }

            recycler_shows_container.layoutManager = layout_manager

            recycler_shows_container.adapter = AllShowsAdapter()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            onStart()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArray(SHOWS, data.value?.toTypedArray())
    }

    override fun getDownloadLink(episode: Show): List<Link>? {
        return null
    }

    override fun onNext(item: Show, msg: String) {
        val downloaded_file = getDownloadPath() + "/" + item.name

        if (File(downloaded_file).exists()) {
            item.poster = downloaded_file
        }

        super.onNext(item, msg)
    }

    override fun onCompleted() {
        super.onCompleted()

        val listener = object : TvShows4MobileViewModel.ShowsViewModelListener<Show>{
            override fun onNext(item: Show, msg: String) {
                if (item.poster != null || item.poster?.isEmpty() == false) {
                    val item_index = data.value!!.indexOf(item)
                    recycler_shows_container.adapter?.notifyItemChanged(item_index)
                }
            }
        }

        if (data.value != null) {
            val shows = ArrayList<Show>()
            shows.addAll(data.value?.toTypedArray()!!)
            data.value?.forEach {

                if (it.poster != null && it.poster?.isEmpty() == false){
                    shows.remove(it)
                }

            }

            if (shows.size > 0) {
                lifecycle.showViewModel.getShowsProperties(*shows.toTypedArray(), listener = listener, photoDownloadLocation = getDownloadPath())
            }
        }
    }

    fun onShowClick(show: Show, view: View) {

        fun startShowDetailsActivity(){
            val activityCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity as Activity, Pair(view.movie_poster, "poster"))

            val intent = Intent(activity, ShowDetailsActivity::class.java)
            intent.putExtra(ShowDetailsActivity.DETAILS, show)

            ActivityCompat.startActivity(context!!, intent, activityCompat.toBundle())
        }

        if (show.description == null || show.poster == null || show.casts == null){
            val progressDialog = AlertDialog.Builder(context!!).setView(R.layout.please_wait_dialog).setCancelable(false).create()
            progressDialog.show()
            lifecycle.showViewModel.getShowsProperties(show, listener = object :TvShows4MobileViewModel.ShowsViewModelListener<Show>{
                override fun onNext(item: Show, msg: String) {
                    super.onNext(item, msg)

                    val downloaded_file = getDownloadPath() + "/" + item.name

                    if (File(downloaded_file).exists()) {
                        item.poster = downloaded_file
                    }

                    startShowDetailsActivity()
                }

                override fun onError(throwable: Throwable) {
                    super.onError(throwable)
                    progressDialog.dismiss()
                    Snackbar.make(view, R.string.error_message, Snackbar.LENGTH_LONG).show()
                }

                override fun onCompleted() {
                    super.onCompleted()
                    progressDialog.dismiss()
                }

            }, photoDownloadLocation = null)

        }else {
            startShowDetailsActivity()
        }

    }

    override fun load() {
        if (userVisibleHint) {
            lifecycle.showViewModel.loadAllTVShows(this)
        }
    }


    override fun state(): TvShows4MobileViewModel.STATE {
        return lifecycle.showViewModel.getState(TvShows4MobileViewModel.TYPE.ALL_TV_SHOWS)
    }


    private inner class AllShowsAdapter : RecyclerView.Adapter<ViewHolder>() {

        override fun getItemCount(): Int {
            return data.value?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.movie, parent, false))
        }

        override fun onViewRecycled(holder: ViewHolder) {
            super.onViewRecycled(holder)
            holder._itemView.movie_poster.setImageDrawable(null)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val show = data.value?.get(position)

            holder._itemView.movie_name.text = show?.name

            if (show?.poster != null && File(show.poster).exists()) {
                glide.load(show.poster).into(holder._itemView.movie_poster)
            }

            holder._itemView.setOnClickListener{
                data.value?.get(recycler_shows_container.getChildAdapterPosition(it))

                onShowClick(show!!, it )
            }

        }

    }
}