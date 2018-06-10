package com.dahham.tvshowmobile.Models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by dahham on 4/5/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */

class LastestEpisode (show_name: String, season_name: String? = null, episode_name: String? = null, val rating: Float? = null, val runtime: Int? = null,  var poster: String? = null, val dateAdded: String? = null, var download_links: List<Link>? = null): Episode(show_name, season_name, episode_name, ""),
Parcelable{

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Float::class.java.classLoader) as? Float,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(Link)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(show_name)
        parcel.writeString(season_name)
        parcel.writeString(episode_name)
        parcel.writeValue(rating)
        parcel.writeValue(runtime)
        parcel.writeString(poster)
        parcel.writeString(dateAdded)
        parcel.writeTypedList(download_links)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LastestEpisode> {
        override fun createFromParcel(parcel: Parcel): LastestEpisode {
            return LastestEpisode(parcel)
        }

        override fun newArray(size: Int): Array<LastestEpisode?> {
            return arrayOfNulls(size)
        }
    }
}