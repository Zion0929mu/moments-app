package com.moments.app

import android.app.Application
import com.moments.app.data.local.AppDatabase

/**
 * Application类 - 应用程序入口
 * 负责初始化全局组件和数据库
 */
class MomentsApplication : Application() {
    
    // 数据库实例
    val database: AppDatabase by lazy { 
        AppDatabase.getDatabase(this) 
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: MomentsApplication
            private set
    }
}
