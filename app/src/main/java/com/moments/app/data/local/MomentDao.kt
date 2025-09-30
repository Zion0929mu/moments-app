package com.moments.app.data.local

import androidx.room.*
import com.moments.app.data.model.Moment
import kotlinx.coroutines.flow.Flow

/**
 * 朋友圈动态数据访问对象(DAO)
 * 定义与动态表相关的数据库操作
 */
@Dao
interface MomentDao {
    
    /**
     * 插入新动态
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoment(moment: Moment)
    
    /**
     * 获取所有动态(按时间倒序)
     */
    @Query("SELECT * FROM moments ORDER BY timestamp DESC")
    fun getAllMoments(): Flow<List<Moment>>
    
    /**
     * 根据ID获取动态
     */
    @Query("SELECT * FROM moments WHERE id = :momentId")
    suspend fun getMomentById(momentId: String): Moment?
    
    /**
     * 更新动态
     */
    @Update
    suspend fun updateMoment(moment: Moment)
    
    /**
     * 删除动态
     */
    @Delete
    suspend fun deleteMoment(moment: Moment)
    
    /**
     * 根据用户ID获取该用户的所有动态
     */
    @Query("SELECT * FROM moments WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMomentsByUser(userId: String): Flow<List<Moment>>
    
    /**
     * 清空所有动态
     */
    @Query("DELETE FROM moments")
    suspend fun deleteAllMoments()
}
