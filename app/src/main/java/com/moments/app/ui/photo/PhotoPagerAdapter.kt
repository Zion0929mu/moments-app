package com.moments.app.ui.photo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moments.app.databinding.ItemPhotoBinding
import com.moments.app.utils.ImageUtils

/**
 * 图片查看器Adapter
 * 支持ViewPager2滑动查看多张图片
 */
class PhotoPagerAdapter(
    private val images: List<String>,
    private val onPhotoTap: () -> Unit
) : RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PhotoViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(images[position])
    }
    
    override fun getItemCount(): Int = images.size
    
    inner class PhotoViewHolder(
        private val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(imageUrl: String) {
            // 加载图片
            ImageUtils.loadImage(binding.root.context, imageUrl, binding.photoView)
            
            // 点击图片关闭查看器
            binding.photoView.setOnPhotoTapListener { _, _, _ ->
                onPhotoTap()
            }
        }
    }
}
