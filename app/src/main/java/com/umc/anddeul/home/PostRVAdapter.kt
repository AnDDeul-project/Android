package com.umc.anddeul.home

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentHomeMyUploadBinding
import com.umc.anddeul.databinding.FragmentHomeUploadBinding
import com.umc.anddeul.home.model.EmojiDTO
import com.umc.anddeul.home.model.EmojiRequest
import com.umc.anddeul.home.model.PostData
import com.umc.anddeul.home.network.EmojiInterface
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class PostRVAdapter(private val context: Context, var postList: List<PostData>, var authorTypeList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ME = 0
        const val VIEW_TYPE_OTHER = 1
    }

    interface MyItemClickListener{
        fun onItemClick(userId: String)
        fun onDeleteClick(postId : Int)
        fun onModifyClick(postId : Int, postImages:List<String>, postContent: String)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener : MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ME -> {
                val binding = FragmentHomeMyUploadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyViewHolder(binding)
            }
            VIEW_TYPE_OTHER -> {
                val binding = FragmentHomeUploadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // 게시글 뷰의 레이아웃을 inflater하고 StudyUploadViewHolder 객체를 생성하여 반환
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyViewHolder -> {
                holder.bind(postList[position])

                // 유저 이름 클릭 시 해당 유저 아이디 전달
                holder.binding.homeMyUploadUsernameTv.setOnClickListener {
                    val userId = postList[position].user_idx
                    mItemClickListener.onItemClick(userId)
                }

                // 유저 프로필 클릭 시 해당 유저 아이디 전달
                holder.binding.homeMyUploadProfileIv.setOnClickListener {
                    val userId = postList[position].user_idx
                    mItemClickListener.onItemClick(userId)
                }
            }
            is ViewHolder -> {
                holder.bind(postList[position])

                // 유저 이름 클릭 시 해당 유저 아이디 전달
                holder.binding.homeUploadUsernameTv.setOnClickListener {
                    val userId = postList[position].user_idx
                    mItemClickListener.onItemClick(userId)
                }

                // 유저 프로필 클릭 시 해당 유저 아이디 전달
                holder.binding.homeUploadProfileIv.setOnClickListener {
                    val userId = postList[position].user_idx
                    mItemClickListener.onItemClick(userId)
                }
            }
        }
    }


    override fun getItemCount(): Int = postList.size // 데이터 세트의 크기를 알려줌 (recyclerView의 마지막이 언제인지를 알게해줌)

    override fun getItemViewType(position: Int): Int {
        return when (authorTypeList[position]) {
            "me" -> VIEW_TYPE_ME
            "other" -> VIEW_TYPE_OTHER
            else -> throw IllegalArgumentException("Invalid author type")
        }
    }

    inner class ViewHolder(val binding: FragmentHomeUploadBinding): RecyclerView.ViewHolder(binding.root) {
        // bind 메서드를 통해 해당 뷰의 텍스트를 게시글 데이터로 설정
        fun bind(postData: PostData) {
            binding.homeUploadUsernameTv.text = postData.nickname
            binding.homeUploadExplainTv.text = postData.content
            binding.homeUploadEmojiIb.setOnClickListener {
                showEmojiPopup(binding, postData.post_idx)
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

    inner class MyViewHolder(val binding: FragmentHomeMyUploadBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(postData: PostData) {
            binding.homeMyUploadUsernameTv.text = postData.nickname
            binding.homeMyUploadExplainTv.text = postData.content
            binding.homeMyUploadEmojiIb.setOnClickListener {
                showMyEmojiPopup(binding, postData.post_idx)
            }

            val profileImageUrl = postData.userImage
            val imageView = binding.homeMyUploadProfileIv
            val loadImage = LoadProfileImage(imageView)
            loadImage.execute(profileImageUrl)

            val imageUrlsString = postData.picture
            Log.e("postRVAdapter", "$imageUrlsString")

            val postVPAdapter = PostVPAdapter(imageUrlsString)
            binding.homeMyUploadImageVp.adapter = postVPAdapter
            binding.homeMyUploadImageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            binding.homeMyUploadMenuIb.setOnClickListener {
                showPopupMenu(it, postData)
            }
        }

        private fun showPopupMenu(view: View, postData: PostData) {
            val popupMenu = PopupMenu(view.context, view, Gravity.END, 0, R.style.PopupMenuStyle)

            popupMenu.inflate(R.menu.home_upload_my_menu)

            val postIdx = postData.post_idx
            val postImages = postData.picture
            val postContent = postData.content
            // 팝업 메뉴 버튼 클릭 시
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.home_my_upload_menu_modify -> {
                        // 수정하기
                        mItemClickListener.onModifyClick(postIdx, postImages, postContent)
                        true
                    }
                    R.id.home_my_upload_menu_delete -> {
                        // 삭제하기
                        mItemClickListener.onDeleteClick(postIdx)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()

        }
    }

    fun showEmojiPopup(binding: FragmentHomeUploadBinding, postId: Int) {

        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        binding.homeEmojiLinear.startAnimation(slideUpAnimation)
        binding.homeEmojiLinear.visibility = View.VISIBLE

        // 사라지는 애니메이션
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        binding.homeEmojiHappy.setOnClickListener{
            // 이모티콘 선택과 관련된 작업 수행
            val spf: SharedPreferences = context.getSharedPreferences("myToken", Context.MODE_PRIVATE)
            // val token = spf.getString("jwtToken", "")
            val token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzI0MTg1MDA0Il0sImlhdCI6MTcwODE0OTYzN30.gdMMpNYi6ewkV8ND2vsU138Z9nryiXQNfr-HvUnQUL8"

            val retrofitBearer = Retrofit.Builder()
                .baseUrl("http://umc-garden.store")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            val request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + token.orEmpty())
                                .build()
                            Log.d("retrofitBearer", "Token: ${token.toString()}" + token.orEmpty())
                            chain.proceed(request)
                        }
                        .build()
                )
                .build()

            val emojiService = retrofitBearer.create(EmojiInterface::class.java)
            val emojiRequest = EmojiRequest("happy_emj")

            emojiService.getEmoji(postId, emojiRequest).enqueue(object : Callback<EmojiDTO> {
                override fun onResponse(call: Call<EmojiDTO>, response: Response<EmojiDTO>) {
                    Log.e("emojiService", "선택한 게시글 id : $postId")
                    Log.e("emojiService", "onResponse code : ${response.code()}")
                    Log.e("emojiService", "${response.body()}")

                    val emojiResponse = response.body()?.result

                    if (response.isSuccessful) {
                        binding.homeEmojiLinear.startAnimation(fadeOutAnimation)
                        binding.homeEmojiLinear.visibility = View.GONE

                        binding.homeEmojiHappyLayout.visibility = View.VISIBLE
                        binding.homeEmojiHappyCount.text = emojiResponse?.happy_emj?.size.toString()
                    }
                }

                override fun onFailure(call: Call<EmojiDTO>, t: Throwable) {
                    Log.e("emojiService", "onFailure")
                    Log.e("emojiService", "Failure message: ${t.message}")
                }
            })
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

    fun showMyEmojiPopup(binding: FragmentHomeMyUploadBinding, postId: Int) {

        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        binding.homeMyEmojiLinear.startAnimation(slideUpAnimation)
        binding.homeMyEmojiLinear.visibility = View.VISIBLE

        // 사라지는 애니메이션
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        binding.homeMyEmojiHappy.setOnClickListener{
            // 이모티콘 선택과 관련된 작업 수행
            val spf: SharedPreferences = context.getSharedPreferences("myToken", Context.MODE_PRIVATE)
            // val token = spf.getString("jwtToken", "")
            val token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzI0MTg1MDA0Il0sImlhdCI6MTcwODE0OTYzN30.gdMMpNYi6ewkV8ND2vsU138Z9nryiXQNfr-HvUnQUL8"

            val retrofitBearer = Retrofit.Builder()
                .baseUrl("http://umc-garden.store")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            val request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + token.orEmpty())
                                .build()
                            Log.d("retrofitBearer", "Token: ${token.toString()}" + token.orEmpty())
                            chain.proceed(request)
                        }
                        .build()
                )
                .build()

            val emojiService = retrofitBearer.create(EmojiInterface::class.java)
            val emojiRequest = EmojiRequest("happy_emj")

            emojiService.getEmoji(postId, emojiRequest).enqueue(object : Callback<EmojiDTO> {
                override fun onResponse(call: Call<EmojiDTO>, response: Response<EmojiDTO>) {
                    Log.e("emojiService", "선택한 게시글 id : $postId")
                    Log.e("emojiService", "onResponse code : ${response.code()}")
                    Log.e("emojiService", "${response.body()}")

                    val emojiResponse = response.body()?.result

                    if (response.isSuccessful) {
                        binding.homeMyEmojiLinear.startAnimation(fadeOutAnimation)
                        binding.homeMyEmojiLinear.visibility = View.GONE

                        binding.homeMyEmojiHappyLayout.visibility = View.VISIBLE
                        binding.homeMyEmojiHappyCount.text = emojiResponse?.happy_emj?.size.toString()
                    }
                }

                override fun onFailure(call: Call<EmojiDTO>, t: Throwable) {
                    Log.e("emojiService", "onFailure")
                    Log.e("emojiService", "Failure message: ${t.message}")
                }
            })
        }
        binding.homeMyEmojiLaugh.setOnClickListener {
            // 이모티콘 선택과 관련된 작업 수행
            binding.homeMyEmojiLinear.startAnimation(fadeOutAnimation)
            binding.homeMyEmojiLinear.visibility = View.GONE

        }
        binding.homeMyEmojiSad.setOnClickListener {
            // 이모티콘 선택과 관련된 작업 수행
            binding.homeMyEmojiLinear.startAnimation(fadeOutAnimation)
            binding.homeMyEmojiLinear.visibility = View.GONE

        }
    }
}