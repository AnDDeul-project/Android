package com.umc.anddeul.checklist

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.checklist.service.ChecklistService
import com.umc.anddeul.common.toast.AnddeulToast
import com.umc.anddeul.databinding.ItemChecklistBinding
import java.io.File


class ChecklistRVAdapter(private val context : Context, private val onItemClicked: (Int) -> Unit) : RecyclerView.Adapter<ChecklistRVAdapter.ViewHolder>() {
    var checklist: List<Checklist>? = null
    lateinit var file : File

    override fun getItemCount(): Int {
        return checklist?.size ?: 0
    }

    fun setChecklistData(list: List<Checklist>) {
        Log.d("Checklist", "setChecklistData 진입")
        Log.d("Checklist", "${list}")
        checklist = list
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ChecklistRVAdapter.ViewHolder {
        val binding: ItemChecklistBinding = ItemChecklistBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistRVAdapter.ViewHolder, position: Int) {

        //체크리스트 리사이클러뷰 연결
        checklist?.getOrNull(position)?.let { item ->
            holder.bind(item)
        }

        holder.binding.checkliBtnChecking.setOnClickListener {
            ChecklistService(context).completeApi(checklist!!.get(position), position)
            checking(holder.binding)
        }

        holder.binding.checkliBtnChecked.setOnClickListener {
            ChecklistService(context).completeApi(checklist!!.get(position), position)
            checked(holder.binding)
        }
    }

    fun checking(binding : ItemChecklistBinding) {
        binding.checkliBtnChecking.visibility = View.INVISIBLE
        binding.checkliBtnChecked.visibility = View.VISIBLE
        binding.checkliTvWriter.setTextColor(Color.parseColor("#BFBFBF"))
        binding.checkliTvChecklist.setTextColor(Color.parseColor("#BFBFBF"))
    }

    fun checked(binding : ItemChecklistBinding) {
        binding.checkliBtnChecking.visibility = View.VISIBLE
        binding.checkliBtnChecked.visibility = View.INVISIBLE
        binding.checkliTvWriter.setTextColor(Color.parseColor("#261710"))
        binding.checkliTvChecklist.setTextColor(Color.parseColor("#261710"))
    }

    inner class ViewHolder(val binding: ItemChecklistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(checklist: Checklist) {
            binding.checkliTvChecklist.text = checklist?.content
            binding.checkliTvWriter.text = checklist?.sender + "님이 남기셨습니다."
            if (checklist.complete == 1) {
                binding.checkliBtnChecking.visibility = View.INVISIBLE
                binding.checkliBtnChecked.visibility = View.VISIBLE
                binding.checkliTvWriter.setTextColor(Color.parseColor("#BFBFBF"))
                binding.checkliTvChecklist.setTextColor(Color.parseColor("#BFBFBF"))
                if (checklist.picture != null) {
                    binding.checkliIvPhoto.visibility = View.VISIBLE
                    val loadCheckImg = LoadCheckImg(binding.checkliIvPhoto)
                    loadCheckImg.execute(checklist.picture)
                } else {
                    binding.checkliIvPhoto.visibility = View.GONE
                }
            }
            else {
                binding.checkliBtnChecking.visibility = View.VISIBLE
                binding.checkliBtnChecked.visibility = View.INVISIBLE
                binding.checkliTvWriter.setTextColor(Color.parseColor("#261710"))
                binding.checkliTvChecklist.setTextColor(Color.parseColor("#261710"))
                binding.checkliIvPhoto.visibility = View.GONE
            }

            //카메라 앱 연동 함수
            binding.checkliBtnCamera.setOnClickListener {
                if (checklist.complete == 0) {
                    AnddeulToast.createToast(context, "체크리스트 달성 전에는 인증샷을 추가할 수 없습니다.")?.show()
                }
                else {
                    onItemClicked(checklist.check_idx)
                }
            }
        }
    }
}
