package com.dahham.tvshowmobile.ViewModels

import com.dahham.tvshowmobile.Models.*

/**
 * Created by dahham on 3/29/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvmdbShowViewModel : AbstractShowViewModel() {

    private val apiKey = "4806d249fa0741001ec6d3c098021c87" //R.string.moviesdb_api_key_v3_ouath
////    private var  tmdb : TmdbApi? = null
//    get() {
//        if (field == null) {
//            field = TmdbApi(apiKey)
//        }
//        return field
//    }

    override fun loadEpisodes(series: Series, listner: ShowsViewModelListener<Episode>?) {

    }

    override fun loadAllTVShows(listner: ShowsViewModelListener<Show>?) {
//        setState(TYPE.ALL_TV_SHOWS, STATE.STARTED)
//        listner?.onStarted()
//        val flowable = Flowable.create({ e: FlowableEmitter<Show> ->
//
//            val series = tmdb!!.tvSeries.getTopRated("en", 1)
//
//            setState(TYPE.ALL_TV_SHOWS, STATE.RUNNING)
//
//            for (serie in series.results){
//
//
//                val _serie = Show(name = serie.name, description = serie.overview, rating = serie?.userRating, genre = formatGenre(serie?.genres),
//                        runtime = serie?.episodeRuntime?.get(0), poster = serie?.posterPath, casts = formatCast(serie?.credits?.cast))
//
//                e.onNext(_serie)
//            }
//
//            setState(TYPE.ALL_TV_SHOWS, STATE.FINISHED)
//        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({
//            _shows.add(it)
//            shows.postValue(_shows)
//            listner?.onNext(it, "")
//        }, {listner?.onError(it)}, {listner?.onCompleted()})
//
//        disposables.put(TYPE.ALL_TV_SHOWS, flowable)
    }

    override fun loadAllMovies(listner: ShowsViewModelListener<Movie>?) {
//        setState(TYPE.ALL_MOVIES, STATE.STARTED)
//        listner?.onStarted()
//        val flowable = Flowable.create({ e: FlowableEmitter<Movie> ->
//            val movies = tmdb!!.movies.getTopRatedMovies("en", 1)
//
//            setState(TYPE.ALL_MOVIES, STATE.RUNNING)
//            for (movie in movies.results){
//                val _movie = Movie(name = movie.originalTitle, description = movie.overview,
//                        casts = formatCast(movie.cast), rating = movie.userRating, genre = formatGenre(movie.genres),
//                        runtime = movie.runtime, poster = movie.posterPath)
//
//                e.onNext(_movie)
//            }
//
//            setState(TYPE.ALL_MOVIES, STATE.FINISHED)
//        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({
//            _movies.add(it)
//            movies.postValue(_movies)
//            listner?.onNext(it, "")
//        }, {listner?.onError(it)}, {listner?.onCompleted()})
//
//        disposables.put(TYPE.ALL_MOVIES, flowable)

    }

    override fun loadLastestMovies(listner: ShowsViewModelListener<LastestMovie>?){
//        setState(TYPE.LASTEST_MOVIES, STATE.STARTED)
//        listner?.onStarted()
//        val flowable = Flowable.create({ e: FlowableEmitter<LastestMovie> ->
//
//            val lastest_movie = tmdb!!.movies.latestMovie
//
//            setState(TYPE.LASTEST_MOVIES, STATE.RUNNING)
//            val movie = LastestMovie(name = lastest_movie.originalTitle, description = lastest_movie.overview,
//                     casts =  formatCast(lastest_movie.cast), rating = lastest_movie.userRating, genre = formatGenre(lastest_movie.genres),
//                    runtime = lastest_movie.runtime, poster = lastest_movie.posterPath)
//
//            e.onNext(movie)
//            setState(TYPE.LASTEST_MOVIES, STATE.FINISHED)
//        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ( {
//            _lastestMovies.add(it)
//            lastestMovies.postValue(_lastestMovies)
//            listner?.onNext(it, "")
//        }, {listner?.onError(it)}, {listner?.onCompleted()})
//
//        disposables.put(TYPE.LASTEST_MOVIES, flowable)
    }

    override fun loadLastestEpisodes(listner: ShowsViewModelListener<LastestEpisode>?){
//        setState(TYPE.LASTEST_TV_EPISODES, STATE.STARTED)
//        listner?.onStarted()
//        val flowable = Flowable.create({ e: FlowableEmitter<LastestEpisode> ->
//
//            val lastest_series = tmdb!!.tvSeries.getAiringToday("en", 1, Timezone("GMT", "USA"))
//
//            setState(TYPE.LASTEST_TV_EPISODES, STATE.RUNNING)
//            for (series in lastest_series.results){
//                val lastestEpisode = LastestEpisode(name = series.name,  rating = series.userRating, season = series.numberOfSeasons,
//                        episode = series.numberOfEpisodes, runtime = series.episodeRuntime?.get(series.episodeRuntime?.size!! - 1), poster = series?.posterPath)
//
//                e.onNext(lastestEpisode)
//            }
//            setState(TYPE.LASTEST_TV_EPISODES, STATE.FINISHED)
//        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({
//            _lastestEpisodes.add(it)
//            lastestEpisodes.postValue(_lastestEpisodes)
//            listner?.onNext(it, "")
//        }, {listner?.onError(it)}, {listner?.onCompleted()})
//
//        disposables.put(TYPE.LASTEST_TV_EPISODES, flowable)
    }



//    private fun formatCast(list: List<PersonCast>?): ArrayList<String>?{
//        val casts = arrayListOf<String>()
//        list?.listIterator()?.forEach { casts.add(it.name) }
//
//        return casts
//    }
//
//    private fun formatGenre(list: List<Genre>?): ArrayList<String>?{
//        val genres = arrayListOf<String>()
//        list?.listIterator()?.forEach { genres.add(it.name) }
//
//        return genres
//    }
//
//    private fun getSuitablePoster(artworks: List<Artwork>): String{
//
//        var _artwork = Artwork()
//        for (artwork in artworks){
//            if (artwork.voteAverage > _artwork.voteAverage){
//                _artwork = artwork
//            }
//        }
//
//        return _artwork.filePath
//    }
}