package com.umc.anddeul.postbox

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.checklist.service.ChecklistService
import com.umc.anddeul.databinding.FragmentPotBinding
import com.umc.anddeul.pot.GardenFragment
import com.umc.anddeul.pot.model.Flower
import com.umc.anddeul.pot.model.FlowerRoot
import com.umc.anddeul.pot.model.LoveRoot
import com.umc.anddeul.pot.model.Point
import com.umc.anddeul.pot.model.PointRoot
import com.umc.anddeul.pot.model.Result
import com.umc.anddeul.pot.model.ResultImg
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPotBinding.inflate(inflater, container, false)

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

        //토큰 가져오기
        val spf : SharedPreferences = context!!.getSharedPreferences("myToken", Context.MODE_PRIVATE)
        val spfMyId : SharedPreferences = context!!.getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzA0MTMzMDkzIl0sImlhdCI6MTcwNjY4MzkxMH0.ncVxzwxBVaiMegGD0VU5pI5i9GJjhrU8kUIYtQrSLSg"
        val retrofit = Retrofit.Builder()
            .baseUrl("http://umc-garden.store")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token.orEmpty())
                            .build()
                        Log.d("retrofit", "Token: " + token.orEmpty())
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()

        val service = retrofit.create(PotInterface::class.java)

        binding.potImgPointPlus.setOnClickListener {
            giveLove(service)
        }

        val myPointCall : Call<PointRoot> = service.getMyPoint()
        Log.d("포인트", "mypointCall: ${myPointCall}")
        myPointCall.enqueue(object : Callback<PointRoot> {
            override fun onResponse(call: Call<PointRoot>, response: Response<PointRoot>) {
                Log.d("포인트", "Response: ${response}")

                if (response.isSuccessful) {
                    val root : PointRoot? = response.body()
                    Log.d("포인트", "Root: ${root}")
                    val point : Point? = root?.point
                    Log.d("포인트", "Result: ${point}")

                    point.let {
                        binding.potTvHeartPoint.text = it?.point.toString()
                    }
                }
            }

            override fun onFailure(call: Call<PointRoot>, t: Throwable) {
                Log.d("포인트 불러오기 실패", "myPointCall: ${t.message}")
            }
        })

        val flowerCall : Call<FlowerRoot> = service.getFlower()
        Log.d("현재 꽃", "flowerCall: ${flowerCall}")
        flowerCall.enqueue(object: Callback<FlowerRoot> {
            override fun onResponse(call: Call<FlowerRoot>, response: Response<FlowerRoot>) {
                Log.d("현재 꽃", "Response : ${response}")

                if (response.isSuccessful) {
                    val root : FlowerRoot? = response.body()
                    Log.d("현재 꽃", "root : ${root}")
                    val flower : Flower? = root?.flower
                    Log.d("현재 꽃", "flower : ${flower}")

                    flower.let {
                        binding.potTvPlantsName.text = it?.name.toString()
                        //이미지뷰에 it?.img 값 넣기
                    }
                }
            }

            override fun onFailure(call: Call<FlowerRoot>, t: Throwable) {
                Log.d("현재 꽃 불러오기 실패", "flowerCall: ${t.message}")
            }
        })

        return binding.root
    }

    fun giveLove(service: PotInterface) {
        val loveCall: Call<LoveRoot> = service.giveLove()
        Log.d("사랑주기", "loveCall: ${loveCall}")
        loveCall.enqueue(object : Callback<LoveRoot> {
            override fun onResponse(call: Call<LoveRoot>, response: Response<LoveRoot>) {
                Log.d("사랑주기", "Response: ${response}")
                if(response.isSuccessful) {
                    val root : LoveRoot? = response.body()
                    Log.d("사랑주기", "root: ${root}")
                    val result : Result? = root?.result
                    Log.d("사랑주기", "result: ${result}")
                    val img : ResultImg? = result?.img
                    Log.d("사랑주기", "img: ${img}")

                    result.let {
                        binding.potTvHeartPoint.text = it?.point.toString()
                        //프로그레스바 이미지 변경
                    }
                }
            }

            override fun onFailure(call: Call<LoveRoot>, t: Throwable) {
                Log.d("사랑주기", "loveCall: ${t.message}")
            }
        })
    }
}