package com.umc.anddeul.mypage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMyPostBinding
import com.umc.anddeul.databinding.FragmentUserPostBinding
import com.umc.anddeul.home.DeleteDialog
import com.umc.anddeul.home.LoadProfileImage
import com.umc.anddeul.home.PostModifyActivity
import com.umc.anddeul.home.PostVPAdapter
import com.umc.anddeul.home.model.EmojiDTO
import com.umc.anddeul.home.model.EmojiRequest
import com.umc.anddeul.home.model.OnePostDTO
import com.umc.anddeul.home.model.OnePostData
import com.umc.anddeul.home.model.PostData
import com.umc.anddeul.home.network.EmojiInterface
import com.umc.anddeul.home.network.OnePostInterface
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPostFragment : Fragment() {
    lateinit var binding: FragmentMyPostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)

        setToolbar()
        loadPost()

        return binding.root
    }

    fun setToolbar() {
        binding.myPostToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)

            setNavigationOnClickListener {
                // UserProfileFragment로 이동
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
    }

    fun loadPost() {
        val postIdxJson = arguments?.getInt("postIdx")
        val postId: Int = postIdxJson ?: 0

        val spf: SharedPreferences =
            requireActivity().getSharedPreferences("myToken", Context.MODE_PRIVATE)
        // val token = spf.getString("jwtToken", "")
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzA0MTMzMDkzIl0sImlhdCI6MTcwNzExNDkyMn0.xUiMr__vOcdjOVjcrmV3HiuWOqatI1PPmSPgJFljwTw"

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

        val onePostService = retrofitBearer.create(OnePostInterface::class.java)

        onePostService.onePost(postId).enqueue(object : Callback<OnePostDTO> {
            override fun onResponse(call: Call<OnePostDTO>, response: Response<OnePostDTO>) {
                Log.e("myPostService", "onResponse")
                Log.e("myPostService response code : ", "${response.code()}")
                Log.e("myPostService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val postData = response.body()?.result

                    postData?.let {
                        binding.myPostUsernameTv.text = postData.nickname
                        binding.myPostExplainTv.text = postData.content
                        binding.myPostEmojiIb.setOnClickListener {
                            // 이모지 설정
                            showEmojiPopup(postId)
                        }

                        // 메뉴 설정 (수정, 삭제)
                        binding.myPostMenu.setOnClickListener {
                            showPopupMenu(it, postData)
                        }

                        // 프로필 사진 설정
                        val imageView = binding.myPostProfileIv
                        val loadImage = LoadProfileImage(imageView)
                        loadImage.execute(postData.userImage)

                        // 게시글 이미지 설정
                        val imageUrlsString = postData.picture

                        val postVPAdapter = PostVPAdapter(imageUrlsString)
                        binding.myPostImageVp.adapter = postVPAdapter
                        binding.myPostImageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    }
                }
            }

            override fun onFailure(call: Call<OnePostDTO>, t: Throwable) {
                Log.e("myPostService", "onFailure")
                Log.e("myPostService", "Failure message: ${t.message}")
            }
        })
    }

    private fun showPopupMenu(view: View, postData: OnePostData) {
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
                    onModifyClick(postIdx, postImages, postContent)
                    true
                }
                R.id.home_my_upload_menu_delete -> {
                    // 삭제하기
                    onDeleteClick(postIdx)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()

    }

    fun onDeleteClick(postId: Int) {
        val deleteDialog = DeleteDialog(postId)
        deleteDialog.isCancelable = false
        deleteDialog.show(requireActivity().supportFragmentManager, "delete dialog")

    }

    fun onModifyClick(postId: Int, selectedImages: List<String>, postContent: String) {
        val intent = Intent(requireContext(), PostModifyActivity::class.java)

        intent.putStringArrayListExtra("selectedImages", ArrayList(selectedImages))
        intent.putExtra("postId", postId)
        intent.putExtra("postContent", postContent)

        // 다음 액티비티 시작
        startActivity(intent)
    }

    fun showEmojiPopup(postId : Int) {
        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        binding.myPostEmojiLinear.startAnimation(slideUpAnimation)
        binding.myPostEmojiLinear.visibility = View.VISIBLE

        binding.myPostEmojiHappy.setOnClickListener{
            selectEmoji(postId, "happy_emj")
        }
        binding.myPostEmojiLaugh.setOnClickListener {
            selectEmoji(postId, "laugh_emj")
        }
        binding.myPostEmojiSad.setOnClickListener {
            selectEmoji(postId, "sad_emj")
        }
    }

    fun selectEmoji(postId: Int, emojiType: String) {
        // 사라지는 애니메이션
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        val spf: SharedPreferences = requireActivity().getSharedPreferences("myToken", Context.MODE_PRIVATE)
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
        val emojiRequest = EmojiRequest(emojiType)

        emojiService.getEmoji(postId, emojiRequest).enqueue(object : Callback<EmojiDTO> {
            override fun onResponse(call: Call<EmojiDTO>, response: Response<EmojiDTO>) {
                Log.e("emojiService", "선택한 게시글 id : $postId")
                Log.e("emojiService", "onResponse code : ${response.code()}")
                Log.e("emojiService", "${response.body()}")

                val emojiResponse = response.body()?.result

                if (response.isSuccessful) {
                    binding.myPostEmojiLinear.startAnimation(fadeOutAnimation)
                    binding.myPostEmojiLinear.visibility = View.GONE

                    binding.myPostEmojiHappyLayout.visibility = View.VISIBLE

                    if(emojiType == "happy_emj") {
                        binding.myPostEmojiHappyOne.visibility = View.VISIBLE
                        binding.myPostEmojiFunOne.visibility = View.GONE
                        binding.myPostEmojiSadOne.visibility = View.GONE
                        binding.myPostEmojiHappyCount.text = emojiResponse?.happy_emj?.size.toString()

                        if(emojiResponse?.happy_emj?.size == 0) {
                            binding.myPostEmojiHappyLayout.visibility = View.GONE
                        }
                    }

                    if(emojiType == "laugh_emj") {
                        binding.myPostEmojiHappyOne.visibility = View.GONE
                        binding.myPostEmojiFunOne.visibility = View.VISIBLE
                        binding.myPostEmojiSadOne.visibility = View.GONE
                        binding.myPostEmojiHappyCount.text = emojiResponse?.laugh_emj?.size.toString()

                        if(emojiResponse?.laugh_emj?.size == 0) {
                            binding.myPostEmojiHappyLayout.visibility = View.GONE
                        }
                    }

                    if (emojiType == "sad_emj") {
                        binding.myPostEmojiHappyOne.visibility = View.GONE
                        binding.myPostEmojiFunOne.visibility = View.GONE
                        binding.myPostEmojiSadOne.visibility = View.VISIBLE
                        binding.myPostEmojiHappyCount.text = emojiResponse?.sad_emj?.size.toString()

                        if(emojiResponse?.sad_emj?.size == 0) {
                            binding.myPostEmojiHappyLayout.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<EmojiDTO>, t: Throwable) {
                Log.e("emojiService", "onFailure")
                Log.e("emojiService", "Failure message: ${t.message}")
            }
        })

    }

}