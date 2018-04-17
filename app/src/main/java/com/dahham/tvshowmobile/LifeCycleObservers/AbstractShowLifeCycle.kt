package com.dahham.tvshowmobile.LifeCycleObservers

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.OnLifecycleEvent
import com.dahham.tvshowmobile.Models.*
import com.dahham.tvshowmobile.ViewModels.AbstractShowViewModel
import com.dahham.tvshowmobile.fragments.AbstractShowsFragment

/**
 * Created by dahham on 4/3/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
abstract class AbstractShowLifeCycle<T>  constructor(open var fragment: AbstractShowsFragment<T>) : LifecycleObserver {


    abstract val showViewModel: AbstractShowViewModel

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun start(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun stop(){
        stopAll()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun resume(){

    }

    open fun getAllTVShows(allTvShowsListener: AbstractShowViewModel.ShowsViewModelListener<Show>?): LiveData<List<Show>>{
        if (showViewModel.getState(AbstractShowViewModel.TYPE.ALL_TV_SHOWS) <= AbstractShowViewModel.STATE.STOPPED){
            showViewModel.loadAllTVShows(allTvShowsListener)
        }

        return showViewModel.getAllShows()
    }



    open fun getAllMovies(allMoviesListener: AbstractShowViewModel.ShowsViewModelListener<Movie>?):  LiveData<List<Movie>>{
        if (showViewModel.getState(AbstractShowViewModel.TYPE.ALL_MOVIES) <= AbstractShowViewModel.STATE.STOPPED){
            showViewModel.loadAllMovies(allMoviesListener)
        }
        return showViewModel.getAllMovies()
    }

    open fun getLastestMovies(lastestMoviesListener: AbstractShowViewModel.ShowsViewModelListener<LastestMovie>?): LiveData<List<LastestMovie>>{
        if (showViewModel.getState(AbstractShowViewModel.TYPE.LASTEST_MOVIES) <= AbstractShowViewModel.STATE.STOPPED){
            showViewModel.loadLastestMovies(lastestMoviesListener)
        }

        return showViewModel.getLastestMovies()
    }

    open fun getLastestEpisodes(lastestEpisodeListener: AbstractShowViewModel.ShowsViewModelListener<LastestEpisode>?):  LiveData<List<LastestEpisode>>{
        if (showViewModel.getState(AbstractShowViewModel.TYPE.LASTEST_TV_EPISODES) <= AbstractShowViewModel.STATE.STOPPED){
            showViewModel.loadLastestEpisodes(lastestEpisodeListener)
        }

        return showViewModel.getLastestEpisodes()
    }

    open fun getEpisodes(series: Series, lastestEpisodeListener: AbstractShowViewModel.ShowsViewModelListener<Episode>?): LiveData<List<Episode>> {
        if (showViewModel.getState(AbstractShowViewModel.TYPE.EPISODE) <= AbstractShowViewModel.STATE.STOPPED){
            showViewModel.loadEpisodes(series, lastestEpisodeListener)
        }

        return showViewModel.getEpisodes()
    }

    open fun stopAll(){
        showViewModel.stopAll()
    }
}