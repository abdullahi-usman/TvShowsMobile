package com.dahham.tvshowmobile.Models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by dahham on 3/29/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */

open class Episode(val show_name: String, val season_name: String? = null, val episode_name: String? = null, var link: String? = null): Parcelable, Comparable<Episode>{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(show_name)
        parcel.writeString(season_name)
        parcel.writeString(episode_name)
        parcel.writeString(link)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(other: Episode): Int {
        var status: Int
        status = other.show_name.compareTo(show_name, true)

        if (status != 0) return status

        if (season_name != null) {
            status = other.season_name?.compareTo(season_name, true)!!

            if (status != 0) return status
        }

        if (episode_name != null) {
            status = other.episode_name?.compareTo(episode_name, true)!!
        }

        return status
    }
    override fun hashCode(): Int {
        return (show_name + season_name + episode_name).hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Episode && other.show_name.matches(Regex(this.show_name)) == true
                && this.season_name != null && other.season_name?.matches(Regex(this.season_name)) == true
                && this.episode_name != null && other.episode_name?.matches(Regex(this.episode_name)) == true
    }

    companion object CREATOR : Parcelable.Creator<Episode> {
        override fun createFromParcel(parcel: Parcel): Episode {
            return Episode(parcel)
        }

        override fun newArray(size: Int): Array<Episode?> {
            return arrayOfNulls(size)
        }
    }
}

class Series(val show_name: String, val season: String, var episodes: List<Episode>? = null, val link: String?): Parcelable{

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(Episode),
            parcel.readString()) {

    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(show_name)
        dest?.writeString(season)
        dest?.writeTypedList(episodes)
        dest?.writeString(link)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Series> {
        override fun createFromParcel(parcel: Parcel): Series {
            return Series(parcel)
        }

        override fun newArray(size: Int): Array<Series?> {
            return arrayOfNulls(size)
        }
    }

}
class Show(val name: String, var description: String? = null, var casts: List<String>? = null,
           var rating: Float? = null, var genre: List<String>? = null, var runtime: String? = null,  var series: List<Series>? = null,  var poster: String? = null,
           var dateAdded: String? = null, var link: String? = null, var views: Int? = null): Parcelable, Comparable<Show>{

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArrayList(),
            parcel.readValue(Float::class.java.classLoader) as? Float,
            parcel.createStringArrayList(),
            parcel.readString(),
            parcel.createTypedArrayList(Series),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun compareTo(other: Show): Int {
        return this.name.compareTo(other.name)
    }
    override fun hashCode(): Int {
        return this.name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Show && other.name.matches(Regex(this.name))
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeString(description)
        dest?.writeStringList(casts)
        dest?.writeValue(rating)
        dest?.writeStringList(genre)
        dest?.writeString(runtime)
        dest?.writeTypedList(series)
        dest?.writeString(poster)
        dest?.writeString(dateAdded)
        dest?.writeString(link)
        dest?.writeValue(views)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Show> {
        override fun createFromParcel(parcel: Parcel): Show {
            return Show(parcel)
        }

        override fun newArray(size: Int): Array<Show?> {
            return arrayOfNulls(size)
        }
    }
}