package com.moments.app.data.local

import androidx.room.*
import com.moments.app.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象(DAO)
 * 定义与用户表相关的数据库操作
 */
@Dao
interface UserDao {
    
    /**
     * 插入或更新用户
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    /**
     * 根据UID获取用户
     */
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): User?
    
    /**
     * 获取当前登录用户(最后登录的用户)
     */
    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    fun getCurrentUser(): Flow<User?>
    
    /**
     * 删除用户
     */
    @Delete
    suspend fun deleteUser(user: User)
    
    /**
     * 清空所有用户
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
