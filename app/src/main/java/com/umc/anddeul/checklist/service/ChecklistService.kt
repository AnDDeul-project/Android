package com.umc.anddeul.checklist.service

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.umc.anddeul.checklist.ChecklistRVAdapter
import com.umc.anddeul.checklist.model.CheckImg
import com.umc.anddeul.checklist.model.CheckImgRoot
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.checklist.model.CompleteCheck
import com.umc.anddeul.checklist.model.CompleteRoot
import com.umc.anddeul.checklist.model.Root
import com.umc.anddeul.checklist.network.ChecklistInterface
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.common.toast.AnddeulToast
import com.umc.anddeul.start.StartActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ChecklistService(context : Context) {
    private val checklistRVAdapter: ChecklistRVAdapter

    init {
        checklistRVAdapter = ChecklistRVAdapter(context) {
        }
    }

    val contextService = context

    val retrofit = RetrofitManager.getRetrofitInstance()
    val service = retrofit.create(ChecklistInterface::class.java)

    val spfMyId = context.getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
    val myId = spfMyId.getString("myId", "")

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

    fun imgApi(checkId: Int, uri: Uri) {
        val selectedImg = getFileFromUri(contextService, uri)
        val imageFileRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), selectedImg)
        val imagePart = MultipartBody.Part.createFormData("image", selectedImg.name, imageFileRequestBody)
        val checkidRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), checkId.toString())

        val imgCall : Call<CheckImgRoot> = service.imgPic(
            imagePart,
            checkidRequestBody
        )

        imgCall.enqueue(object : Callback<CheckImgRoot> {
            override fun onResponse(call: Call<CheckImgRoot>, response: Response<CheckImgRoot>) {
                Log.d("Checklist ImgService code", "${response.code()}")
                Log.d("Checklist ImgService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : CheckImgRoot? = response.body()
                    val checkImg : CheckImg? = root?.check

                    if (root?.isSuccess == true) {
                        checkImg?.let {
                            val due_date = checkImg.due_date
                            Log.d("Checklist", "${checkImg}")
                            readApi(due_date, myId!!)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CheckImgRoot>, t: Throwable) {
                Log.d("Checklist ImgService Fail", "imgCall: ${t.message}")
                AnddeulErrorToast.createToast(contextService, "서버 연결이 불안정합니다").show()
            }
        })
    }

    fun completeApi(checklist: Checklist) {
        val completeCall : Call<CompleteRoot> = service.complete(
            checklist.check_idx
        )

        completeCall.enqueue(object : Callback<CompleteRoot> {
            override fun onResponse(call: Call<CompleteRoot>, response: Response<CompleteRoot>) {
                Log.d("Checklist CompleteService code", "${response.code()}")
                Log.d("Checklist CompleteService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : CompleteRoot? = response.body()
                    val check : CompleteCheck? = root?.check

                    if (root?.isSuccess == true) {
                        check.let {
                            readApi(check?.due_date!!, myId!!)
                        }
                    }

                    if (response.code() == 500) {
                        AnddeulErrorToast.createToast(contextService, "인터넷 연결이 불안정합니다").show()
                    }

                    if(response.code() == 401) {
                        val startIntent = Intent(contextService, StartActivity::class.java)
                        contextService!!.startActivity(startIntent)
                    }
                }
            }
            override fun onFailure(call: Call<CompleteRoot>, t: Throwable) {
                Log.d("Checklist CompleteService Fail", "completeCall : ${t.message}")
                AnddeulErrorToast.createToast(contextService, "서버 연결이 불안정합니다").show()
            }
        })
    }

    fun readApi(due_date : String, myId : String) {
        val readCall : Call<Root> = service.getChecklist(
            myId!!,
            false,
            due_date
        )

        readCall.enqueue(object : Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                val checklist = ArrayList<Checklist>()
                checklistRVAdapter.setChecklistData(checklist)
                checklistRVAdapter.notifyDataSetChanged()

                Log.d("Checklist ReadService code", "${response.code()}")
                Log.d("Checklist ReadService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : Root? = response.body()
                    val result : List<Checklist>? = root?.checklist

                    result?.let {
                        checklistRVAdapter.setChecklistData(it)
                        checklistRVAdapter.notifyDataSetChanged()
                    }
                }
                if (response.code() == 500) {
                    AnddeulErrorToast.createToast(contextService, "인터넷 연결이 불안정합니다").show()
                }

                if (response.code() == 451) {
                    AnddeulToast.createToast(contextService, "해당 날짜에 만들어진 체크리스트가 없습니다").show()
                }

                if(response.code() == 401) {
                    val startIntent = Intent(contextService, StartActivity::class.java)
                    contextService!!.startActivity(startIntent)
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                Log.d("Checklist ReadService Fail", "readCall: ${t.message}")
                AnddeulErrorToast.createToast(contextService, "서버 연결이 불안정합니다").show()
            }
        })
    }

}