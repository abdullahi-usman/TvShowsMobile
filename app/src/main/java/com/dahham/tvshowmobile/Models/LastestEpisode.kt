package com.dahham.tvshowmobile.Models

/**
 * Created by dahham on 4/5/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */

class LastestEpisode (val name: String, val season: String? = null, val episode: String? = null, val rating: Float? = null, val runtime: Int? = null,  val poster: String? = null, val dateAdded: String? = null, var link: List<Link>? = null){

    override fun hashCode(): Int {
        return name.hashCode() + (season?.hashCode() ?:0)  + (episode?.hashCode() ?:0)
    }

    override fun equals(other: Any?): Boolean {
        return other is LastestEpisode && other.name == this.name
    }
}