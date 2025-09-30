package com.moments.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.moments.app.MomentsApplication
import com.moments.app.R
import com.moments.app.data.model.User
import com.moments.app.databinding.ActivityLoginBinding
import com.moments.app.ui.moments.MomentsActivity
import com.moments.app.viewmodel.LoginViewModel
import com.moments.app.viewmodel.ViewModelFactory

/**
 * 登录界面
 * 实现Google登录功能
 */
class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    
    // Google登录结果处理
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed", e)
            showError("Google登录失败: ${e.message}")
            hideLoading()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initViewModel()
        initGoogleSignIn()
        setupClickListeners()
        observeViewModel()
        
        // 检查是否已登录
        viewModel.checkLoginStatus()
    }
    
    /**
     * 初始化ViewModel
     */
    private fun initViewModel() {
        val app = application as MomentsApplication
        val factory = ViewModelFactory(
            userRepository = com.moments.app.data.repository.UserRepository(app.database.userDao()),
            momentRepository = com.moments.app.data.repository.MomentRepository(app.database.momentDao())
        )
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }
    
    /**
     * 初始化Google登录配置
     */
    private fun initGoogleSignIn() {
        // 配置Google登录选项
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // 从google-services.json自动生成
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()
    }
    
    /**
     * 设置点击事件
     */
    private fun setupClickListeners() {
        binding.btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }
    }
    
    /**
     * 监听ViewModel数据变化
     */
    private fun observeViewModel() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    hideLoading()
                    navigateToMoments()
                }
                is LoginViewModel.LoginState.Error -> {
                    hideLoading()
                    showError(state.message)
                }
                is LoginViewModel.LoginState.Loading -> {
                    showLoading()
                }
            }
        }
    }
    
    /**
     * 开始Google登录流程
     */
    private fun signInWithGoogle() {
        showLoading()
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
    
    /**
     * 使用Google账号进行Firebase认证
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle: ${account.id}")
        
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 登录成功,保存用户信息
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        val user = User(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            displayName = firebaseUser.displayName ?: "用户",
                            photoUrl = firebaseUser.photoUrl?.toString()
                        )
                        viewModel.saveUser(user)
                    }
                } else {
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    hideLoading()
                    showError("Firebase认证失败")
                }
            }
    }
    
    /**
     * 跳转到朋友圈界面
     */
    private fun navigateToMoments() {
        val intent = Intent(this, MomentsActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    /**
     * 显示加载状态
     */
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnGoogleLogin.isEnabled = false
    }
    
    /**
     * 隐藏加载状态
     */
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnGoogleLogin.isEnabled = true
    }
    
    /**
     * 显示错误信息
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    companion object {
        private const val TAG = "LoginActivity"
    }
}
