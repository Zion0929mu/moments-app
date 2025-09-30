package com.moments.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moments.app.data.repository.MomentRepository
import com.moments.app.data.repository.UserRepository

/**
 * ViewModel工厂类
 * 用于创建带参数的ViewModel实例
 */
class ViewModelFactory(
    private val userRepository: UserRepository,
    private val momentRepository: MomentRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MomentsViewModel::class.java) -> {
                MomentsViewModel(momentRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(momentRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
