package com.umc.anddeul.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.R
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentMyPostBinding
import com.umc.anddeul.home.DeleteDialog
import com.umc.anddeul.home.adapter.EmojiRVAdpater
import com.umc.anddeul.home.LoadProfileImage
import com.umc.anddeul.home.PostModifyActivity
import com.umc.anddeul.home.adapter.PostVPAdapter
import com.umc.anddeul.home.model.EmojiDTO
import com.umc.anddeul.home.model.EmojiRequest
import com.umc.anddeul.home.model.EmojiUiModel
import com.umc.anddeul.home.model.OnePostDTO
import com.umc.anddeul.home.model.OnePostData
import com.umc.anddeul.home.network.EmojiInterface
import com.umc.anddeul.home.network.OnePostInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MyPostFragment : Fragment() {
    lateinit var binding: FragmentMyPostBinding
    var token: String? = null
    lateinit var retrofitBearer: Retrofit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

        setToolbar()
        loadPost()

        return binding.root
    }

    fun setToolbar() {
        binding.myPostBackIv.setOnClickListener {
            // UserProfileFragment로 이동
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }

    fun loadPost() {
        val postIdxJson = arguments?.getInt("postIdx")
        val postId: Int = postIdxJson ?: 0

        val onePostService = retrofitBearer.create(OnePostInterface::class.java)

        onePostService.onePost(postId).enqueue(object : Callback<OnePostDTO> {
            override fun onResponse(call: Call<OnePostDTO>, response: Response<OnePostDTO>) {
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

                        val emojis = postData.emojis

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
                        binding.myPostEmojiRv.adapter = emojiRVAdapter
                        binding.myPostEmojiRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                    }
                } else {
                    context?.let { AnddeulErrorToast.createToast(it, "게시글을 찾을 수 없습니다").show() }
                }
            }

            override fun onFailure(call: Call<OnePostDTO>, t: Throwable) {
                context?.let { AnddeulErrorToast.createToast(it, "서버 연결이 불안정합니다").show() }
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

        val emojiService = retrofitBearer.create(EmojiInterface::class.java)
        val emojiRequest = EmojiRequest(emojiType)

        emojiService.getEmoji(postId, emojiRequest).enqueue(object : Callback<EmojiDTO> {
            override fun onResponse(call: Call<EmojiDTO>, response: Response<EmojiDTO>) {
                Log.e("emojiService", "onResponse code : ${response.code()}")
                Log.e("emojiService", "${response.body()}")

                val emojiResponse = response.body()?.result

                if (response.isSuccessful) {
                    binding.myPostEmojiLinear.startAnimation(fadeOutAnimation)
                    binding.myPostEmojiLinear.visibility = View.GONE

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
                    binding.myPostEmojiRv.adapter = emojiRVAdapter
                    binding.myPostEmojiRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                }
            }

            override fun onFailure(call: Call<EmojiDTO>, t: Throwable) {
                context?.let { AnddeulErrorToast.createToast(it, "서버 연결이 불안정합니다").show() }
                Log.e("emojiService", "Failure message: ${t.message}")
            }
        })
    }
}