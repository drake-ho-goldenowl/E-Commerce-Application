package com.goldenowl.ecommerceapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*


class Converters  {
    @TypeConverter
    fun dateToTimestamp(value: Long?): Date?{
        return if(value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long?{
        return date?.time
    }

    @TypeConverter
    fun imagesToJson(value: List<String>?): String?{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToImages(value: String?): List<String>{
        return Gson().fromJson(value,Array<String>::class.java).toList()
    }

    @TypeConverter
    fun colorToJson(value: List<Color>): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToColor(value: String?): List<Color>{
        return Gson().fromJson(value,Array<Color>::class.java).toList()
    }

    @TypeConverter
    fun tagsToJson(value: List<Tag>?): String?{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToTags(value: String?): List<Tag>{
        return Gson().fromJson(value,Array<Tag>::class.java).toList()
    }
}