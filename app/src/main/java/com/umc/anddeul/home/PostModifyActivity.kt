package com.umc.anddeul.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.ActivityPostModifyBinding
import com.umc.anddeul.home.model.ModifyRequest
import com.umc.anddeul.home.model.PostModifyDTO
import com.umc.anddeul.home.network.PostModifyInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class PostModifyActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostModifyBinding
    var token: String? = null
    lateinit var retrofitBearer: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

        val selectedImages = intent.getStringArrayListExtra("selectedImages")
        val postIdx: Int = intent.getIntExtra("postId", -1)
        val postContent: String? = intent.getStringExtra("postContent")

        // 사진 ViewPager2에 띄우기
        val selectedVPAdapter = PostVPAdapter(selectedImages?.toList() ?: emptyList())
        binding.postModifySelectedVp.adapter = selectedVPAdapter
        binding.postModifySelectedVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // 원래 게시글 내용 담기
        binding.postModifyEdit.setText(postContent)

        // 게시글 수정 버튼 클릭 시 수정 api 호출
        binding.postModifyBtn.setOnClickListener {
            modify(postIdx)
        }

        setToolbar()
    }

    fun setToolbar() {
        binding.postModifyToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                finish()
            }
        }
    }

    fun modify(postIdx: Int) {
        // 수정한 게시글 내용 담기
        val modifyContent = binding.postModifyEdit.text.toString()
        val modifyRequest = ModifyRequest(modifyContent)

        val modifyService = retrofitBearer.create(PostModifyInterface::class.java)

        modifyService.modifyPost(postIdx, modifyRequest).enqueue(object : Callback<PostModifyDTO> {
            override fun onResponse(call: Call<PostModifyDTO>, response: Response<PostModifyDTO>) {
                Log.e("modifyService", "${response.code()}")
                Log.e("modifyService", "${response.body()}")

                if (response.isSuccessful) {
                    Intent(this@PostModifyActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    }.also {
                        startActivity(it)
                    }
                    finishAffinity()
                } else {
                    Log.e("modifyService", "게시글 수정 실패")
                }
            }

            override fun onFailure(call: Call<PostModifyDTO>, t: Throwable) {
                Log.e("modifyService", "Failure message: ${t.message}")
            }
        })
    }
}