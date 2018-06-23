package com.dahham.tvshowmobile.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.JsonReader
import android.util.JsonWriter
import com.dahham.tvshowmobile.Models.Episode
import com.dahham.tvshowmobile.Models.Link
import com.dahham.tvshowmobile.R
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by dahham on 5/3/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class DownloadStore private constructor() {

    private lateinit var downloadManager: DownloadManager
    private lateinit var preferenceManager: SharedPreferences
    private lateinit var download_ids: MutableSet<String>
    private lateinit var DOWNLOAD_PREF_TAG: String

    private val downloading_shows = Hashtable<Episode, Long>()

    private val downloadStoreListeners = Hashtable<Episode, DownloadStoreListener>()

    interface DownloadStoreListener {
        fun onDownloadStateChanged(episode: Episode, status: Int);
    }

    companion object {

        val download_location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)

        @JvmStatic
        private var _instance: DownloadStore? = null

        fun instnace(context: Context): DownloadStore {

            if (_instance == null) {
                _instance = DownloadStore()
                _instance!!.DOWNLOAD_PREF_TAG = context.getString(R.string.download_ids)
                _instance!!.downloadManager = context.applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                _instance!!.preferenceManager = context.getSharedPreferences(_instance!!.DOWNLOAD_PREF_TAG, Context.MODE_PRIVATE)

                _instance!!.download_ids = _instance!!.preferenceManager.getStringSet(_instance!!.DOWNLOAD_PREF_TAG, mutableSetOf<String>())

                _instance!!.query_downloading_properties()
            }

            return _instance!!
        }

        fun writeOutDownloadedFile(context: Context, vararg episodes: Episode) {

            val downloaded_episodes = getDownloadedEpisodes(context)

            downloaded_episodes.removeAll(episodes)
            downloaded_episodes.addAll(episodes)

            val sorted_episodes = sortedEpisodes(downloaded_episodes)

            context.deleteFile("downloaded_movies.json")
            //File(context.filesDir.absolutePath + "downloaded_movies.json").delete()

            val downloaded_list = context.openFileOutput("downloaded_movies.json", Context.MODE_PRIVATE)

            val json_writer = JsonWriter(OutputStreamWriter(downloaded_list))

            json_writer.beginObject()

            for (episode in sorted_episodes) {
                json_writer.name(episode.key)
                json_writer.beginObject()
                for (seasons in episode.value) {
                    json_writer.name(seasons.key)
                    json_writer.beginObject()
                    json_writer.name("episodes")

                    json_writer.beginArray()
                    for (_episode in seasons.value) {
                        json_writer.beginObject()
                        json_writer.name("name").value(_episode.episode_name)
                        json_writer.name("location").value(_episode.link)
                        json_writer.endObject()
                    }
                    json_writer.endArray()

                    json_writer.endObject()
                }
                json_writer.endObject()
            }
            json_writer.endObject()

            json_writer.flush()
            json_writer.close()

            downloaded_list.flush()
            downloaded_list.close()
        }


        fun sortedEpisodes(episodes: ArrayList<Episode>): Hashtable<String, Hashtable<String, ArrayList<Episode>>> {
            episodes.sort()

            val sorted_episodes = Hashtable<String, Hashtable<String, ArrayList<Episode>>>()

            for (_episode in episodes) {

                if (!sorted_episodes.containsKey(_episode.show_name)){
                    sorted_episodes.put(_episode.show_name, Hashtable())
                }

                if (!sorted_episodes.get(_episode.show_name)!!.containsKey(_episode.season_name)){
                    sorted_episodes.get(_episode.show_name)!!.put(_episode.season_name, ArrayList())
                }

                sorted_episodes.get(_episode.show_name)!!.get(_episode.season_name)?.add(_episode)

            }

            return sorted_episodes
        }

        fun getDownloadedEpisodes(context: Context): ArrayList<Episode> {
            val episodes = arrayListOf<Episode>()
            val sorted_episodes = Hashtable<String, ArrayList<Episode>>()

            try {

                val downloaded_list = context.openFileInput("downloaded_movies.json")

                val json_reader = JsonReader(InputStreamReader(downloaded_list))
                json_reader.beginObject()
                while (json_reader.hasNext()) {
                    val show_name = json_reader.nextName()
                    val show_episodes = arrayListOf<Episode>()

                    json_reader.beginObject()
                    while (json_reader.hasNext()) {
                        val season_name = json_reader.nextName()
                        json_reader.beginObject()
                        while (json_reader.hasNext()) {
                            if (json_reader.nextName().matches(Regex("episodes"))) {
                                json_reader.beginArray()
                                while (json_reader.hasNext()) {
                                    json_reader.beginObject()
                                    json_reader.nextName()

                                    val episode = Episode(show_name, season_name, json_reader.nextString())
                                    json_reader.nextName()

                                    episode.link = json_reader.nextString()
                                    show_episodes.add(episode)
                                    episodes.add(episode)
                                    json_reader.endObject()
                                }
                                json_reader.endArray()
                            }
                        }
                        json_reader.endObject()
                    }
                    json_reader.endObject()
                    sorted_episodes.put(show_name, show_episodes)
                }
                json_reader.endObject()

                json_reader.close()
                downloaded_list?.close()
            } catch (e: IOException) {
                //Crashlytics.log(e.message)
                e.printStackTrace()
            }

            return episodes
        }

        fun clearDownloadedEpisodes(context: Context): Boolean {
            return context.deleteFile("downloaded_movies.json")
        }
    }

    fun registerOnDownloadChanged(episode: Episode, downloadStoreListener: DownloadStoreListener) {

        if (!downloadStoreListeners.contains(episode)) {
            downloadStoreListeners.put(episode, downloadStoreListener)
        } else if (downloadStoreListeners.get(episode) != downloadStoreListener) {
            downloadStoreListeners.remove(episode)
            downloadStoreListeners.put(episode, downloadStoreListener)
        }

    }

    fun hasRegisteredListener(episode: Episode): Boolean {
        return downloadStoreListeners.get(episode) != null
    }

    fun removeOnDownloadChanged(episode: Episode) {
        downloadStoreListeners.remove(episode)
    }

    private fun query_downloading_properties() {

        download_ids = preferenceManager.getStringSet(DOWNLOAD_PREF_TAG, mutableSetOf<String>())

        downloading_shows.clear()

        for (id in download_ids) {

            val _episode = getEpisodeForId(id.toLong())

            if (_episode == null)continue

            downloading_shows.put(_episode, id.toLong())
        }

    }

    fun getDownloadIdForEpisode(episode: Episode): Long {
        return downloading_shows.get(episode) ?: -1L
    }

    @SuppressLint("ApplySharedPref")
    fun enqueue(episode: Episode, link: Link) {

        val request = DownloadManager.Request(Uri.parse(link.link))
        request.allowScanningByMediaScanner()
        request.setTitle(episode.show_name)
        request.setDescription("${episode.season_name} - ${episode.episode_name}")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDestinationUri(Uri.parse("file:///" + download_location.absolutePath + "/" + "${episode.show_name} - ${episode.season_name} - ${episode.episode_name}.${if (link.type == Link.GP3) "3gp" else "mp4"}"))
        request.setVisibleInDownloadsUi(false)
        val id = downloadManager.enqueue(request)

        download_ids.add(id.toString())
        preferenceManager.edit().remove(DOWNLOAD_PREF_TAG).putStringSet(DOWNLOAD_PREF_TAG, download_ids).commit()

        query_downloading_properties()

        postDownloadStatus(episode, id)
    }

    fun isDownloadInStore(id: Long): Boolean {

        val ids = preferenceManager.getStringSet(DOWNLOAD_PREF_TAG, mutableSetOf<String>())

        return ids.contains(id.toString())
    }

    fun removeFromStore(id: Long): Boolean {
        var status = download_ids.remove(id.toString())

        if (status) {

            status = preferenceManager.edit().remove(DOWNLOAD_PREF_TAG).putStringSet(DOWNLOAD_PREF_TAG, download_ids).commit()

            query_downloading_properties()

            val episode = getEpisodeForId(id) ?: return status

            postDownloadStatus(episode, id)
        }

        return status
    }

    fun postDownloadStatus(episode: Episode, id: Long) {
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))

        val listener = downloadStoreListeners.get(episode)

        cursor.moveToFirst()

        if (cursor.count <= 0) {
            listener?.onDownloadStateChanged(episode, -1)
        } else {
            listener?.onDownloadStateChanged(episode, cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)))
        }

        cursor.close()
    }

    fun getEpisodeForId(id: Long): Episode? {
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))

        if (cursor.count <= 0) return null

        cursor.moveToFirst()
        val downloding_show_name = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
        val season_episode = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)).split(Regex("-"), 2)

        val season_name = season_episode[0].trim()
        val episode_name = season_episode[1].trim()

        cursor.close()

        return Episode(downloding_show_name, season_name, episode_name, null)
    }

    fun getDownloadedFile(episode: Episode): String? {

        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(getDownloadIdForEpisode(episode)))

        if (cursor.count <= 0) return null

        cursor.moveToFirst()

        return cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
    }

}