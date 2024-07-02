package com.umc.anddeul.pot

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentGardenBinding
import com.umc.anddeul.home.LoadImage
import com.umc.anddeul.pot.model.Flower
import com.umc.anddeul.pot.model.FlowerRoot
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
    var season = "spring"
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

                    flower.let {
                        //이미지뷰에 변경
                        val loadFlower = LoadImage(binding.gardenIvFlower1)
                        loadFlower.execute(it?.img)
                    }
                }
            }

            override fun onFailure(call: Call<FlowerRoot>, t: Throwable) {
                Log.d("Flower CurrentPotService Fail", "flowerCall: ${t.message}")
            }
        })

        return binding.root
    }
}