package com.moments.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moments.app.data.model.User
import com.moments.app.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * 登录界面ViewModel
 * 处理登录相关的业务逻辑
 */
class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    // 登录状态
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState
    
    /**
     * 保存登录用户信息
     */
    fun saveUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.saveUser(user)
                _loginState.value = LoginState.Success(user)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("保存用户信息失败: ${e.message}")
            }
        }
    }
    
    /**
     * 检查是否已登录
     */
    fun checkLoginStatus() {
        viewModelScope.launch {
            // 使用Flow监听数据库中的用户信息
            userRepository.getCurrentUser().collect { user ->
                if (user != null) {
                    _loginState.value = LoginState.Success(user)
                }
            }
        }
    }
    
    /**
     * 登录状态密封类
     */
    sealed class LoginState {
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
        object Loading : LoginState()
    }
}
