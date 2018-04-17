package com.dahham.tvshowmobile.LifeCycleObservers

import android.arch.lifecycle.ViewModelProviders
import com.dahham.tvshowmobile.ViewModels.TvShows4MobileViewModel
import com.dahham.tvshowmobile.fragments.AbstractShowsFragment

/**
 * Created by dahham on 4/6/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvShows4MobileLifeCycle<T>(fragment: AbstractShowsFragment<T>) : AbstractShowLifeCycle<T>(fragment) {
    override val showViewModel = ViewModelProviders.of(fragment).get(TvShows4MobileViewModel::class.java)
}