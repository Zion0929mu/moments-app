package com.moments.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * 用户数据模型
 * 存储用户基本信息
 */
@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uid: String,              // Google用户ID
    val email: String,            // 邮箱
    val displayName: String,      // 显示名称
    val photoUrl: String?,        // 头像URL
    val createdAt: Long = System.currentTimeMillis()  // 创建时间
) : Parcelable
