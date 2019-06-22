package com.dahham.downloadmanager

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by dahham on 6/20/18.
 * This file is part of DownloadManager licensed under GNU Public License
 *
 */
class UUIDConverter {

    @TypeConverter
    fun toString(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String): UUID {
        return UUID.fromString(uuid)
    }

}