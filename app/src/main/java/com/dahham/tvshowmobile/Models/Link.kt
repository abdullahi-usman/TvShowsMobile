package com.dahham.tvshowmobile.Models

/**
 * Created by dahham on 4/17/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */

class Link(var type: Type, var link: String){
    enum class Type{HD, MP4, GP3}
}