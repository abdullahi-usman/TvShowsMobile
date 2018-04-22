package com.dahham.tvshowmobile.LifeCycleObservers

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModelProviders
import com.dahham.tvshowmobile.ViewModels.TvShows4MobileViewModel
import com.dahham.tvshowmobile.fragments.AbstractShowsFragment

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileLifeCycle<T>(val fragment: AbstractShowsFragment<T>) : LifecycleObserver {
    val showViewModel = ViewModelProviders.of(fragment).get(TvShows4MobileViewModel::class.java)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun start(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun stop(){
        showViewModel.stopAll()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun resume(){

    }
}