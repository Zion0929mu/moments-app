package com.moments.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.moments.app.data.model.Comment
import com.moments.app.data.model.LikeUser
import com.moments.app.data.model.Moment
import com.moments.app.data.model.User
import com.moments.app.data.repository.MomentRepository
import com.moments.app.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 朋友圈界面ViewModel
 * 处理朋友圈相关的业务逻辑
 */
class MomentsViewModel(
    private val momentRepository: MomentRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    // 当前用户
    val currentUser: LiveData<User?> = userRepository.getCurrentUser().asLiveData()
    
    // 朋友圈动态列表
    val moments: LiveData<List<Moment>> = momentRepository.getAllMoments().asLiveData()
    
    // 操作结果状态
    private val _operationState = MutableLiveData<OperationState>()
    val operationState: LiveData<OperationState> = _operationState
    
    /**
     * 点赞/取消点赞
     */
    fun toggleLike(momentId: String, currentUser: User) {
        viewModelScope.launch {
            try {
                val likeUser = LikeUser(currentUser.uid, currentUser.displayName)
                momentRepository.toggleLike(momentId, likeUser)
                _operationState.value = OperationState.Success("操作成功")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("操作失败: ${e.message}")
            }
        }
    }
    
    /**
     * 添加评论
     */
    fun addComment(
        momentId: String,
        content: String,
        currentUser: User,
        replyToUser: Pair<String, String>? = null  // userId to userName
    ) {
        viewModelScope.launch {
            try {
                val comment = Comment(
                    id = UUID.randomUUID().toString(),
                    userId = currentUser.uid,
                    userName = currentUser.displayName,
                    content = content,
                    replyToUserId = replyToUser?.first,
                    replyToUserName = replyToUser?.second,
                    timestamp = System.currentTimeMillis()
                )
                momentRepository.addComment(momentId, comment)
                _operationState.value = OperationState.Success("评论成功")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("评论失败: ${e.message}")
            }
        }
    }
    
    /**
     * 删除动态
     */
    fun deleteMoment(moment: Moment) {
        viewModelScope.launch {
            try {
                momentRepository.deleteMoment(moment)
                _operationState.value = OperationState.Success("删除成功")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("删除失败: ${e.message}")
            }
        }
    }
    
    /**
     * 登出
     */
    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logout()
                _operationState.value = OperationState.Logout
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("登出失败: ${e.message}")
            }
        }
    }
    
    /**
     * 操作状态密封类
     */
    sealed class OperationState {
        data class Success(val message: String) : OperationState()
        data class Error(val message: String) : OperationState()
        object Logout : OperationState()
    }
}
