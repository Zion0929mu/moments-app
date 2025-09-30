package com.moments.app.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期时间工具类
 * 用于格式化时间戳
 */
object DateUtils {
    
    /**
     * 将时间戳转换为相对时间描述
     * 例如: "刚刚"、"5分钟前"、"昨天"、"2天前"、"2025-01-01"
     */
    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "刚刚"  // 1分钟内
            diff < 3600_000 -> "${diff / 60_000}分钟前"  // 1小时内
            diff < 86400_000 -> "${diff / 3600_000}小时前"  // 24小时内
            diff < 172800_000 -> "昨天"  // 48小时内
            diff < 604800_000 -> "${diff / 86400_000}天前"  // 7天内
            else -> {
                // 超过7天显示具体日期
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }
    
    /**
     * 格式化时间戳为标准日期时间
     */
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * 格式化时间戳为日期
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
