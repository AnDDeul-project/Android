package com.umc.anddeul.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.ActivityGalleryAllImageBinding

class GalleryAdapter(
    private val galleryImages: List<Uri>,
    private val selectedImages: MutableList<Uri>,
    private val onItemClick: (Uri) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_GALLERY = 1
    private var isCheckBoxVisible = false // 초기에는 체크박스가 보이지 않도록 설정


    fun toggleCheckBoxVisibility() {

        isCheckBoxVisible = !isCheckBoxVisible
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_GALLERY -> {
                val binding = ActivityGalleryAllImageBinding.inflate(inflater, parent, false)
                GalleryViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GalleryViewHolder -> {
                val galleryPosition = position
                if (galleryPosition < galleryImages.size) {
                    val imageUri = galleryImages[galleryPosition]
                    holder.bind(imageUri)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return galleryImages.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_GALLERY
    }

    inner class GalleryViewHolder(private val binding: ActivityGalleryAllImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // 체크박스 가시성을 설정
            binding.galleryCheckBox.visibility = if (isCheckBoxVisible) View.VISIBLE else View.GONE

            // 체크박스 상태 초기화
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                binding.galleryCheckBox.isChecked = selectedImages.contains(galleryImages[position])
            }

            // 체크박스 상태 변경 리스너 설정
            binding.galleryCheckBox.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val imageUri = galleryImages[position]
                    if (isChecked) {
                        selectedImages.add(imageUri)
                    } else {
                        selectedImages.remove(imageUri)
                    }
                }
            }

            // 이미지 클릭 시 체크박스 토글
            binding.galleryAllImageIv.setOnClickListener {
                toggleCheckBox()
            }
        }

        private fun toggleCheckBox() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                // 체크박스 상태를 토글
                binding.galleryCheckBox.isChecked = !binding.galleryCheckBox.isChecked
            }
        }


        fun bind(imageUri: Uri) {
            // 뷰 홀더가 재사용될 때마다 체크박스 가시성 초기화
            binding.galleryCheckBox.visibility = if (isCheckBoxVisible) View.VISIBLE else View.GONE

            // 체크박스 상태 초기화
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                binding.galleryCheckBox.isChecked = selectedImages.contains(imageUri)
            }

            // 이미지 클릭 시 체크박스 토글
            binding.galleryAllImageIv.setOnClickListener {
                toggleCheckBox()
                onItemClick.invoke(imageUri) // 이미지 클릭 시 onItemClick 호출
            }

            binding.galleryAllImageIv.setImageURI(Uri.parse(imageUri.toString()))
            binding.root.setOnClickListener { onItemClick.invoke(imageUri) }
        }
    }
}
