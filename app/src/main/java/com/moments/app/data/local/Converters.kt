package com.moments.app.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moments.app.data.model.Comment
import com.moments.app.data.model.LikeUser

/**
 * Room数据库类型转换器
 * 用于将复杂类型(List)转换为数据库可存储的类型(String)
 */
class Converters {
    
    private val gson = Gson()
    
    // 图片列表转换
    @TypeConverter
    fun fromImageList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }
    
    @TypeConverter
    fun toImageList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    // 点赞用户列表转换
    @TypeConverter
    fun fromLikeUserList(value: List<LikeUser>?): String {
        return gson.toJson(value ?: emptyList<LikeUser>())
    }
    
    @TypeConverter
    fun toLikeUserList(value: String): List<LikeUser> {
        val listType = object : TypeToken<List<LikeUser>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    // 评论列表转换
    @TypeConverter
    fun fromCommentList(value: List<Comment>?): String {
        return gson.toJson(value ?: emptyList<Comment>())
    }
    
    @TypeConverter
    fun toCommentList(value: String): List<Comment> {
        val listType = object : TypeToken<List<Comment>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}
