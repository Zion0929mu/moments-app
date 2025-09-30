package com.moments.app.data.repository

import com.moments.app.data.local.MomentDao
import com.moments.app.data.model.Comment
import com.moments.app.data.model.LikeUser
import com.moments.app.data.model.Moment
import kotlinx.coroutines.flow.Flow

/**
 * 朋友圈动态数据仓库
 * 负责动态数据的CRUD操作和业务逻辑
 */
class MomentRepository(private val momentDao: MomentDao) {
    
    /**
     * 获取所有动态(按时间倒序)
     */
    fun getAllMoments(): Flow<List<Moment>> {
        return momentDao.getAllMoments()
    }
    
    /**
     * 发布新动态
     */
    suspend fun postMoment(moment: Moment) {
        momentDao.insertMoment(moment)
    }
    
    /**
     * 点赞/取消点赞
     */
    suspend fun toggleLike(momentId: String, user: LikeUser) {
        val moment = momentDao.getMomentById(momentId) ?: return
        
        val likeUsers = moment.likeUsers.toMutableList()
        val existingIndex = likeUsers.indexOfFirst { it.uid == user.uid }
        
        if (existingIndex != -1) {
            // 已点赞,取消点赞
            likeUsers.removeAt(existingIndex)
        } else {
            // 未点赞,添加点赞
            likeUsers.add(user)
        }
        
        val updatedMoment = moment.copy(
            likeUsers = likeUsers,
            likeCount = likeUsers.size
        )
        momentDao.updateMoment(updatedMoment)
    }
    
    /**
     * 添加评论
     */
    suspend fun addComment(momentId: String, comment: Comment) {
        val moment = momentDao.getMomentById(momentId) ?: return
        
        val comments = moment.comments.toMutableList()
        comments.add(comment)
        
        val updatedMoment = moment.copy(comments = comments)
        momentDao.updateMoment(updatedMoment)
    }
    
    /**
     * 删除动态
     */
    suspend fun deleteMoment(moment: Moment) {
        momentDao.deleteMoment(moment)
    }
    
    /**
     * 获取用户发布的动态
     */
    fun getMomentsByUser(userId: String): Flow<List<Moment>> {
        return momentDao.getMomentsByUser(userId)
    }
}
