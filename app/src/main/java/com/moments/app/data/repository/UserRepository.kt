package com.moments.app.data.repository

import com.moments.app.data.local.UserDao
import com.moments.app.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据仓库
 * 负责用户数据的CRUD操作
 */
class UserRepository(private val userDao: UserDao) {
    
    /**
     * 保存用户信息
     */
    suspend fun saveUser(user: User) {
        userDao.insertUser(user)
    }
    
    /**
     * 获取当前登录用户
     */
    fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUser()
    }
    
    /**
     * 根据ID获取用户
     */
    suspend fun getUserById(uid: String): User? {
        return userDao.getUserById(uid)
    }
    
    /**
     * 登出(清空所有用户数据)
     */
    suspend fun logout() {
        userDao.deleteAllUsers()
    }
}
