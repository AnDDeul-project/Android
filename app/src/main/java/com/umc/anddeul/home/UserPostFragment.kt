package com.umc.anddeul.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.R
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentUserPostBinding
import com.umc.anddeul.home.model.EmojiDTO
import com.umc.anddeul.home.model.EmojiRequest
import com.umc.anddeul.home.model.EmojiUiModel
import com.umc.anddeul.home.model.OnePostDTO
import com.umc.anddeul.home.network.EmojiInterface
import com.umc.anddeul.home.network.OnePostInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class UserPostFragment : Fragment() {
    lateinit var binding: FragmentUserPostBinding
    var token: String? = null
    lateinit var retrofitBearer: Retrofit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserPostBinding.inflate(inflater, container, false)

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

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

        val onePostService = retrofitBearer.create(OnePostInterface::class.java)

        onePostService.onePost(postId).enqueue(object : Callback<OnePostDTO> {
            override fun onResponse(call: Call<OnePostDTO>, response: Response<OnePostDTO>) {
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
                Log.e("onePostService", "Failure message: ${t.message}")
            }
        })
    }

    fun showEmojiPopup(postId : Int) {
        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        binding.userPostEmojiLinear.startAnimation(slideUpAnimation)
        binding.userPostEmojiLinear.visibility = View.VISIBLE

        binding.userPostEmojiHappy.setOnClickListener{
            selectEmoji(postId, "happy_emj")
        }
        binding.userPostEmojiLaugh.setOnClickListener {
            selectEmoji(postId, "laugh_emj")
        }
        binding.userPostEmojiSad.setOnClickListener {
            selectEmoji(postId, "sad_emj")
        }
    }

    fun selectEmoji(postId: Int, emojiType: String) {
        // 사라지는 애니메이션
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        val emojiService = retrofitBearer.create(EmojiInterface::class.java)
        val emojiRequest = EmojiRequest(emojiType)

        emojiService.getEmoji(postId, emojiRequest).enqueue(object : Callback<EmojiDTO> {
            override fun onResponse(call: Call<EmojiDTO>, response: Response<EmojiDTO>) {
                Log.e("emojiService", "선택한 게시글 id : $postId")
                Log.e("emojiService", "onResponse code : ${response.code()}")
                Log.e("emojiService", "${response.body()}")

                val emojiResponse = response.body()?.result

                if (response.isSuccessful) {
                    binding.userPostEmojiLinear.startAnimation(fadeOutAnimation)
                    binding.userPostEmojiLinear.visibility = View.GONE

                    val emojis = emojiResponse?.emojis

                    val emojiList : List<EmojiUiModel> = listOf(
                        emojis!!.happy,
                        emojis!!.laugh,
                        emojis!!.sad
                    ).mapIndexed { index, emoji ->
                        val type = when (index) {
                            0 -> "happy"
                            1 -> "laugh"
                            else -> "sad"
                        }
                        EmojiUiModel(
                            type = type,
                            selected = emoji.selected,
                            count = emoji.count
                        )
                    }.filter { it.count != 0 }

                    val emojiRVAdapter = EmojiRVAdpater(requireContext(), emojiList)
                    binding.userPostEmojiRv.adapter = emojiRVAdapter
                    binding.userPostEmojiRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }
            }

            override fun onFailure(call: Call<EmojiDTO>, t: Throwable) {
                Log.e("emojiService", "onFailure")
                Log.e("emojiService", "Failure message: ${t.message}")
            }
        })
    }
}