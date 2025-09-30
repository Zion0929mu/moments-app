package com.moments.app.ui.post

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moments.app.databinding.ItemPostImageBinding
import com.moments.app.utils.ImageUtils

/**
 * 发布动态图片选择Adapter
 * 显示已选择的图片,支持删除
 */
class PostImageAdapter(
    private val images: MutableList<Uri>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<PostImageAdapter.ImageViewHolder>() {
    
    fun updateImages(newImages: List<Uri>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemPostImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], position)
    }
    
    override fun getItemCount(): Int = images.size
    
    inner class ImageViewHolder(
        private val binding: ItemPostImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(uri: Uri, position: Int) {
            // 加载图片
            ImageUtils.loadImageFromUri(binding.root.context, uri, binding.ivImage)
            
            // 删除按钮
            binding.btnDelete.setOnClickListener {
                onDeleteClick(position)
            }
        }
    }
}
