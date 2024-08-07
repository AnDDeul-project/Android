package com.umc.anddeul.home

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.common.toast.AnddeulNoLogoToast
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.ActivityPostWriteBinding
import com.umc.anddeul.ext.enqueueWithLogoutOnUnauthorized
import com.umc.anddeul.home.adapter.SelectedVPAdapter
import com.umc.anddeul.home.model.BoardResponse
import com.umc.anddeul.home.network.BoardInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File

@Suppress("DEPRECATION")
class PostWriteActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostWriteBinding
    lateinit var selectedVPAdapter: SelectedVPAdapter
    var token: String? = null
    lateinit var retrofitBearer: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

        settingBtn()
        settingImages()

        binding.root.post{
            AnddeulNoLogoToast.createNoLogoToast(this@PostWriteActivity, "사진을 길게 누르고 움직여 순서를 조정해보세요!").show()
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

    fun settingBtn() {
        binding.uploadWriteBackIv.setOnClickListener {
            // 현재 Activity를 종료
            finish()
        }

        binding.uploadWriteBtn.setOnClickListener {
            // 서버에 데이터 전송
            boardPost()
        }
    }

    fun settingImages() {
        // 이미지 URI 목록 받아오기
        val selectedImagesUri: ArrayList<Uri> =
            intent.getParcelableArrayListExtra("selectedImages")!!

        selectedVPAdapter = SelectedVPAdapter(selectedImagesUri)
        val viewPager = binding.uploadWriteSelectedVp.apply {
            adapter = selectedVPAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
        }

        // 받아온 이미지 URI 목록을 이용하여 이미지를 나열
        for (imageUri in selectedImagesUri) {
            selectedVPAdapter.addImage(imageUri)
        }

        ItemTouchHelper(
            DragAndDropHelper(listener = object : DragAndDropHelper.OnItemMovedListener {
                override fun onItemMoved(fromPosition: Int, toPosition: Int) {
                    selectedVPAdapter.swapItems(fromPosition, toPosition)
                }

                override fun onDraggingStarted() {
                    viewPager.setSemanticPadding(
                        horizontal = R.dimen.padding_post_pager_horizontal_dragging,
                        vertical = R.dimen.padding_post_pager_vertical_dragging
                    )
                }

                override fun onDraggingFinished() {
                    viewPager.setSemanticPadding(
                        horizontal = R.dimen.padding_post_pager_horizontal_idle,
                        vertical = R.dimen.padding_post_pager_vertical_idle
                    )
                }
            })
        ).attachToRecyclerView(viewPager.getChildAt(0) as RecyclerView)
    }

    fun boardPost() {
        val boardService = retrofitBearer.create(BoardInterface::class.java)

        fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }

        // 이미지 파일들을 MultipartBody.Part로 변환
        val imageParts: List<MultipartBody.Part> = selectedVPAdapter.getImages().map { uri ->
            val file = getFileFromUri(this, uri)
            val bitmap = BitmapFactory.decodeFile(file.path)
            val compressedImage = compressBitmap(bitmap, 30) // 품질을 조절하여 압축 (예: 80)
            val requestImage = RequestBody.create("image/jpeg".toMediaTypeOrNull(), compressedImage)
            MultipartBody.Part.createFormData("image", file.name, requestImage)
        }

        val content = binding.uploadWriteEdit.text.toString()
        val contentRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), content)

        Log.e("boardService", "${contentRequestBody}, ${imageParts}")

        boardService.homeBoard(contentRequestBody, imageParts)
            .enqueueWithLogoutOnUnauthorized(this@PostWriteActivity, object : Callback<BoardResponse> {
                override fun onResponse(
                    call: Call<BoardResponse>,
                    response: Response<BoardResponse>
                ) {
                    Log.e("boardService", "${response.code()}")
                    Log.e("boardService", "${response.body()}")

                    if (response.isSuccessful) {
                        Log.e("boardService", "게시글 업로드 성공")

                        // 홈 화면으로 이동하기
                        Intent(this@PostWriteActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        }.also {
                            startActivity(it)
                        }
                        finishAffinity()

                    } else {
                        if (response.code() == 400) {
                            AnddeulErrorToast.createToast(this@PostWriteActivity, "내용을 입력해 주세요")
                                .show()
                            Log.e("boardService", "게시글 업로드 실패")
                        }

                    }
                }

                override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                    AnddeulErrorToast.createToast(this@PostWriteActivity, "서버 연결이 불안정합니다").show()
                    Log.e("boardService", "Failure message: ${t.message}")
                }
            })
    }

    private fun ViewPager2.setSemanticPadding(
        @DimenRes horizontal: Int,
        @DimenRes vertical: Int
    ) {
        val horizontalPx = resources.getDimensionPixelOffset(horizontal)
        val verticalPx = resources.getDimensionPixelOffset(vertical)
        setPadding(horizontalPx, verticalPx, horizontalPx, verticalPx)
    }
}