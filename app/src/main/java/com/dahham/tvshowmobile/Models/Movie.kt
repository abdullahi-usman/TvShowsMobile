package com.dahham.tvshowmobile.Models

/**
 * Created by dahham on 4/5/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */
data class Movie (val name: String, val description: String, val casts: List<String>?,
                 val rating: Float?, val genre: List<String>?, val runtime: Int?, val poster: String?)