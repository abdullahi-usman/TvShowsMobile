package com.dahham.tvshowmobile.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dahham.tvshowmobile.Models.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileViewModel : ViewModel() {

    private val tvShows4Mobile = TvShows4Mobile()

    val shows = MutableLiveData<List<Show>>()
    //    protected var movies = MutableLiveData<List<Movie>>()
//    protected var lastestMovies = MutableLiveData<List<LastestMovie>>()
    val lastestEpisodes = MutableLiveData<List<LastestEpisode>>()
    val episodes = MutableLiveData<List<Episode>>()


    protected var _shows = ArrayList<Show>()
    //    protected var _movies = ArrayList<Movie>()
//    protected var _lastestMovies = ArrayList<LastestMovie>()
    protected var _lastestEpisodes = ArrayList<LastestEpisode>()
    protected var _episodes = ArrayList<Episode>()


    protected var disposables = ArrayList<Disposable>()

    protected var lastestMoviesState = STATE.STOPPED
    protected var allTVShowsState = STATE.STOPPED
    protected var lastestTVEpisodeState = STATE.STOPPED
    protected var allMoviesState = STATE.STOPPED
    protected var episodeState = STATE.STOPPED

    interface ShowsViewModelListener<T> {
        fun onStarted() {}
        fun onNext(item: T, msg: String) {}
        fun onError(throwable: Throwable) {}
        fun onCompleted() {}
    }

    enum class STATE {
        STOPPED, STARTED, RUNNING, FINISHED
    }

    enum class TYPE {
        LASTEST_MOVIES, ALL_MOVIES, ALL_TV_SHOWS, LASTEST_TV_EPISODES, EPISODE
    }

    fun stopAll() {

        for (disposable in disposables) {
            if (disposable.isDisposed == false) {
                disposable.dispose()
            }
        }

    }

    fun getState(type: TYPE): STATE {
        return when (type) {
            TYPE.LASTEST_MOVIES -> lastestMoviesState
            TYPE.ALL_MOVIES -> allTVShowsState
            TYPE.LASTEST_TV_EPISODES -> lastestTVEpisodeState
            TYPE.ALL_TV_SHOWS -> allMoviesState
            TYPE.EPISODE -> episodeState
        }
    }

    protected fun setState(type: TYPE, state: STATE) {
        when (type) {
            TYPE.LASTEST_MOVIES -> lastestMoviesState = state
            TYPE.ALL_MOVIES -> allTVShowsState = state
            TYPE.LASTEST_TV_EPISODES -> lastestTVEpisodeState = state
            TYPE.ALL_TV_SHOWS -> allMoviesState = state
            TYPE.EPISODE -> episodeState = state
        }

    }

    fun getShowsProperties(vararg shows: Show, listener: ShowsViewModelListener<Show>?, photoDownloadLocation: String?) {
        listener?.onStarted()

        val flowable = Flowable.create({ e: FlowableEmitter<Show> ->

            for (show in shows) {
                try {
                    tvShows4Mobile.getShowProperties(show)
                } catch (except: Exception) {

                    if (!e.tryOnError(except)){
                        break
                    }

                    continue
                }

                if (photoDownloadLocation != null && show.poster != null && show.poster?.isEmpty() == false){
                    var connection : HttpURLConnection? = null
                    var stream : BufferedInputStream? = null
                    var out_put: FileOutputStream? = null
                    try {
                        connection = URL(show.poster).openConnection() as HttpURLConnection

                        stream = BufferedInputStream(connection.inputStream)

                        val download_name = photoDownloadLocation + "/" + show.name
                        out_put = FileOutputStream(download_name)

                        var i = 0
                        while (true){
                            i = stream.read()

                            if (i == -1){
                                break
                            }

                            out_put.write(i)
                        }

                        show.poster = download_name
                    }catch (e: Exception){

                    }finally {
                        out_put?.flush()
                        out_put?.close()
                        stream?.close()

                    }

                }

                e.onNext(show)
            }

            e.onComplete()
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            listener?.onNext(it, "")
        }, {
            listener?.onError(it)
            it.printStackTrace()
        }, {
            listener?.onCompleted()
        })

        disposables.add(flowable)
    }

    fun loadAllTVShows(listner: ShowsViewModelListener<Show>?) {

        if (getState(TYPE.ALL_TV_SHOWS) > STATE.STOPPED) {
            return
        }

        setState(TYPE.ALL_TV_SHOWS, STATE.STARTED)
        listner?.onStarted()

        val flowable = Flowable.create({ e: FlowableEmitter<Show> ->

            setState(TYPE.ALL_TV_SHOWS, STATE.RUNNING)

            try {

                tvShows4Mobile.getAllShows().forEach { show ->
                    e.onNext(show)
                }

            } catch (except: Exception) {
                e.onError(except)
            }

            setState(TYPE.ALL_TV_SHOWS, STATE.FINISHED)
            e.onComplete()
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            listner?.onNext(it, "")
            _shows.add(it)
            shows.value = _shows
        }, {
            setState(TYPE.ALL_TV_SHOWS, STATE.STOPPED)
            listner?.onError(it)
            it.printStackTrace()
        }, {
            listner?.onCompleted()
        })

        disposables.add(flowable)
    }


    fun loadLastestEpisodes(listner: ShowsViewModelListener<LastestEpisode>?) {

        if (getState(TYPE.LASTEST_TV_EPISODES) > STATE.STOPPED) {
            return
        }

        setState(TYPE.LASTEST_TV_EPISODES, STATE.STARTED)
        listner?.onStarted()

        val flowable = Flowable.create<LastestEpisode>({ e: FlowableEmitter<LastestEpisode> ->

            setState(TYPE.LASTEST_TV_EPISODES, STATE.RUNNING)

            try {

                tvShows4Mobile.getLastestEpisodes().forEach { latest_episode ->
                    e.onNext(latest_episode)
                }

            } catch (except: Exception) {
                e.onError(except)
            }

            setState(TYPE.LASTEST_TV_EPISODES, STATE.FINISHED)
            e.onComplete()
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            listner?.onNext(it, "")
            _lastestEpisodes.add(it)
            lastestEpisodes.value = _lastestEpisodes
        }, {
            setState(TYPE.LASTEST_TV_EPISODES, STATE.STOPPED)
            listner?.onError(it)
            it.printStackTrace()
        }, {
            listner?.onCompleted()
        })

        disposables.add(flowable)
    }


    fun loadEpisodes(series: Series, listner: ShowsViewModelListener<Episode>?) {
        if (getState(TYPE.EPISODE) > STATE.STOPPED) {
            return
        }

        setState(TYPE.EPISODE, STATE.STARTED)

        listner?.onStarted()
        val flowable = Flowable.create<Episode>({ e: FlowableEmitter<Episode> ->

            setState(TYPE.EPISODE, STATE.RUNNING)
            try {

                tvShows4Mobile.getEpisodes(series).forEach { episode ->
                    e.onNext(episode)
                }

            } catch (except: Exception) {
                e.onError(except)
            }

            setState(TYPE.EPISODE, STATE.FINISHED)
            e.onComplete()
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            listner?.onNext(it, "")
            _episodes.add(it)
            episodes.value = _episodes
        }, {
            setState(TYPE.EPISODE, STATE.STOPPED)
            listner?.onError(it)
            it.printStackTrace()
        }, {
            listner?.onCompleted()
        })

        disposables.add(flowable)
    }

    fun getLastestEpisodesLinks(vararg latest_episodes: LastestEpisode) {
        //tvShows4Mobile.getLastestEpisodesLinks(*latest_episodes)

        //TODO before we could be able to devise how tvshows4mobile.com query its download link from raw link let just get it direct
        for (latest_episode in latest_episodes){
            val links = ArrayList<Link>()

            links.add(getDownloadLinkDirect(latest_episode.name, latest_episode.season!!, latest_episode.episode!!, Link.GP3))
            links.add(getDownloadLinkDirect(latest_episode.name, latest_episode.season, latest_episode.episode, Link.MP4))
            links.add(getDownloadLinkDirect(latest_episode.name, latest_episode.season, latest_episode.episode, Link.HD))

            latest_episode.link = links
            return
        }

    }

    fun getEpisodeLink(name: String, season: String, episode: Episode): List<Link> {
        //return tvShows4Mobile.getEpisodeLink(episode)
        //TODO before we could be able to devise how tvshows4mobile.com query its download link from raw link let just get it direct

        val links = ArrayList<Link>()

        links.add(getDownloadLinkDirect(name, season, episode.name , Link.GP3))
        links.add(getDownloadLinkDirect(name, season, episode.name, Link.MP4))
        links.add(getDownloadLinkDirect(name, season, episode.name, Link.HD))


        return links
    }

    fun getDownloadLinkDirect(name: String, season: String, episode: String , linkType: Int): Link{
        return Link(linkType, tvShows4Mobile.getDownloadLinkDirect(name, season, episode, if(linkType == Link.GP3) "3gp" else "mp4", if(linkType == Link.HD) true else false))
    }
}