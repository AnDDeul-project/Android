package com.umc.anddeul.checklist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umc.anddeul.MainActivity
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.checklist.network.ChecklistInterface
import com.umc.anddeul.checklist.service.ChecklistService
import com.umc.anddeul.databinding.ItemChecklistBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class ChecklistRVAdapter(private val context : Context) : RecyclerView.Adapter<ChecklistRVAdapter.ViewHolder>() {
    val CAMERA_REQUEST_CODE = 405
    val REQUEST_IMAGE_CAPTURE = 200
    var checklist: List<Checklist>? = null
//    val checklistService = ChecklistService(context)

    override fun getItemCount(): Int {
        return checklist?.size ?: 0
    }

    fun setChecklistData(checklist: List<Checklist>) {
        this.checklist = checklist
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ChecklistRVAdapter.ViewHolder {
        val binding: ItemChecklistBinding = ItemChecklistBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistRVAdapter.ViewHolder, position: Int) {

        //체크리스트 리사이클러뷰 연결
        holder.bind(checklist!!.get(position))
//        holder.bind(checklist[position])

        //카메라 앱 연동 함수
        holder.binding.checkliBtnCamera.setOnClickListener {
            Log.d("카메라", "클릭")
            //카메라 앱 실행
//            val activity = holder.itemView.context as? MainActivity
//            activity?.let {
//                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////                it.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//
//                if (takePictureIntent.resolveActivity(holder.itemView.context.packageManager) != null) {
//                    val photoFile: File? = try {
//                        createImageFile()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                        null
//                    }
//                    if (photoFile != null) {
//                        val photoURI: Uri = FileProvider.getUriForFile(
//                            it,
//                            "your.fileprovider.authority",
//                            photoFile
//                        )
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                        it.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                    }
//                }
//            }
            checkCameraPermission()
//            checklistService.completeApi()

            //체크
            holder.binding.checkliBtnChecking.visibility = View.INVISIBLE
            holder.binding.checkliBtnChecked.visibility = View.VISIBLE
            holder.binding.checkliTvWriter.setTextColor(Color.parseColor("#BFBFBF"))
            holder.binding.checkliTvChecklist.setTextColor(Color.parseColor("#BFBFBF"))


        }

    }

    inner class ViewHolder(val binding: ItemChecklistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(checklist : Checklist) {
            binding.checkliTvChecklist.text = checklist?.content
            binding.checkliTvWriter.text = checklist?.sender + "님이 남기셨습니다."
            if (binding.checkliIvPhoto != null) {
                binding.checkliIvPhoto.visibility = View.VISIBLE
//                Glide.with(this@ChecklistRVAdapter)
//                    .load(File(checklist.picture))
//                    .into(binding.checkliIvPhoto)
            } else {
                binding.checkliIvPhoto.visibility = View.GONE
            }
            if (checklist.complete == 1) {
                binding.checkliBtnChecking.visibility = View.INVISIBLE
                binding.checkliBtnChecked.visibility = View.VISIBLE
                binding.checkliTvWriter.setTextColor(Color.parseColor("#BFBFBF"))
                binding.checkliTvChecklist.setTextColor(Color.parseColor("#BFBFBF"))
            }
        }
    }

    private fun checkCameraPermission() {
        Log.d("카메라", "권한 함수")
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없으면 권한 요청
            Log.d("카메라", "권한 요청")
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
            Log.d("카메라", "권한 요청 성공")
        } else {
            // 권한이 이미 허용되어 있으면 카메라 열기
            Log.d("카메라", "권한 허용 되어있음")
            openCamera()
        }
    }

    private fun openCamera() {
        Log.d("카메라", "앱 열기")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context.packageManager) != null) {
            // 파일을 저장할 디렉토리 생성
            Log.d("카메라", "인텐트")
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            Log.d("카메라", "file : ${photoFile}")
            // 파일이 생성되었다면 카메라 앱에 전달
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    context,
                    "com.umc.anddeul.fileprovider",
                    it
                )
                Log.d("카메라", "file : ${photoURI}")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                (context as Activity).startActivityForResult(intent, CAMERA_REQUEST_CODE)
                Log.d("카메라", "startActivityForResult")
            }
        }
    }

    lateinit var currentPhotoPath: String   // 현재 이미지 파일의 경로 저장
    var currentPhotoFileName: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        Log.d("카메라", "시간 ${timeStamp}")
        val storageDir: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val file = File("${storageDir?.path}/${timeStamp}.jpg")
        Log.d("카메라", "file ${file}")
        /*Temp File 생성*/
//        val file = File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        )

        currentPhotoFileName = file.name
        currentPhotoPath = file.absolutePath
        return file
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            REQUEST_IMAGE_CAPTURE -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    // 카메라 앱에서 사진 촬영 완료된 경우
//                    // 사진을 리사이클러뷰 내의 다른 이미지뷰에 설정
//                    yourOtherImageView.setImageURI(Uri.fromFile(File(currentPhotoPath)))
//                }
//            }
//        }
//    }
}