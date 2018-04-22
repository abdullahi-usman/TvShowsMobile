package com.dahham.tvshowmobile.Models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by dahham on 4/5/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */

class LastestEpisode (val name: String, val season: String? = null, val episode: String? = null, val rating: Float? = null, val runtime: Int? = null,  var poster: String? = null, val dateAdded: String? = null, var link: List<Link>? = null):
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

    override fun hashCode(): Int {
        return name.hashCode() + (season?.hashCode() ?:0)  + (episode?.hashCode() ?:0)
    }

    override fun equals(other: Any?): Boolean {
        return other is LastestEpisode && other.name == this.name
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(season)
        parcel.writeString(episode)
        parcel.writeValue(rating)
        parcel.writeValue(runtime)
        parcel.writeString(poster)
        parcel.writeString(dateAdded)
        parcel.writeTypedList(link)
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