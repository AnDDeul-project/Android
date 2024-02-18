package com.umc.anddeul.checklist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.checklist.model.CompleteCheck
import com.umc.anddeul.checklist.model.CompleteRoot
import com.umc.anddeul.checklist.model.Root
import com.umc.anddeul.checklist.network.ChecklistInterface
import com.umc.anddeul.databinding.FragmentChecklistBinding
import com.umc.anddeul.databinding.ItemChecklistBinding
import com.umc.anddeul.postbox.LetterListFragment
import com.umc.anddeul.postbox.PostboxFragment
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Date
import java.util.Locale

class ChecklistFragment : Fragment() {

    lateinit var binding: FragmentChecklistBinding
    private var currentStartOfWeek: LocalDate = LocalDate.now()
    lateinit var selectedDateText : String
    private var checklist : ArrayList<Checklist>? = null
    lateinit var checklistRVAdapter : ChecklistRVAdapter
    val today : String = SimpleDateFormat("yyyy-MM-dd").format(Date())
    private lateinit var selectedDay: LocalDate
    val CAMERA_REQUEST_CODE = 405
    val REQUEST_IMAGE_CAPTURE = 406

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChecklistBinding.inflate(inflater, container, false)

        //리사이클러뷰 연결
        checklistRVAdapter = ChecklistRVAdapter(requireContext())
        binding.checklistRecylerView.adapter = checklistRVAdapter
        binding.checklistRecylerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 초기 세팅
        setWeek(currentStartOfWeek)

        // 저번주
        binding.checkliBeforeBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.minusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.checkliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(currentStartOfWeek)
        }

        // 다음주
        binding.checkliAfterBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.plusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.checkliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(currentStartOfWeek)
        }

        selectedDateText = SimpleDateFormat("yyyy-MM-dd").format(Date())
        Log.d("날짜", "${selectedDateText}")

        //spf 받아오기
        val spf : SharedPreferences = context!!.getSharedPreferences("myToken", Context.MODE_PRIVATE)
        val spfMyId = context!!.getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val myId = spfMyId.getString("myId", "")
        Log.d("myId", "${myId}")
        val spfMyName = context!!.getSharedPreferences("checkUserName", Context.MODE_PRIVATE)
        val myName = spfMyName.getString("checkUserName", "")

        binding.checkliTvName.text = myName

//        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzA0MTMzMDkzIl0sImlhdCI6MTcwNjY4MzkxMH0.ncVxzwxBVaiMegGD0VU5pI5i9GJjhrU8kUIYtQrSLSg"
        //토큰 가져오기
        val token = spf.getString("jwtToken", "")
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

        val service = retrofit.create(ChecklistInterface::class.java)

        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val readCall : Call<Root> = service.getChecklist(
            myId!!,
            false,
            "2024-02-17"
        )
        readCall.enqueue(object : Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                Log.d("api 조회", "Response ${response}")

                if (response.isSuccessful) {
                    val root : Root? = response.body()
                    val result : List<Checklist>? = root?.checklist

                    result?.let {
                        checklistRVAdapter.setChecklistData(it)
                        checklistRVAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                Log.d("read 실패", "readCall: ${t.message}")
            }
        })

        return binding.root
    }

    fun readApi(service : ChecklistInterface, spfMyId : String) {
        val readCall : Call<Root> = service.getChecklist(
            "3304133093",
            false,
            "2000-12-05"
        )
        Log.d("조회", "readCall ${readCall}")
        readCall.enqueue(object : Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                Log.d("api 조회", "Response ${response}")

                if (response.isSuccessful) {
                    val root : Root? = response.body()
                    Log.d("조회", "Root : ${root}")
                    val result : List<Checklist>? = root?.checklist
                    Log.d("조회", "Result : ${result}")

                    result?.let {
                        checklistRVAdapter.setChecklistData(it)
                        checklistRVAdapter.notifyDataSetChanged()
                        binding.checkliTvName.text = result?.get(0)?.receiver
                    }
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                Log.d("read 실패", "readCall: ${t.message}")
            }
        })
    }

    fun completeApi(service: ChecklistInterface, spfMyId : String) {
        val completeCall : Call<CompleteRoot> = service.complete(
            17
        )
        completeCall.enqueue(object : Callback<CompleteRoot> {
            override fun onResponse(call: Call<CompleteRoot>, response: Response<CompleteRoot>) {
                Log.d("complete", "Response ${response}")

                if (response.isSuccessful) {
                    val root : CompleteRoot? = response.body()
                    val check : CompleteCheck? = root?.check

                    if (root?.isSuccess == true) {
                        check.let {
                            readApi(service, spfMyId)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<CompleteRoot>, t: Throwable) {
                Log.d("complete 실패", "completeCall : ${t.message}")
            }
        })
    }

    private fun setWeek(startOfWeek: LocalDate) {
        val nearestMonday = startOfWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val yearMonth = YearMonth.from(nearestMonday)
        binding.checkliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"

        for (i in 1..7) {
            val currentDateForDay = nearestMonday.plusDays(i.toLong() - 1)
            val dateTextView = when (i) {
                1 -> binding.date1
                2 -> binding.date2
                3 -> binding.date3
                4 -> binding.date4
                5 -> binding.date5
                6 -> binding.date6
                7 -> binding.date7
                else -> null
            }
            val dayTextView = when (i) {
                1 -> binding.day1
                2 -> binding.day2
                3 -> binding.day3
                4 -> binding.day4
                5 -> binding.day5
                6 -> binding.day6
                7 -> binding.day7
                else -> null
            }

            dateTextView?.text = formatDate(currentDateForDay)

            // 오늘 날짜에 동그라미 표시
            val today = LocalDate.now()
            val isTodayInWeek = startOfWeek <= today && today <= startOfWeek.plusDays(6)

            // 선택한 날짜에 동그라미 표시
//            val isSelectedDay = startOfWeek <= selectedDay && selectedDay <= startOfWeek.plusDays(6)

            if (isTodayInWeek) {
                if (today == currentDateForDay) {
                    binding.checkliTodayCircle.visibility = View.VISIBLE
                    dateTextView?.viewTreeObserver?.addOnPreDrawListener(object :
                        ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            dateTextView.viewTreeObserver.removeOnPreDrawListener(this)
                            val dateTextViewX = dateTextView.x
                            val dateTextViewWidth = dateTextView.width.toFloat()
                            val circleWidth = binding.checkliTodayCircle.width.toFloat()
                            binding.checkliTodayCircle.x =
                                dateTextViewX + (dateTextViewWidth - circleWidth) / 2
                            return true
                        }
                    })
                    dateTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    dayTextView?.setTextColor(Color.parseColor("#1D1D1D"))
                    dayTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                    dateTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                }
            } else {
                dateTextView?.setTextColor(Color.parseColor("#666666"))
                dayTextView?.setTextColor(Color.parseColor("#666666"))
                dayTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                dateTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                binding.checkliTodayCircle.visibility = View.GONE
            }

            // 날짜 선택 시
            dateTextView?.setOnClickListener {
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                selectedDateText = currentDateForDay.format(dateFormat)
                Log.d("날짜 선택", "${selectedDateText}")

                if (currentDateForDay == LocalDate.now()) {
                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ChecklistFragment())
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                }
                else {
                    val checklistFragment = ChecklistFragment()

                    val bundle = Bundle()
                    bundle.putString("selectedDate", selectedDateText)
                    checklistFragment.arguments = bundle

                    // 날짜별 편지 확인 페이지로 이동
                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, checklistFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                }
            }

        }
    }

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        return date.format(formatter)
    }
}