package com.umc.anddeul.pot

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.checklist.service.ChecklistService
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.databinding.FragmentPotBinding
import com.umc.anddeul.home.LoadImage
import com.umc.anddeul.pot.GardenFragment
import com.umc.anddeul.pot.model.ChangedImg
import com.umc.anddeul.pot.model.Flower
import com.umc.anddeul.pot.model.FlowerRoot
import com.umc.anddeul.pot.model.LoveRoot
import com.umc.anddeul.pot.model.Point
import com.umc.anddeul.pot.model.PointRoot
import com.umc.anddeul.pot.model.Result
import com.umc.anddeul.pot.network.PotInterface
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.format.DateTimeFormatter

class PotFragment : Fragment() {
    lateinit var binding: FragmentPotBinding
    var token : String? = null
    lateinit var retrofit: Retrofit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPotBinding.inflate(inflater, container, false)

        token = TokenManager.getToken()
        retrofit = RetrofitManager.getRetrofitInstance()
        val service = retrofit.create(PotInterface::class.java)

        binding.potImgGarden.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, GardenFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.potImgBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.potImgPointPlus.setOnClickListener {
            giveLove(service)
        }

        val myPointCall : Call<PointRoot> = service.getMyPoint()
        myPointCall.enqueue(object : Callback<PointRoot> {
            override fun onResponse(call: Call<PointRoot>, response: Response<PointRoot>) {
                Log.d("Flower PointService code", "${response.code()}")
                Log.d("Flower PointService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : PointRoot? = response.body()
                    val point : Point? = root?.point

                    point.let {
                        binding.potTvHeartPoint.text = it?.point.toString()
                    }
                }
            }

            override fun onFailure(call: Call<PointRoot>, t: Throwable) {
                Log.d("Flower PointService Fail", "myPointCall: ${t.message}")
            }
        })

        val flowerCall : Call<FlowerRoot> = service.getFlower()
        flowerCall.enqueue(object: Callback<FlowerRoot> {
            override fun onResponse(call: Call<FlowerRoot>, response: Response<FlowerRoot>) {
                Log.d("Flower CurrentPotService code", "${response.code()}")
                Log.d("Flower CurrentPotService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : FlowerRoot? = response.body()
                    val flower : Flower? = root?.flower

                    flower.let {
                        binding.potTvPlantsName.text = it?.name.toString()
                        //이미지뷰에 변경
                        binding.potImgPlants.loadImageFromUrl(it?.img!!)
                        binding.potImageGauge.loadImageFromUrl(it?.gauge!!)
                    }
                    if (response.code() == 500) {
                        AnddeulErrorToast.createToast(context!!, "인터넷 연결이 불안정합니다")?.show()
                    }
                }
            }

            override fun onFailure(call: Call<FlowerRoot>, t: Throwable) {
                Log.d("Flower CurrentPotService Fail", "flowerCall: ${t.message}")
                AnddeulErrorToast.createToast(context!!, "서버 연결이 불안정합니다")?.show()
            }
        })

        return binding.root
    }

    fun giveLove(service: PotInterface) {
        val loveCall: Call<LoveRoot> = service.giveLove()
        loveCall.enqueue(object : Callback<LoveRoot> {
            override fun onResponse(call: Call<LoveRoot>, response: Response<LoveRoot>) {
                Log.d("Flower GiveLoveService code", "${response.code()}")
                Log.d("Flower GiveLoveService body", "${response.body()}")

                if(response.isSuccessful) {
                    val root : LoveRoot? = response.body()
                    val result : Result? = root?.result
                    var changedImg : List<ChangedImg>? = result?.changed_img

                    result.let {
                        binding.potTvHeartPoint.text = it?.point.toString()
                    }
                    changedImg.let {
                        //꽃 사진 변경
                        binding.potImgPlants.loadImageFromUrl(it?.get(0)?.img!!)
                        //프로그레스바 이미지 변경
                        binding.potImageGauge.loadImageFromUrl(it?.get(1)?.gauge!!)
                    }
                    if (response.code() == 500) {
                        AnddeulErrorToast.createToast(context!!, "인터넷 연결이 불안정합니다")?.show()
                    }
                }
            }

            override fun onFailure(call: Call<LoveRoot>, t: Throwable) {
                Log.d("Flower GiveLoveService Fail", "loveCall: ${t.message}")
                AnddeulErrorToast.createToast(context!!, "서버 연결이 불안정합니다")?.show()
            }
        })
    }

    fun ImageView.loadImageFromUrl(imageUrl: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(context))
            }
            .build()

        val imageRequest = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(300)
            .data(imageUrl)
            .target(
                onSuccess = { result ->
                    val bitmap = (result as BitmapDrawable).bitmap
                    this.setImageBitmap(bitmap)
                },
            )
            .build()

        imageLoader.enqueue(imageRequest)
    }
}