package com.moments.app.ui.moments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moments.app.databinding.ItemMomentImageBinding
import com.moments.app.utils.ImageUtils

/**
 * 朋友圈图片网格Adapter
 * 显示动态中的图片列表
 */
class MomentImageAdapter(
    private val images: List<String>,
    private val onImageClick: (position: Int) -> Unit
) : RecyclerView.Adapter<MomentImageAdapter.ImageViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemMomentImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], position)
    }
    
    override fun getItemCount(): Int = images.size
    
    inner class ImageViewHolder(
        private val binding: ItemMomentImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(imageUrl: String, position: Int) {
            // 加载图片
            ImageUtils.loadImage(binding.root.context, imageUrl, binding.ivImage)
            
            // 点击查看大图
            binding.ivImage.setOnClickListener {
                onImageClick(position)
            }
        }
    }
}
