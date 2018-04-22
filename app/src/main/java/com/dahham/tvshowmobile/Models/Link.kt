package com.dahham.tvshowmobile.Models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by dahham on 4/17/18.
 * This file is part of TVShowMobile licensed under GNU Public License
 *
 */

class Link(var type: Int, var link: String): Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeString(link)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Link> {
        val HD = 3
        val MP4 = 2
        val GP3 = 1

        override fun createFromParcel(parcel: Parcel): Link {
            return Link(parcel)
        }

        override fun newArray(size: Int): Array<Link?> {
            return arrayOfNulls(size)
        }
    }
}