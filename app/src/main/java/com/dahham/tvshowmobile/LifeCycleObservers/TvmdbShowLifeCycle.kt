package com.dahham.tvshowmobile.LifeCycleObservers

import android.arch.lifecycle.ViewModelProviders
import com.dahham.tvshowmobile.ViewModels.TvmdbShowViewModel
import com.dahham.tvshowmobile.fragments.AbstractShowsFragment

/**
 * Created by dahham on 3/29/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class TvmdbShowLifeCycle<T>(override var fragment: AbstractShowsFragment<T>) : AbstractShowLifeCycle<T>(fragment) {

    override val showViewModel = ViewModelProviders.of(fragment).get(TvmdbShowViewModel::class.java)

}