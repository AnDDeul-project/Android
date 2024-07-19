package com.umc.anddeul.pot

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.opengl.Visibility
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
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentGardenBinding
import com.umc.anddeul.home.LoadImage
import com.umc.anddeul.pot.model.Flower
import com.umc.anddeul.pot.model.FlowerRoot
import com.umc.anddeul.pot.model.Flowers
import com.umc.anddeul.pot.model.Garden
import com.umc.anddeul.pot.model.GardenRoot
import com.umc.anddeul.pot.model.Point
import com.umc.anddeul.pot.model.PointRoot
import com.umc.anddeul.pot.network.PotInterface
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GardenFragment : Fragment() {
    lateinit var binding : FragmentGardenBinding
    var flowerId : Int = 0
    var season = "봄"
    var token : String? = null
    lateinit var retrofit: Retrofit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGardenBinding.inflate(inflater, container, false)
        token = TokenManager.getToken()
        retrofit = RetrofitManager.getRetrofitInstance()

        val service = retrofit.create(PotInterface::class.java)

        binding.gardenImgBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
        }

        when (season) {
            "봄" -> {
                binding.gardenBtnThemeBefore.visibility = View.GONE
                binding.gardenBtnThemeNext.visibility = View.VISIBLE
            }
            "여름" -> {
                binding.gardenBtnThemeBefore.visibility = View.VISIBLE
                binding.gardenBtnThemeNext.visibility = View.GONE
            }
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
                        binding.gardenTvHeartPoint.text = it?.point.toString()
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
                    flowerId = flower!!.idx
                }
            }

            override fun onFailure(call: Call<FlowerRoot>, t: Throwable) {
                Log.d("Flower CurrentPotService Fail", "flowerCall: ${t.message}")
            }
        })

        val gardenCall : Call<GardenRoot> = service.getGarden(flowerId)
        gardenCall.enqueue(object: Callback<GardenRoot> {
            override fun onResponse(call: Call<GardenRoot>, response: Response<GardenRoot>) {
                Log.d("Flower GardenService code", "${response.code()}")
                Log.d("Flower GardenService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : GardenRoot? = response.body()
                    val garden : Garden? = root?.garden
                    season = garden!!.theme
                    val flowers : List<Flowers>? = garden?.flowers

                    flowers.let {
                        var i = 0
                        while (i < flowers!!.size) {
                            when (i) {
                                0 -> binding.gardenIvFlower1.loadImageFromUrl(flowers?.get(i)?.img_5!!)
                                1 -> binding.gardenIvFlower2.loadImageFromUrl(flowers?.get(i)?.img_5!!)
                            }
                            i++
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GardenRoot>, t: Throwable) {
                Log.d("Flower GardenService Fail", "flowerCall: ${t.message}")
            }
        })
        return binding.root
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