package com.dahham.tvshowmobile.adapters

import android.arch.lifecycle.LiveData
import android.support.v7.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.dahham.tvshowmobile.LifeCycleObservers.AbstractShowLifeCycle

/**
 * Created by dahham on 4/5/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
abstract class TvAdapter<T>(val lifecycle: AbstractShowLifeCycle<T>, val data: LiveData<List<T>>) : RecyclerView.Adapter<ViewHolder>() {

    val glide: RequestManager

    init {
        glide = Glide.with(lifecycle.fragment.context!!)
    }

    override fun getItemCount(): Int {
        return data.value?.size ?: 0
    }

    fun hasViewHolders(): Boolean {
        return data.value?.isEmpty() ?: false
    }

    abstract fun load()
}