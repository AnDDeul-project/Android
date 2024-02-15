package com.umc.anddeul.home

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.common.AnddeulToast
import com.umc.anddeul.databinding.ActivityGalleryAllImageBinding

class GalleryAdapter(
    private val context: Context,
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
                GalleryViewHolder(binding, onItemClick, context)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GalleryViewHolder -> {
                if (position < galleryImages.size) {
                    val imageUri = galleryImages[position]
                    holder.bind(
                        imageUri = imageUri,
                        position = position,
                        isCheckBoxVisible = isCheckBoxVisible,
                        selectedImages = selectedImages,
                    )
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
}

class GalleryViewHolder(
    private val binding: ActivityGalleryAllImageBinding,
    private val onItemClick: (Uri) -> Unit,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private fun toggleCheckBox(position: Int, imageUri: Uri, selectedImages: MutableList<Uri>) {
        if (position != RecyclerView.NO_POSITION) {
            // 체크박스 상태를 토글
            binding.galleryCheckBox.isChecked = !binding.galleryCheckBox.isChecked

            if (binding.galleryCheckBox.isChecked) {
                if (selectedImages.size < 10) {
                    selectedImages.add(imageUri)
                }
                else {
                    AnddeulToast.createToast(context, "사진은 최대 10장 선택 가능합니다")?.show()

                    Log.e("seletedImages", "${selectedImages.size}")
                    binding.galleryCheckBox.isChecked = false
                }

            } else {
                selectedImages.remove(imageUri)
            }
        }
    }

    fun bind(imageUri: Uri, position: Int, isCheckBoxVisible: Boolean, selectedImages: MutableList<Uri>) {
        // 뷰 홀더가 재사용될 때마다 체크박스 가시성 초기화
        binding.galleryCheckBox.visibility = if (isCheckBoxVisible) View.VISIBLE else View.GONE

        // 체크박스 상태 초기화
        if (position != RecyclerView.NO_POSITION) {
            binding.galleryCheckBox.isChecked = selectedImages.contains(imageUri)
        }

        // 이미지 클릭 시 체크박스 토글
        binding.galleryAllImageIv.setOnClickListener {
            if (isCheckBoxVisible) {
                toggleCheckBox(position, imageUri, selectedImages)
            }
            onItemClick.invoke(imageUri) // 이미지 클릭 시 onItemClick 호출
        }

        binding.galleryAllImageIv.setImageURI(Uri.parse(imageUri.toString()))
        binding.root.setOnClickListener { onItemClick.invoke(imageUri) }
    }
}