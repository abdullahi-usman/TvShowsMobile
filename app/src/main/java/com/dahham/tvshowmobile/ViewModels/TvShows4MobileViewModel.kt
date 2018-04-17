package com.dahham.tvshowmobile.ViewModels

import com.dahham.tvshowmobile.Models.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileViewModel : AbstractShowViewModel() {

    val tvShows4Mobile = TvShows4Mobile()

    override fun loadAllMovies(listner: ShowsViewModelListener<Movie>?) {
        throw UnsupportedOperationException("TvShows4Mobile does not have movies")
    }

    override fun loadLastestMovies(listner: ShowsViewModelListener<LastestMovie>?) {
        throw UnsupportedOperationException("TvShows4Mobile does not have movies")
    }

    override fun loadAllTVShows(listner: ShowsViewModelListener<Show>?) {

        setState(TYPE.ALL_TV_SHOWS, STATE.STARTED)
        listner?.onStarted()
        val flowable = Flowable.create({ e: FlowableEmitter<Show> ->

            for (show in tvShows4Mobile.getAllShows()) {
                e.onNext(show)
            }

            setState(TYPE.ALL_TV_SHOWS, STATE.FINISHED)
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            _shows.add(it)
            shows.postValue(_shows)
            listner?.onNext(it, "")

        }, { listner?.onError(it) }, { listner?.onCompleted() })

        disposables.put(TYPE.ALL_TV_SHOWS, flowable)
    }


    override fun loadLastestEpisodes(listner: ShowsViewModelListener<LastestEpisode>?) {
        setState(TYPE.LASTEST_TV_EPISODES, STATE.STARTED)
        listner?.onStarted()
        val flowable = Flowable.create<LastestEpisode>({ e: FlowableEmitter<LastestEpisode> ->

            for (latest_episode in tvShows4Mobile.getLastestEpisodes()) {
                e.onNext(latest_episode)
            }

            setState(TYPE.LASTEST_TV_EPISODES, STATE.FINISHED)
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            _lastestEpisodes.add(it)
            lastestEpisodes.postValue(_lastestEpisodes)
            listner?.onNext(it, "")
        }, {
            listner?.onError(it)
            it.printStackTrace()
        }, {
            listner?.onCompleted()
        })

        disposables.put(TYPE.LASTEST_TV_EPISODES, flowable)
    }


    override fun loadEpisodes(series: Series, listner: ShowsViewModelListener<Episode>?) {
        setState(TYPE.EPISODE, STATE.STARTED)
        listner?.onStarted()
        val flowable = Flowable.create<Episode>({ e: FlowableEmitter<Episode> ->

            for (episode in tvShows4Mobile.getEpisodes(series)) {
                e.onNext(episode)
            }

            setState(TYPE.EPISODE, STATE.FINISHED)
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            _episodes.add(it)
            episodes.postValue(_episodes)
            listner?.onNext(it, "")
        }, {
            listner?.onError(it)
            it.printStackTrace()
        }, { listner?.onCompleted() })

        disposables.put(TYPE.EPISODE, flowable)
    }
}