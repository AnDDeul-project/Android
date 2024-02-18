package com.umc.anddeul.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.umc.anddeul.R
import com.umc.anddeul.databinding.ActivityPostUploadBinding

class PostUploadActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPostUploadBinding
    private val selectedImages: ArrayList<Uri> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadGalleryToolbar.apply {
            inflateMenu(R.menu.post_gallery_menu)
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menuItemConfirm -> {
                        // 다음 페이지로 선택한 이미지들 넘겨주기
                        // 글 작성 페이지로 이동
                        val intent = Intent(this@PostUploadActivity, PostWriteActivity::class.java)

                        // 선택된 이미지들을 번들에 추가
                        val selectedImagesUri: MutableList<Uri> = ArrayList()
                        for (selectedImageUri in selectedImages) {
                            selectedImagesUri.add(selectedImageUri)
                        }
                        intent.putParcelableArrayListExtra("selectedImages", ArrayList(selectedImagesUri))

                        // 다음 액티비티 시작
                        startActivity(intent)
                    }
                }
                true
            }


            val drawable = resources.getDrawable(R.drawable.ic_close)
            val resizedDrawable = BitmapDrawable(resources, Bitmap.createScaledBitmap((drawable as BitmapDrawable).bitmap, 40, 40, true))

            setNavigationIcon(resizedDrawable)

            setNavigationOnClickListener {
                // 현재 Activity를 종료
                finish()
            }
        }

        binding.uploadGalleryRv.layoutManager = GridLayoutManager(this, 3)
        val galleryAdapter = GalleryAdapter(this, galleryImages = getGalleryImages(), selectedImages = selectedImages) { selectedImageUri ->
            binding.uploadSelectedIv.setImageURI(selectedImageUri)
        }

        binding.uploadGalleryRv.adapter = galleryAdapter

        binding.uploadGalleryMultiImgBt.setOnClickListener {
// 여러 장 선택 버튼을 눌렀을 때 처리
            binding.uploadGalleryRv.adapter?.let { adapter ->
                if (adapter is GalleryAdapter) {
                    adapter.toggleCheckBoxVisibility()
                }
            }
        }
    }


    private fun getGalleryImages(): List<Uri> {
        // 갤러리 이미지를 저장할 리스트
        val galleryImages = mutableListOf<Uri>()

        // 쿼리 시 사용할 컬럼들 정의
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        // 이미지에 대한 쿼리 수행
        val cursor = this.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val columnIndexId = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            // cursor를 이용해 데이터를 이동하면서 이미지 정보를 가져옴
            while (it.moveToNext()) {
                val imageId = it.getLong(columnIndexId)
                val imageUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId.toString()
                )
                // 생성한 이미지 URI를 리스트에 추가
                galleryImages.add(imageUri)
            }
        }
        // 갤러리 이미지 URI를 담은 리스트 반환
        return galleryImages
    }

}