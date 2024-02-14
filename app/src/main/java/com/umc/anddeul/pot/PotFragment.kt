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
import com.umc.anddeul.checklist.ChecklistFragment
import com.umc.anddeul.checklist.model.Result
import com.umc.anddeul.checklist.model.Root
import com.umc.anddeul.checklist.network.ChecklistInterface
import com.umc.anddeul.databinding.FragmentPotBinding
import com.umc.anddeul.pot.GardenFragment
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
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzMwNzIzOTQzIl0sImlhdCI6MTcwNzkyNjY2MX0.mx7dqaPYZBQUriSnvEkprp9ofGtMDWsdsEMTNpY0xtU"
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

        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

//        val myPointCall : Call<Root> = service.getMyPoint()
//        Log.d("조회", "readCall ${myPointCall}")
//        myPointCall.enqueue(object : Callback<Root> {
//            override fun onResponse(call: Call<Root>, response: Response<Root>) {
//                Log.d("api 조회", "Response ${response}")
//
//                if (response.isSuccessful) {
//                    val root : Root? = response.body()
////                    Log.d("조회", "Root : ${root}")
//                    val result : List<Result>? = root?.result
//                    Log.d("조회", "Result : ${result}")
//
//                    result.let {
//
//                    }
//                }
//            }

//            override fun onFailure(call: Call<Root>, t: Throwable) {
//                Log.d("read 실패", "readCall: ${t.message}")
//            }
//        })

        return binding.root
    }
}