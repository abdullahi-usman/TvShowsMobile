package com.dahham.tvshowmobile.fragments

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.*
import android.widget.SimpleAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.R
import com.dahham.tvshowmobile.utils.DownloadStore
import kotlinx.android.synthetic.main.downloads_item_view.view.*
import kotlinx.android.synthetic.main.downloads_view.*
import java.io.File
import java.util.*

/**
 * Created by dahham on 5/4/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class DownloadsFragment : Fragment(){
    private var sorted_episodes = Hashtable<String, Hashtable<String, ArrayList<Episode>>>()

    lateinit var  glide : RequestManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glide = Glide.with(context!!)
        queryDownloadedEpisodes()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.downloads_pref, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.downloads_pref_clear){
            DownloadStore.clearDownloadedEpisodes(context!!)
            queryDownloadedEpisodes()
            downloads_recyclerview.adapter?.notifyDataSetChanged()
        }

        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.downloads_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sorted_episodes.size > 0){
            if (downloads_view_viewswitcher.currentView.id != R.id.downloads_recyclerview){
                downloads_view_viewswitcher.showNext()
            }
        }


        if (downloads_recyclerview.adapter == null) {
            downloads_recyclerview.adapter = DownloadsFragmentAdapter()
        }
    }

    private fun queryDownloadedEpisodes(){
        if (sorted_episodes.size <= 0) {
            sorted_episodes = DownloadStore.sortedEpisodes(DownloadStore.getDownloadedEpisodes(context!!))
        }
    }
    class DownloadsFagmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var episodes = ArrayList<Episode>()
    }

    inner class DownloadsFragmentAdapter: RecyclerView.Adapter<DownloadsFagmentViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadsFagmentViewHolder {
            return DownloadsFagmentViewHolder(LayoutInflater.from(context).inflate(R.layout.downloads_item_view, parent, false))
        }

        override fun onBindViewHolder(holder: DownloadsFagmentViewHolder, position: Int) {

            val show_name = sorted_episodes.keys.elementAt(position)
            val episodes = sorted_episodes.get(show_name)

            holder.itemView.download_item_view_show_name.text = show_name

            val poster = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath  + "/" + show_name)

            if (poster.exists()) {
                glide.load(poster).into(holder.itemView.download_item_view_poster)
            }

            val list = arrayListOf<Map<String, Any>>()
            episodes?.forEach {

                it.value.forEach {

                    val map = mutableMapOf<String, Any>()
                    map.put("episode", it.season_name + " " + it.episode_name)

                    var uri: Uri?

                    try {
                        uri = Uri.parse(it.link)
                    }catch (e: Exception){
                        uri = null
                    }

                    if (uri?.isAbsolute == true) {
                        val mediaMetadataRetriever = MediaMetadataRetriever()
                        mediaMetadataRetriever.setDataSource(context, uri)
                        var duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        duration = (duration.toLong() / (1000 * 60)).toString()
                        map.put("length",  "$duration Minutes")
                        map.put("play", R.drawable.ic_play_arrow_black_24dp)
                    }else {
                        map.put("length", "00:00")
                        map.put("play", R.drawable.ic_error_black_24dp)
                    }

                    list.add(map)
                    holder.episodes.add(it)
                }

            }

            val click_listener = object :View.OnClickListener {
                override fun onClick(v: View?) {

                    val constraint = ConstraintSet()

                    constraint.clone(holder.itemView.download_item_view_container)

                    if (holder.itemView.download_item_view_episodes.visibility == View.GONE) {
                        holder.itemView.download_item_view_revealer.setImageResource(R.drawable.ic_arrow_up_black_24dp)

                        constraint.setVisibility(R.id.download_item_view_episodes, ConstraintSet.VISIBLE)
                    } else {
                        holder.itemView.download_item_view_revealer.setImageResource(R.drawable.ic_arrow_down_black_24dp)

                        constraint.setVisibility(R.id.download_item_view_episodes, ConstraintSet.GONE)
                    }

                    constraint.applyTo(holder.itemView.download_item_view_container)
                }
            }


//            holder.itemView.download_item_view_reveal_group.setOnClickListener(click_listener)
            holder.itemView.download_item_view_poster.setOnClickListener(click_listener)
            holder.itemView.download_item_view_revealer.setOnClickListener(click_listener)
            holder.itemView.download_item_view_show_name.setOnClickListener(click_listener)


            holder.itemView.download_item_view_episodes.setOnItemClickListener {
                parent, view, pos, id ->

                try {
                    val link = Uri.parse(holder.episodes[pos].link)
                    val intent = Intent(Intent.ACTION_VIEW, link)

                    startActivity(intent)

                }catch (e: Exception){
                    Toast.makeText(context!!, "Cannot find video", Toast.LENGTH_LONG).show()
                    return@setOnItemClickListener
                }
            }



            holder.itemView.download_item_view_episodes.adapter = SimpleAdapter(context, list,
                    R.layout.downloads_item_view_episodes_view, arrayOf("episode", "play", "length"),
                    intArrayOf(R.id.downloads_item_view_episode_view_name, R.id.downloads_item_view_episode_view_play, R.id.downloads_item_view_episode_view_length))

            val constraint = ConstraintSet()
            constraint.clone(holder.itemView.download_item_view_container)

            constraint.constrainHeight(R.id.download_item_view_episodes, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60.0f, context?.resources?.displayMetrics).toInt() * list.size)
            constraint.setVisibility(R.id.download_item_view_episodes, ConstraintSet.GONE)

            constraint.applyTo(holder.itemView.download_item_view_container)
        }

        override fun getItemCount(): Int {
            return sorted_episodes.size
        }

    }
}