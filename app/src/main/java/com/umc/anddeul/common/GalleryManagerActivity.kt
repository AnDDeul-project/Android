package com.umc.anddeul.common

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.provider.MediaStore
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.umc.anddeul.home.PermissionDialog
import com.umc.anddeul.home.PostWriteActivity

class GalleryManagerActivity : AppCompatActivity() {
    private val pickMultipleMediaLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_UPLOAD_IMAGE)
    ) { uris ->
        // 선택한 이미지들의 URI 목록을 처리하는 콜백
        if (uris.isNotEmpty()) {
            // 선택한 이미지가 있을 경우
            val selectedImagesList = ArrayList(uris)

            startActivity(Intent(applicationContext, PostWriteActivity::class.java).apply {
                putParcelableArrayListExtra("selectedImages", selectedImagesList)
            })
        }  else {
            // 선택한 이미지가 없을 경우
        }
    }

    private val albumLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // 사진 선택을 완료한 후 돌아왔다면
        if (it.resultCode == RESULT_OK) {
            // 선택한 이미지의 경로 데이터를 관리하는 Uri 객체 리스트를 추출
            val uriclip = it.data?.clipData
            val selectedImages: List<Uri> = if (uriclip == null) {
                emptyList()
            } else {
                List(uriclip.itemCount) {index ->  uriclip.getItemAt(index).uri}
            }
            if (selectedImages.size > MAX_UPLOAD_IMAGE) {
//                Snackbar.make(
//                    binding.root,
//                    "사진 첨부는 최대 ${MAX_UPLOAD_IMAGE}장까지 가능합니다.",
//                    Snackbar.LENGTH_SHORT
//                ).show()
            }
            if (selectedImages.isNotEmpty()) {
                startActivity(Intent(applicationContext, PostWriteActivity::class.java).apply {
                    putParcelableArrayListExtra(
                        "selectedImages",
                        ArrayList(selectedImages.take(MAX_UPLOAD_IMAGE)) // take API 살펴보기
                    )
                })
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startAlbumLauncher()
        } else {
            val permissionDialog = PermissionDialog()
            permissionDialog.isCancelable = false
            permissionDialog.show(supportFragmentManager, "permission dialog")
        }
    }

    companion object {
        // 이미지 등록 가능 갯수
        const val MAX_UPLOAD_IMAGE = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 포토피커 사용 가능한 레벨인지 확인
        if (isPhotoPickerAvailable()) {
            startPhotoPicker()
        } else {
            checkPermission()
        }

    }

    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
        } else {
            false
        }
    }

    // 갤러리 접근 권한 확인 함수
    fun checkPermission() {
        val permissionReadExternal = android.Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionReadExternalGranted = ContextCompat.checkSelfPermission(
            applicationContext,
            permissionReadExternal
        ) == PackageManager.PERMISSION_GRANTED

        // 포토피커를 사용하지 못하는 버전만 권한 확인 (SDK 30 미만)
        if (permissionReadExternalGranted) {
            startAlbumLauncher()
        } else {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    @SuppressLint("IntentReset")
    fun startAlbumLauncher() {
        val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // 이미지 여러개 선택 가능
        albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        // 액티비티를 실행한다.
        albumLauncher.launch(albumIntent)
    }

    fun startPhotoPicker() {
        pickMultipleMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}