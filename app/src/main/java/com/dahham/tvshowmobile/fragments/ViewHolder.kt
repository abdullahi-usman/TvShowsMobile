package com.dahham.tvshowmobile.fragments

import android.support.v7.widget.RecyclerView
import android.view.View
import com.dahham.tvshowmobile.Models.Episode

/**
 * Created by dahham on 4/3/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
class ViewHolder (var _itemView: View) : RecyclerView.ViewHolder(_itemView){
    var episode: Episode? = null
}