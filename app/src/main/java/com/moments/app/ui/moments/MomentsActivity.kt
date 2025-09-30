package com.moments.app.ui.moments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.moments.app.MomentsApplication
import com.moments.app.R
import com.moments.app.data.model.Comment
import com.moments.app.data.model.Moment
import com.moments.app.data.model.User
import com.moments.app.data.repository.MomentRepository
import com.moments.app.data.repository.UserRepository
import com.moments.app.databinding.ActivityMomentsBinding
import com.moments.app.databinding.DialogCommentBinding
import com.moments.app.ui.login.LoginActivity
import com.moments.app.ui.photo.PhotoViewActivity
import com.moments.app.ui.post.PostActivity
import com.moments.app.viewmodel.MomentsViewModel
import com.moments.app.viewmodel.ViewModelFactory

/**
 * 朋友圈主界面
 * 显示所有动态,支持点赞、评论等操作
 */
class MomentsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMomentsBinding
    private lateinit var viewModel: MomentsViewModel
    private lateinit var adapter: MomentAdapter
    private var currentUser: User? = null
    private var commentDialog: BottomSheetDialog? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMomentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initViewModel()
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    /**
     * 初始化ViewModel
     */
    private fun initViewModel() {
        val app = application as MomentsApplication
        val factory = ViewModelFactory(
            userRepository = UserRepository(app.database.userDao()),
            momentRepository = MomentRepository(app.database.momentDao())
        )
        viewModel = ViewModelProvider(this, factory)[MomentsViewModel::class.java]
    }
    
    /**
     * 设置Toolbar
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = MomentAdapter(
            currentUserId = currentUser?.uid ?: "",
            onLikeClick = { moment ->
                currentUser?.let { user ->
                    viewModel.toggleLike(moment.id, user)
                }
            },
            onCommentClick = { moment, replyToComment ->
                showCommentDialog(moment, replyToComment)
            },
            onImageClick = { moment, position ->
                openPhotoViewer(moment.images, position)
            },
            onDeleteClick = { moment ->
                showDeleteConfirmDialog(moment)
            }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        
        // 下拉刷新
        binding.swipeRefresh.setOnRefreshListener {
            // 实际项目中这里可以刷新数据
            binding.swipeRefresh.isRefreshing = false
        }
    }
    
    /**
     * 设置点击事件
     */
    private fun setupClickListeners() {
        // 发布按钮
        binding.fabPost.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }
    }
    
    /**
     * 监听ViewModel数据
     */
    private fun observeViewModel() {
        // 监听当前用户
        viewModel.currentUser.observe(this) { user ->
            currentUser = user
            if (user == null) {
                // 用户未登录,返回登录界面
                navigateToLogin()
            } else {
                // 更新封面区域的用户信息
                binding.tvUserName.text = user.displayName
                ImageUtils.loadAvatar(this, user.photoUrl, binding.ivUserAvatar)
                
                // 更新Adapter的当前用户ID
                adapter = MomentAdapter(
                    currentUserId = user.uid,
                    onLikeClick = { moment ->
                        viewModel.toggleLike(moment.id, user)
                    },
                    onCommentClick = { moment, replyToComment ->
                        showCommentDialog(moment, replyToComment)
                    },
                    onImageClick = { moment, position ->
                        openPhotoViewer(moment.images, position)
                    },
                    onDeleteClick = { moment ->
                        showDeleteConfirmDialog(moment)
                    }
                )
                binding.recyclerView.adapter = adapter
            }
        }
        
        // 监听朋友圈动态列表
        viewModel.moments.observe(this) { moments ->
            if (moments.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(moments)
            }
        }
        
        // 监听操作结果
        viewModel.operationState.observe(this) { state ->
            when (state) {
                is MomentsViewModel.OperationState.Success -> {
                    // 评论成功后关闭对话框
                    commentDialog?.dismiss()
                }
                is MomentsViewModel.OperationState.Error -> {
                    showToast(state.message)
                }
                is MomentsViewModel.OperationState.Logout -> {
                    navigateToLogin()
                }
            }
        }
    }
    
    /**
     * 显示评论对话框
     */
    private fun showCommentDialog(moment: Moment, replyToComment: Comment?) {
        val dialogBinding = DialogCommentBinding.inflate(layoutInflater)
        
        // 设置提示文字
        if (replyToComment != null) {
            dialogBinding.etComment.hint = getString(
                R.string.comment_reply_hint,
                replyToComment.userName
            )
        }
        
        // 创建BottomSheetDialog
        commentDialog = BottomSheetDialog(this).apply {
            setContentView(dialogBinding.root)
            
            // 自动弹出键盘
            dialogBinding.etComment.requestFocus()
            window?.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            
            // 发送按钮点击
            dialogBinding.btnSend.setOnClickListener {
                val content = dialogBinding.etComment.text.toString().trim()
                if (content.isEmpty()) {
                    showToast("请输入评论内容")
                    return@setOnClickListener
                }
                
                currentUser?.let { user ->
                    val replyTo = if (replyToComment != null) {
                        Pair(replyToComment.userId, replyToComment.userName)
                    } else {
                        null
                    }
                    viewModel.addComment(moment.id, content, user, replyTo)
                }
            }
            
            // 键盘发送键
            dialogBinding.etComment.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    dialogBinding.btnSend.performClick()
                    true
                } else {
                    false
                }
            }
            
            show()
        }
    }
    
    /**
     * 显示删除确认对话框
     */
    private fun showDeleteConfirmDialog(moment: Moment) {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_confirm)
            .setPositiveButton(R.string.confirm) { _, _ ->
                viewModel.deleteMoment(moment)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    /**
     * 打开图片查看器
     */
    private fun openPhotoViewer(images: List<String>, position: Int) {
        val intent = Intent(this, PhotoViewActivity::class.java).apply {
            putStringArrayListExtra("images", ArrayList(images))
            putExtra("position", position)
        }
        startActivity(intent)
    }
    
    /**
     * 返回登录界面
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    /**
     * 显示Toast
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 创建菜单
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_moments_toolbar, menu)
        return true
    }
    
    /**
     * 菜单项点击
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * 显示登出确认对话框
     */
    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(this)
            .setMessage("确定要退出登录吗?")
            .setPositiveButton(R.string.confirm) { _, _ ->
                viewModel.logout()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
