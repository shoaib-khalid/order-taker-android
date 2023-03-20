package com.symplified.ordertaker.data

import android.net.Uri
import androidx.room.TypeConverter
import com.symplified.ordertaker.models.stores.BusinessType

class Converters {
    @TypeConverter
    fun fromString(value: String?): Uri = Uri.parse(value)

    @TypeConverter
    fun toString(uri: Uri): String = uri.toString()

    @TypeConverter
    fun fromBusinessType(value: BusinessType) = value.name

    @TypeConverter
    fun toBusinessType(value: String) = enumValueOf<BusinessType>(value)


}