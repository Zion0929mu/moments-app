package com.moments.app.ui.photo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.moments.app.databinding.ActivityPhotoViewBinding

/**
 * 图片查看界面
 * 支持全屏查看、缩放、滑动切换图片
 */
class PhotoViewActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPhotoViewBinding
    private lateinit var adapter: PhotoPagerAdapter
    private var images: List<String> = emptyList()
    private var initialPosition: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 获取图片列表和初始位置
        images = intent.getStringArrayListExtra("images") ?: emptyList()
        initialPosition = intent.getIntExtra("position", 0)
        
        setupViewPager()
        updateIndicator(initialPosition)
    }
    
    /**
     * 设置ViewPager
     */
    private fun setupViewPager() {
        adapter = PhotoPagerAdapter(images) {
            // 点击图片关闭
            finish()
        }
        
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(initialPosition, false)
        
        // 监听页面切换
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicator(position)
            }
        })
    }
    
    /**
     * 更新指示器
     */
    private fun updateIndicator(position: Int) {
        binding.tvIndicator.text = "${position + 1} / ${images.size}"
    }
}
