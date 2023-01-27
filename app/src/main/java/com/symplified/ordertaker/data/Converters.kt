package com.symplified.ordertaker.data

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromString(value: String?): Uri {
        return Uri.parse(value)
    }

    @TypeConverter
    fun toString(uri: Uri): String {
        return uri.toString()
    }
}