package com.umc.anddeul.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentHomeUploadBinding
import com.umc.anddeul.home.model.PostData

class PostRVAdapter(private val context: Context, var postList: List<PostData>) : RecyclerView.Adapter<PostRVAdapter.ViewHolder>() {

    interface MyItemClickListener{
        fun onItemClick(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener : MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PostRVAdapter.ViewHolder {
        val binding: FragmentHomeUploadBinding = FragmentHomeUploadBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    // 게시글 뷰의 레이아웃을 inflater하고 StudyUploadViewHolder 객체를 생성하여 반환
    override fun onBindViewHolder(holder: PostRVAdapter.ViewHolder, position: Int) {
        holder.bind(postList[position])
        holder.binding.homeUploadUsernameTv.setOnClickListener {
            mItemClickListener.onItemClick(position)
        }

    }

    override fun getItemCount(): Int = postList.size // 데이터 세트의 크기를 알려줌 (recyclerView의 마지막이 언제인지를 알게해줌)

    inner class ViewHolder(val binding: FragmentHomeUploadBinding): RecyclerView.ViewHolder(binding.root) {
        // bind 메서드를 통해 해당 뷰의 텍스트를 게시글 데이터로 설정
        fun bind(postData: PostData) {
            binding.homeUploadUsernameTv.text = postData.user_idx
            binding.homeUploadExplainTv.text = postData.content
            binding.homeUploadEmojiIb.setOnClickListener {
                showEmojiPopup(binding)
            }

            val profileImageUrl = postData.userImage
            val imageView = binding.homeUploadProfileIv
            val loadImage = LoadProfileImage(imageView)
            loadImage.execute(profileImageUrl)

            val imageUrlsString = postData.picture
            Log.e("postRVAdapter", "$imageUrlsString")

            val postVPAdapter = PostVPAdapter(imageUrlsString)
            binding.homeUploadImageVp.adapter = postVPAdapter
            binding.homeUploadImageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        }
    }

    fun showEmojiPopup(binding: FragmentHomeUploadBinding) {

        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        binding.homeEmojiLinear.startAnimation(slideUpAnimation)
        binding.homeEmojiLinear.visibility = View.VISIBLE

        // 사라지는 애니메이션
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        binding.homeEmojiHappy.setOnClickListener{
            // 이모티콘 선택과 관련된 작업 수행
            binding.homeEmojiLinear.startAnimation(fadeOutAnimation)
            binding.homeEmojiLinear.visibility = View.GONE

        }
        binding.homeEmojiLaugh.setOnClickListener {
            // 이모티콘 선택과 관련된 작업 수행
            binding.homeEmojiLinear.startAnimation(fadeOutAnimation)
            binding.homeEmojiLinear.visibility = View.GONE

        }
        binding.homeEmojiSad.setOnClickListener {
            // 이모티콘 선택과 관련된 작업 수행
            binding.homeEmojiLinear.startAnimation(fadeOutAnimation)
            binding.homeEmojiLinear.visibility = View.GONE

        }
    }
}