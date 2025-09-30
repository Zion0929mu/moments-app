package com.moments.app.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * 图片加载工具类
 * 封装Glide图片加载功能
 */
object ImageUtils {
    
    /**
     * 加载圆形头像
     */
    fun loadAvatar(context: Context, url: String?, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }
    
    /**
     * 加载普通图片
     */
    fun loadImage(context: Context, url: String?, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }
    
    /**
     * 加载Uri图片
     */
    fun loadImageFromUri(context: Context, uri: Uri, imageView: ImageView) {
        Glide.with(context)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }
    
    /**
     * 获取图片网格列数
     * 根据图片数量返回合适的列数
     */
    fun getImageGridColumns(imageCount: Int): Int {
        return when (imageCount) {
            1 -> 1
            2, 4 -> 2
            else -> 3
        }
    }
    
    /**
     * 计算图片网格的行数
     */
    fun getImageGridRows(imageCount: Int, columns: Int): Int {
        return (imageCount + columns - 1) / columns
    }
}
