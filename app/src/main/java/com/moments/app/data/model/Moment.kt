package com.moments.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.moments.app.data.local.Converters
import kotlinx.parcelize.Parcelize

/**
 * 朋友圈动态数据模型
 * 包含动态的所有信息:文字、图片、点赞、评论等
 */
@Parcelize
@Entity(tableName = "moments")
@TypeConverters(Converters::class)
data class Moment(
    @PrimaryKey
    val id: String,                           // 动态ID
    val userId: String,                       // 发布者ID
    val userName: String,                     // 发布者姓名
    val userAvatar: String?,                  // 发布者头像
    val content: String,                      // 文字内容
    val images: List<String>,                 // 图片路径列表(最多9张)
    val location: String? = null,             // 位置信息(可选)
    val timestamp: Long = System.currentTimeMillis(),  // 发布时间戳
    val likeCount: Int = 0,                   // 点赞数
    val likeUsers: List<LikeUser> = emptyList(),      // 点赞用户列表
    val comments: List<Comment> = emptyList()         // 评论列表
) : Parcelable

/**
 * 点赞用户信息
 */
@Parcelize
data class LikeUser(
    val uid: String,
    val name: String
) : Parcelable

/**
 * 评论信息
 */
@Parcelize
data class Comment(
    val id: String,                           // 评论ID
    val userId: String,                       // 评论者ID
    val userName: String,                     // 评论者姓名
    val content: String,                      // 评论内容
    val replyToUserId: String? = null,        // 回复的用户ID(可选)
    val replyToUserName: String? = null,      // 回复的用户名(可选)
    val timestamp: Long = System.currentTimeMillis()  // 评论时间
) : Parcelable
