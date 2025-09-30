package com.moments.app.ui.post

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.moments.app.MomentsApplication
import com.moments.app.R
import com.moments.app.data.repository.MomentRepository
import com.moments.app.data.repository.UserRepository
import com.moments.app.databinding.ActivityPostBinding
import com.moments.app.viewmodel.PostViewModel
import com.moments.app.viewmodel.ViewModelFactory

/**
 * 发布动态界面
 * 支持输入文字和选择图片(最多9张)
 */
class PostActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPostBinding
    private lateinit var viewModel: PostViewModel
    private lateinit var imageAdapter: PostImageAdapter
    
    // 图片选择器
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addImages(uris)
        }
    }
    
    // 权限请求
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            selectImages()
        } else {
            showToast("需要存储权限才能选择图片")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
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
        viewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]
    }
    
    /**
     * 设置Toolbar
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        imageAdapter = PostImageAdapter(
            images = mutableListOf(),
            onDeleteClick = { position ->
                viewModel.removeImage(position)
            }
        )
        
        binding.rvImages.layoutManager = GridLayoutManager(this, 3)
        binding.rvImages.adapter = imageAdapter
    }
    
    /**
     * 设置点击事件
     */
    private fun setupClickListeners() {
        // 发布按钮
        binding.btnPublish.setOnClickListener {
            publishMoment()
        }
        
        // 添加图片按钮
        binding.btnAddImage.setOnClickListener {
            checkPermissionAndSelectImages()
        }
    }
    
    /**
     * 监听ViewModel数据
     */
    private fun observeViewModel() {
        // 监听选中的图片
        viewModel.selectedImages.observe(this) { images ->
            imageAdapter.updateImages(images)
            
            // 根据图片数量显示/隐藏添加按钮
            binding.btnAddImage.isEnabled = images.size < 9
            binding.btnAddImage.alpha = if (images.size < 9) 1f else 0.5f
        }
        
        // 监听发布状态
        viewModel.postState.observe(this) { state ->
            when (state) {
                is PostViewModel.PostState.Success -> {
                    showToast(getString(R.string.post_success))
                    finish()
                }
                is PostViewModel.PostState.Error -> {
                    showToast(state.message)
                }
                is PostViewModel.PostState.Loading -> {
                    // 可以显示加载进度
                }
            }
        }
    }
    
    /**
     * 检查权限并选择图片
     */
    private fun checkPermissionAndSelectImages() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+使用READ_MEDIA_IMAGES权限
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    selectImages()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                // Android 6-12使用READ_EXTERNAL_STORAGE权限
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    selectImages()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            else -> {
                selectImages()
            }
        }
    }
    
    /**
     * 选择图片
     */
    private fun selectImages() {
        val currentImageCount = viewModel.selectedImages.value?.size ?: 0
        val remainingSlots = 9 - currentImageCount
        
        if (remainingSlots <= 0) {
            showToast(getString(R.string.post_max_images))
            return
        }
        
        imagePickerLauncher.launch("image/*")
    }
    
    /**
     * 发布动态
     */
    private fun publishMoment() {
        val content = binding.etContent.text.toString().trim()
        val images = viewModel.selectedImages.value ?: emptyList()
        
        // 验证内容
        if (content.isEmpty() && images.isEmpty()) {
            showToast(getString(R.string.post_empty_content))
            return
        }
        
        // 获取当前用户
        val app = application as MomentsApplication
        val userDao = app.database.userDao()
        
        // 使用协程获取当前用户
        lifecycleScope.launchWhenStarted {
            userDao.getCurrentUser().collect { user ->
                if (user != null) {
                    viewModel.postMoment(content, user)
                } else {
                    showToast("获取用户信息失败")
                }
            }
        }
    }
    
    /**
     * 显示Toast
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 菜单项点击
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    // 添加lifecycle-runtime-ktx支持
    private val lifecycleScope get() = androidx.lifecycle.lifecycleScope
}
