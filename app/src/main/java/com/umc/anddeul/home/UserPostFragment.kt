package com.umc.anddeul.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentUserPostBinding
import com.umc.anddeul.home.model.EmojiDTO
import com.umc.anddeul.home.model.EmojiRequest
import com.umc.anddeul.home.model.OnePostDTO
import com.umc.anddeul.home.network.EmojiInterface
import com.umc.anddeul.home.network.OnePostInterface
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserPostFragment : Fragment() {
    lateinit var binding: FragmentUserPostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserPostBinding.inflate(inflater, container, false)

        setToolbar()
        loadPost()

        return binding.root
    }

    fun setToolbar() {
        binding.userPostToolbar.apply {
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
                Log.e("onePostService", "onResponse")
                Log.e("onePostService response code : ", "${response.code()}")
                Log.e("onePostService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val postData = response.body()?.result

                    postData?.let {
                        binding.userPostUsernameTv.text = postData.nickname
                        binding.userPostExplainTv.text = postData.content
                        binding.userPostEmojiIb.setOnClickListener {
                            // 이모지 설정
                            showEmojiPopup(postId)
                        }

                        // 프로필 사진 설정
                        val imageView = binding.userPostProfileIv
                        val loadImage = LoadProfileImage(imageView)
                        loadImage.execute(postData.userImage)

                        // 게시글 이미지 설정
                        val imageUrlsString = postData.picture

                        val postVPAdapter = PostVPAdapter(imageUrlsString)
                        binding.userPostImageVp.adapter = postVPAdapter
                        binding.userPostImageVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    }
                }
            }

            override fun onFailure(call: Call<OnePostDTO>, t: Throwable) {
                Log.e("onePostService", "onFailure")
                Log.e("onePostService", "Failure message: ${t.message}")
            }
        })
    }

    fun showEmojiPopup(postId : Int) {
        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        binding.userPostEmojiLinear.startAnimation(slideUpAnimation)
        binding.userPostEmojiLinear.visibility = View.VISIBLE

        // 사라지는 애니메이션
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        binding.userPostEmojiHappy.setOnClickListener{
            // 이모티콘 선택과 관련된 작업 수행
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
            val emojiRequest = EmojiRequest("happy_emj")

            emojiService.getEmoji(postId, emojiRequest).enqueue(object : Callback<EmojiDTO> {
                override fun onResponse(call: Call<EmojiDTO>, response: Response<EmojiDTO>) {
                    Log.e("emojiService", "선택한 게시글 id : $postId")
                    Log.e("emojiService", "onResponse code : ${response.code()}")
                    Log.e("emojiService", "${response.body()}")

                    val emojiResponse = response.body()?.result

                    if (response.isSuccessful) {
                        binding.userPostEmojiLinear.startAnimation(fadeOutAnimation)
                        binding.userPostEmojiLinear.visibility = View.GONE

                        binding.userPostEmojiHappyLayout.visibility = View.VISIBLE
                        binding.userPostEmojiHappyCount.text = emojiResponse?.happy_emj?.size.toString()
                    }
                }

                override fun onFailure(call: Call<EmojiDTO>, t: Throwable) {
                    Log.e("emojiService", "onFailure")
                    Log.e("emojiService", "Failure message: ${t.message}")
                }
            })
        }
        binding.userPostEmojiLaugh.setOnClickListener {
            // 이모티콘 선택과 관련된 작업 수행
            binding.userPostEmojiLinear.startAnimation(fadeOutAnimation)
            binding.userPostEmojiLinear.visibility = View.GONE

        }
        binding.userPostEmojiSad.setOnClickListener {
            // 이모티콘 선택과 관련된 작업 수행
            binding.userPostEmojiLinear.startAnimation(fadeOutAnimation)
            binding.userPostEmojiLinear.visibility = View.GONE

        }
    }
}