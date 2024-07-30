package com.umc.anddeul.pot

import android.content.Intent
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
import com.umc.anddeul.R
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.databinding.FragmentGardenBinding
import com.umc.anddeul.pot.model.Flower
import com.umc.anddeul.pot.model.FlowerRoot
import com.umc.anddeul.pot.model.Flowers
import com.umc.anddeul.pot.model.Garden
import com.umc.anddeul.pot.model.GardenRoot
import com.umc.anddeul.pot.model.Point
import com.umc.anddeul.pot.model.PointRoot
import com.umc.anddeul.pot.network.PotInterface
import com.umc.anddeul.start.StartActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class GardenFragment : Fragment() {
    lateinit var binding : FragmentGardenBinding
    var flowerId = 0
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

        binding.gardenBtnThemeNext.setOnClickListener {
            season = "여름"
            seasonSet()
            flowerApi(service)
        }

        binding.gardenBtnThemeBefore.setOnClickListener {
            season = "봄"
            seasonSet()
            flowerApi(service)
        }

        seasonSet()
        pointApi(service)
        flowerApi(service)

        return binding.root
    }

    private fun flowerApi(service: PotInterface) {
        val flowerCall : Call<FlowerRoot> = service.getFlower()
        flowerCall.enqueue(object: Callback<FlowerRoot> {
            override fun onResponse(call: Call<FlowerRoot>, response: Response<FlowerRoot>) {
                Log.d("Flower CurrentPotService code", "${response.code()}")
                Log.d("Flower CurrentPotService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : FlowerRoot? = response.body()
                    val flower : Flower? = root?.flower
                    flowerId = flower!!.idx
                    gardenApi(service, flowerId)
                }
                if (response.code() == 500) {
                    AnddeulErrorToast.createToast(context!!, "인터넷 연결이 불안정합니다")?.show()
                }
                if (response.code() == 401) {
                    val startIntent = Intent(context, StartActivity::class.java)
                    context!!.startActivity(startIntent)
                }
            }

            override fun onFailure(call: Call<FlowerRoot>, t: Throwable) {
                Log.d("Flower CurrentPotService Fail", "flowerCall: ${t.message}")
                AnddeulErrorToast.createToast(context!!, "서버 연결이 불안정합니다")?.show()
            }
        })
    }

    private fun pointApi(service: PotInterface) {
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

                    if (response.code() == 500) {
                        AnddeulErrorToast.createToast(context!!, "인터넷 연결이 불안정합니다")?.show()
                    }
                    if (response.code() == 401) {
                        val startIntent = Intent(context, StartActivity::class.java)
                        context!!.startActivity(startIntent)
                    }
                }
            }

            override fun onFailure(call: Call<PointRoot>, t: Throwable) {
                Log.d("Flower PointService Fail", "myPointCall: ${t.message}")
                AnddeulErrorToast.createToast(context!!, "서버 연결이 불안정합니다")?.show()
            }
        })
    }

    private fun gardenApi(service : PotInterface, flowerId : Int) {
        val gardenCall : Call<GardenRoot> = service.getGarden(flowerId)
        gardenCall.enqueue(object: Callback<GardenRoot> {
            override fun onResponse(call: Call<GardenRoot>, response: Response<GardenRoot>) {
                Log.d("Flower GardenService code", "${response.code()}")
                Log.d("Flower GardenService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : GardenRoot? = response.body()
                    val garden : Garden? = root?.garden

                    if (season == garden?.theme) {
                        val flowers: List<Flowers>? = garden?.flowers
                        flowers.let {
                            var i = 0
                            while (i < flowers!!.size) {
                                when (i) {
                                    0 -> binding.gardenIvFlower1.loadImageFromUrl(flowers?.get(i)?.img_5!!)
                                    1 -> binding.gardenIvFlower2.loadImageFromUrl(flowers?.get(i)?.img_5!!)
                                    2 -> binding.gardenIvFlower3.loadImageFromUrl(flowers?.get(i)?.img_5!!)
                                    3 -> binding.gardenIvFlower4.loadImageFromUrl(flowers?.get(i)?.img_5!!)
                                }
                                i++
                            }
                        }
                    }
                    else {
                        binding.gardenIvFlower1.setImageResource(R.drawable.garden_img_plants1)
                        binding.gardenIvFlower2.setImageResource(R.drawable.garden_img_plants1)
                        binding.gardenIvFlower3.setImageResource(R.drawable.garden_img_plants2)
                        binding.gardenIvFlower4.setImageResource(R.drawable.garden_img_plants2)
                    }

                    if (response.code() == 500) {
                        AnddeulErrorToast.createToast(context!!, "인터넷 연결이 불안정합니다")?.show()
                    }
                    if (response.code() == 401) {
                        val startIntent = Intent(context, StartActivity::class.java)
                        context!!.startActivity(startIntent)
                    }
                }
            }

            override fun onFailure(call: Call<GardenRoot>, t: Throwable) {
                Log.d("Flower GardenService Fail", "flowerCall: ${t.message}")
                AnddeulErrorToast.createToast(context!!, "서버 연결이 불안정합니다")?.show()
            }
        })
    }

    fun seasonSet() {
        when (season) {
            "봄" -> {
                binding.gardenBtnThemeBefore.visibility = View.GONE
                binding.gardenBtnThemeNext.visibility = View.VISIBLE
                binding.gardenTvUsGarden.text = "우리 가족 정원: 봄"
            }
            "여름" -> {
                binding.gardenBtnThemeBefore.visibility = View.VISIBLE
                binding.gardenBtnThemeNext.visibility = View.GONE
                binding.gardenTvUsGarden.text = "우리 가족 정원: 여름"
            }
        }
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