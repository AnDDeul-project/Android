package com.umc.anddeul.checklist

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.MainActivity
import com.umc.anddeul.databinding.ItemChecklistBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class ChecklistRVAdapter(private val checklist : ArrayList<Checklist>) : RecyclerView.Adapter<ChecklistRVAdapter.ViewHolder>() {
    val GALLERY_REQUEST_CODE = 405
    val REQUEST_IMAGE_CAPTURE = 200

    override fun getItemCount(): Int {
        return checklist.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ChecklistRVAdapter.ViewHolder {
        val binding: ItemChecklistBinding = ItemChecklistBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistRVAdapter.ViewHolder, position: Int) {

        //체크리스트 리사이클러뷰 연결
        holder.bind(checklist[position])

        //갤러리 앱 연동 함수
        holder.binding.checkliBtnCamera.setOnClickListener {
            Log.d("카메라", "클릭")
            //카메라 앱 실행
            val activity = holder.itemView.context as? MainActivity
            activity?.let {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                it.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

                if (takePictureIntent.resolveActivity(holder.itemView.context.packageManager) != null) {
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        null
                    }
                    if (photoFile != null) {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            it,
                            "your.fileprovider.authority",
                            photoFile
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        it.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }

        //할 일 체크하는 함수
        holder.binding.checkliBtnChecking.setOnClickListener {
            holder.binding.checkliBtnChecking.visibility = View.INVISIBLE
            holder.binding.checkliBtnChecked.visibility = View.VISIBLE
            holder.binding.checkliTvWriter.setTextColor(Color.parseColor("#BFBFBF"))
            holder.binding.checkliTvChecklist.setTextColor(Color.parseColor("#BFBFBF"))
        }
    }

    inner class ViewHolder(val binding: ItemChecklistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(checklist : Checklist) {
            binding.checkliTvChecklist.text = checklist.contents
            binding.checkliTvWriter.text = checklist.writer + "님이 남기셨습니다."
            if (binding.checkliIvPhoto != null) {
                binding.checkliIvPhoto.visibility = View.VISIBLE
            }
        }
    }

    lateinit var currentPhotoPath: String   // 현재 이미지 파일의 경로 저장
    var currentPhotoFileName: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val file = File("${storageDir?.path}/${timeStamp}.jpg")

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


//    private fun setPic() {
//        Glide.with(this)
//            .load(File(currentPhotoPath))
//            .into(binding.ivAddClothes)
//    }

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