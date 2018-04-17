package com.dahham.tvshowmobile.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dahham.tvshowmobile.Models.*
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
abstract class AbstractShowViewModel: ViewModel()  {

    protected var shows = MutableLiveData<List<Show>>()
    protected var movies = MutableLiveData<List<Movie>>()
    protected var lastestMovies = MutableLiveData<List<LastestMovie>>()
    protected var lastestEpisodes = MutableLiveData<List<LastestEpisode>>()
    protected var episodes = MutableLiveData<List<Episode>>()


    protected var _shows = ArrayList<Show>()
    protected var _movies = ArrayList<Movie>()
    protected var _lastestMovies = ArrayList<LastestMovie>()
    protected var _lastestEpisodes = ArrayList<LastestEpisode>()
    protected var _episodes = ArrayList<Episode>()


    protected var disposables: Hashtable<TYPE, Disposable?> = Hashtable()

    protected var lastestMoviesState = STATE.STOPPED
    protected var allTVShowsState = STATE.STOPPED
    protected var lastestTVEpisodeState = STATE.STOPPED
    protected var allMoviesState = STATE.STOPPED
    protected var episodeState = STATE.STOPPED

    interface ShowsViewModelListener<T>{
        fun onStarted()
        fun onNext(item: T, msg: String)
        fun onError(throwable: Throwable)
        fun onCompleted()
    }

    enum class STATE {
        STOPPED, STARTED, RUNNING, FINISHED
    }

    enum class TYPE {
        LASTEST_MOVIES, ALL_MOVIES, ALL_TV_SHOWS, LASTEST_TV_EPISODES, EPISODE
    }

    fun getAllShows(): LiveData<List<Show>> {
        return shows
    }

    fun getAllMovies(): LiveData<List<Movie>> {
        return movies
    }

    fun getLastestEpisodes(): LiveData<List<LastestEpisode>> {
        return lastestEpisodes
    }

    fun getLastestMovies(): LiveData<List<LastestMovie>> {
        return lastestMovies
    }

    fun getEpisodes():  LiveData<List<Episode>>{
        return episodes
    }

    fun stopAll() {
        for (type in TYPE.values()){
            stop(type)
        }
    }

    abstract fun loadAllTVShows(listner: ShowsViewModelListener<Show>?)
    abstract fun loadAllMovies(listner: ShowsViewModelListener<Movie>?)
    abstract fun loadLastestMovies(listner: ShowsViewModelListener<LastestMovie>?)
    abstract fun loadLastestEpisodes(listner: ShowsViewModelListener<LastestEpisode>?)
    abstract fun loadEpisodes(series: Series, listner: ShowsViewModelListener<Episode>?)

    fun getState(type: TYPE): STATE{
        return when(type){
            TYPE.LASTEST_MOVIES -> lastestMoviesState
            TYPE.ALL_MOVIES -> allTVShowsState
            TYPE.LASTEST_TV_EPISODES -> lastestTVEpisodeState
            TYPE.ALL_TV_SHOWS -> allMoviesState
            TYPE.EPISODE -> episodeState
        }
    }

    protected fun setState(type: TYPE, state: STATE){
        when (type){
            TYPE.LASTEST_MOVIES -> lastestMoviesState = state
            TYPE.ALL_MOVIES -> allTVShowsState = state
            TYPE.LASTEST_TV_EPISODES -> lastestTVEpisodeState = state
            TYPE.ALL_TV_SHOWS -> allMoviesState = state
            TYPE.EPISODE -> episodeState = state
        }
    }

    fun stop(type: TYPE) {
        val disposable = disposables[type]
        if (disposable?.isDisposed == false) {
            disposable.dispose()
        }
    }
}