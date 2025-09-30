package com.moments.app.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moments.app.data.model.Moment
import com.moments.app.data.model.User
import com.moments.app.data.repository.MomentRepository
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 发布动态界面ViewModel
 * 处理发布动态的业务逻辑
 */
class PostViewModel(private val momentRepository: MomentRepository) : ViewModel() {
    
    // 选中的图片列表
    private val _selectedImages = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val selectedImages: LiveData<MutableList<Uri>> = _selectedImages
    
    // 发布状态
    private val _postState = MutableLiveData<PostState>()
    val postState: LiveData<PostState> = _postState
    
    /**
     * 添加图片
     */
    fun addImage(uri: Uri) {
        val currentList = _selectedImages.value ?: mutableListOf()
        if (currentList.size < 9) {
            currentList.add(uri)
            _selectedImages.value = currentList
        }
    }
    
    /**
     * 添加多张图片
     */
    fun addImages(uris: List<Uri>) {
        val currentList = _selectedImages.value ?: mutableListOf()
        val remainingSlots = 9 - currentList.size
        val imagesToAdd = uris.take(remainingSlots)
        currentList.addAll(imagesToAdd)
        _selectedImages.value = currentList
    }
    
    /**
     * 移除图片
     */
    fun removeImage(position: Int) {
        val currentList = _selectedImages.value ?: mutableListOf()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            _selectedImages.value = currentList
        }
    }
    
    /**
     * 清空图片
     */
    fun clearImages() {
        _selectedImages.value = mutableListOf()
    }
    
    /**
     * 发布动态
     */
    fun postMoment(content: String, currentUser: User, location: String? = null) {
        viewModelScope.launch {
            try {
                _postState.value = PostState.Loading
                
                // 将Uri转换为String路径
                val imagePaths = _selectedImages.value?.map { it.toString() } ?: emptyList()
                
                val moment = Moment(
                    id = UUID.randomUUID().toString(),
                    userId = currentUser.uid,
                    userName = currentUser.displayName,
                    userAvatar = currentUser.photoUrl,
                    content = content,
                    images = imagePaths,
                    location = location,
                    timestamp = System.currentTimeMillis()
                )
                
                momentRepository.postMoment(moment)
                _postState.value = PostState.Success
            } catch (e: Exception) {
                _postState.value = PostState.Error("发布失败: ${e.message}")
            }
        }
    }
    
    /**
     * 发布状态密封类
     */
    sealed class PostState {
        object Success : PostState()
        data class Error(val message: String) : PostState()
        object Loading : PostState()
    }
}
