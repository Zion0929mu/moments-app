package com.moments.app.ui.moments

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moments.app.R
import com.moments.app.data.model.Comment
import com.moments.app.data.model.Moment
import com.moments.app.databinding.ItemMomentBinding
import com.moments.app.utils.DateUtils
import com.moments.app.utils.ImageUtils

/**
 * 朋友圈动态列表Adapter
 * 显示所有朋友圈动态
 */
class MomentAdapter(
    private val currentUserId: String,
    private val onLikeClick: (Moment) -> Unit,
    private val onCommentClick: (Moment, Comment?) -> Unit,  // Comment为null表示评论动态,非null表示回复评论
    private val onImageClick: (Moment, Int) -> Unit,
    private val onDeleteClick: (Moment) -> Unit
) : RecyclerView.Adapter<MomentAdapter.MomentViewHolder>() {
    
    private val moments = mutableListOf<Moment>()
    
    fun submitList(list: List<Moment>) {
        moments.clear()
        moments.addAll(list)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentViewHolder {
        val binding = ItemMomentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MomentViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MomentViewHolder, position: Int) {
        holder.bind(moments[position])
    }
    
    override fun getItemCount(): Int = moments.size
    
    inner class MomentViewHolder(
        private val binding: ItemMomentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(moment: Moment) {
            val context = binding.root.context
            
            // 用户头像
            ImageUtils.loadAvatar(context, moment.userAvatar, binding.ivAvatar)
            
            // 用户名
            binding.tvUserName.text = moment.userName
            
            // 文字内容
            binding.tvContent.text = moment.content
            binding.tvContent.visibility = if (moment.content.isNotEmpty()) View.VISIBLE else View.GONE
            
            // 图片列表
            if (moment.images.isNotEmpty()) {
                binding.rvImages.visibility = View.VISIBLE
                val columns = ImageUtils.getImageGridColumns(moment.images.size)
                binding.rvImages.layoutManager = GridLayoutManager(context, columns)
                binding.rvImages.adapter = MomentImageAdapter(moment.images) { position ->
                    onImageClick(moment, position)
                }
            } else {
                binding.rvImages.visibility = View.GONE
            }
            
            // 位置信息
            if (!moment.location.isNullOrEmpty()) {
                binding.tvLocation.text = moment.location
                binding.tvLocation.visibility = View.VISIBLE
            } else {
                binding.tvLocation.visibility = View.GONE
            }
            
            // 时间
            binding.tvTime.text = DateUtils.getRelativeTimeString(moment.timestamp)
            
            // 更多按钮(只有自己的动态才显示)
            if (moment.userId == currentUserId) {
                binding.btnMore.visibility = View.VISIBLE
                binding.btnMore.setOnClickListener {
                    showMoreMenu(it, moment)
                }
            } else {
                binding.btnMore.visibility = View.GONE
            }
            
            // 操作按钮(点赞/评论)
            binding.btnAction.setOnClickListener {
                showActionMenu(it, moment)
            }
            
            // 点赞和评论区域
            val hasLikes = moment.likeUsers.isNotEmpty()
            val hasComments = moment.comments.isNotEmpty()
            
            if (hasLikes || hasComments) {
                binding.llInteraction.visibility = View.VISIBLE
                
                // 点赞列表
                if (hasLikes) {
                    binding.tvLikes.visibility = View.VISIBLE
                    binding.tvLikes.text = buildLikesText(moment.likeUsers.map { it.name })
                } else {
                    binding.tvLikes.visibility = View.GONE
                }
                
                // 分割线
                binding.dividerLikeComment.visibility = 
                    if (hasLikes && hasComments) View.VISIBLE else View.GONE
                
                // 评论列表
                if (hasComments) {
                    binding.llComments.visibility = View.VISIBLE
                    binding.llComments.removeAllViews()
                    moment.comments.forEach { comment ->
                        addCommentView(moment, comment)
                    }
                } else {
                    binding.llComments.visibility = View.GONE
                }
            } else {
                binding.llInteraction.visibility = View.GONE
            }
        }
        
        /**
         * 显示更多菜单(删除)
         */
        private fun showMoreMenu(view: View, moment: Moment) {
            PopupMenu(view.context, view).apply {
                menuInflater.inflate(R.menu.menu_moment_more, menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_delete -> {
                            onDeleteClick(moment)
                            true
                        }
                        else -> false
                    }
                }
                show()
            }
        }
        
        /**
         * 显示操作菜单(点赞/评论)
         */
        private fun showActionMenu(view: View, moment: Moment) {
            PopupMenu(view.context, view).apply {
                menuInflater.inflate(R.menu.menu_moment_action, menu)
                
                // 根据点赞状态更新菜单文字
                val isLiked = moment.likeUsers.any { it.uid == currentUserId }
                menu.findItem(R.id.action_like)?.title = if (isLiked) "取消赞" else "赞"
                
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_like -> {
                            onLikeClick(moment)
                            true
                        }
                        R.id.action_comment -> {
                            onCommentClick(moment, null)
                            true
                        }
                        else -> false
                    }
                }
                show()
            }
        }
        
        /**
         * 构建点赞文本(带图标)
         */
        private fun buildLikesText(names: List<String>): CharSequence {
            val text = names.joinToString(", ")
            return "❤️ $text"
        }
        
        /**
         * 添加评论视图
         */
        private fun addCommentView(moment: Moment, comment: Comment) {
            val context = binding.root.context
            val textView = TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textSize = 14f
                setPadding(0, 4, 0, 4)
                
                // 构建评论文本
                text = buildCommentText(comment)
                
                // 点击回复
                setOnClickListener {
                    onCommentClick(moment, comment)
                }
            }
            binding.llComments.addView(textView)
        }
        
        /**
         * 构建评论文本(支持回复)
         */
        private fun buildCommentText(comment: Comment): SpannableStringBuilder {
            val context = binding.root.context
            val blueColor = ContextCompat.getColor(context, R.color.link_blue)
            val blackColor = ContextCompat.getColor(context, R.color.text_primary)
            
            val builder = SpannableStringBuilder()
            
            // 评论者名字(蓝色)
            val nameStart = builder.length
            builder.append(comment.userName)
            builder.setSpan(
                ForegroundColorSpan(blueColor),
                nameStart, builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            // 回复对象
            if (comment.replyToUserName != null) {
                builder.append(" 回复 ")
                val replyNameStart = builder.length
                builder.append(comment.replyToUserName)
                builder.setSpan(
                    ForegroundColorSpan(blueColor),
                    replyNameStart, builder.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            
            // 评论内容(黑色)
            builder.append(": ")
            val contentStart = builder.length
            builder.append(comment.content)
            builder.setSpan(
                ForegroundColorSpan(blackColor),
                contentStart, builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            return builder
        }
    }
}
