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
            Log.d("갤러리", "클릭")
            val activity = holder.itemView.context as? MainActivity
            activity?.let {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                it.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
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
}