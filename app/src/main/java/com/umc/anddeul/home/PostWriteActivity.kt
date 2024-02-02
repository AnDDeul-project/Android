package com.umc.anddeul.home

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.ActivityPostWriteBinding
import com.umc.anddeul.home.model.BoardResponse
import com.umc.anddeul.home.network.BoardInterface
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@Suppress("DEPRECATION")
class PostWriteActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostWriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadWriteToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                // 현재 Activity를 종료
                finish()
            }
        }

        // 이미지 URI 목록 받아오기
        val selectedImagesUri : ArrayList<Uri> = intent.getParcelableArrayListExtra("selectedImages")!!

        val selectedVPAdapter = SelectedVPAdapter(selectedImagesUri)
        binding.uploadWriteSelectedVp.adapter = selectedVPAdapter
        binding.uploadWriteSelectedVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // 받아온 이미지 URI 목록을 이용하여 이미지를 나열
        val copiedImagesUri: List<Uri> = ArrayList(selectedImagesUri)
        for (imageUri in copiedImagesUri) {
            selectedVPAdapter.addImage(imageUri)
        }


        binding.uploadWriteBtn.setOnClickListener {
            // 서버에 데이터 전송
            boardPost()

            // homeFragment로 돌아가기
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }.also {
                startActivity(it)
            }
            finishAffinity()
        }

    }

    private fun getFileFromUri(context: Context, uri: Uri): File {
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            val filePath = it.getString(columnIndex)
            return File(filePath)
        }

        throw IllegalArgumentException("Invalid URI")
    }

    fun boardPost() {
        val spf: SharedPreferences = getSharedPreferences("myToken", Context.MODE_PRIVATE)
        // val token = spf.getString("jwtToken", "")
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzA0MTMzMDkzIl0sImlhdCI6MTcwNjY4MzkxMH0.ncVxzwxBVaiMegGD0VU5pI5i9GJjhrU8kUIYtQrSLSg"

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

        val boardService = retrofitBearer.create(BoardInterface::class.java)

        // 이미지 URI 목록 받아오기
        val selectedImagesUri : ArrayList<Uri> = intent.getParcelableArrayListExtra("selectedImages")!!
        // 받아온 이미지 URI 목록을 이용하여 이미지를 나열
        val copiedImagesUri: List<Uri> = ArrayList(selectedImagesUri)

        // 이미지 파일들을 MultipartBody.Part로 변환
        val imageParts: List<MultipartBody.Part> = copiedImagesUri.map { uri ->
            val file = getFileFromUri(this, uri)
            val requestImage = file.asRequestBody("image/".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestImage)
        }

        val content = binding.uploadWriteEdit.text.toString()
        val contentRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), content)

        Log.e("boardService", "${contentRequestBody}, ${imageParts}")

        boardService.homeBoard(contentRequestBody, imageParts).enqueue(object : Callback<BoardResponse> {
            override fun onResponse(call: Call<BoardResponse>, response: Response<BoardResponse>) {
                Log.e("boardService", "onResponse")
                Log.e("boardService", "${response.code()}")
                Log.e("boardService", "${response.body()}")

                if (response.isSuccessful) {
                    Log.e("boardService", "isSuccessful")
                }
            }

            override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                Log.e("boardService", "onFailure")
                Log.e("boardService", "Failure message: ${t.message}")

            }

        })

    }


}