package com.moments.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.moments.app.data.model.Moment
import com.moments.app.data.model.User

/**
 * Room数据库配置
 * 管理所有数据表和DAO
 */
@Database(
    entities = [User::class, Moment::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun momentDao(): MomentDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * 获取数据库实例(单例模式)
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moments_database"
                )
                    .fallbackToDestructiveMigration()  // 版本升级时清空数据(简单方案)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
