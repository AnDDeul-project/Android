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
import com.umc.anddeul.checklist.service.ChecklistService
import com.umc.anddeul.common.AnddeulErrorToast
import com.umc.anddeul.common.AnddeulToast
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentChecklistBinding
import com.umc.anddeul.databinding.ItemChecklistBinding
import com.umc.anddeul.home.model.UserProfileDTO
import com.umc.anddeul.home.model.UserProfileData
import com.umc.anddeul.home.network.UserProfileInterface
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
    var token : String? = null
    lateinit var retrofit : Retrofit

    private var currentStartOfWeek: LocalDate = LocalDate.now()
    lateinit var selectedDateText : String
    lateinit var checklistRVAdapter : ChecklistRVAdapter
    val today = LocalDate.now()
    private var selectedDay: LocalDate = LocalDate.now()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChecklistBinding.inflate(inflater, container, false)

        //리사이클러뷰 연결
        checklistRVAdapter = ChecklistRVAdapter(requireContext())
        binding.checklistRecylerView.adapter = checklistRVAdapter
        binding.checklistRecylerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //spf 받아오기
        token = TokenManager.getToken()
        val spfMyId = requireActivity().getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val myId = spfMyId.getString("myId", "")

        retrofit = RetrofitManager.getRetrofitInstance()

        val service = retrofit.create(ChecklistInterface::class.java)
        val serviceUser = retrofit.create(UserProfileInterface::class.java)

        val profileCall : Call<UserProfileDTO> = serviceUser.getUserProfile(myId!!)
        profileCall.enqueue(object : Callback<UserProfileDTO> {
            override fun onResponse(
                call: Call<UserProfileDTO>,
                response: Response<UserProfileDTO>
            ) {
                if (response.isSuccessful) {
                    val root : UserProfileDTO? = response.body()
                    val result : UserProfileData? = root?.result

                    Log.d("구성원", "${result}")
                    result.let {
                        binding.checkliTvName.text = result?.nickname
                    }
                }

                if (response.code() == 500) {
                    val checklist = ArrayList<Checklist>()
                    checklistRVAdapter.setChecklistData(checklist)
                    checklistRVAdapter.notifyDataSetChanged()

                    val context = requireContext()
                    AnddeulErrorToast.createToast(context, "인터넷 연결이 불안정합니다")?.show()
                }
            }

            override fun onFailure(call: Call<UserProfileDTO>, t: Throwable) {
                Log.d("구성원", "${t.message}")
            }
        })

        // 초기 세팅
        setWeek(currentStartOfWeek, service, myId!!)

        // 저번주
        binding.checkliBeforeBtn.setOnClickListener {
            selectedDay = selectedDay.minusWeeks(1)
            val yearMonth = YearMonth.from(selectedDay)
            binding.checkliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            if (selectedDay == today) {
                setWeek(selectedDay, service, myId!!)
            }
            else {
                setSelectedWeek(selectedDay, service, myId!!)
            }
        }

        // 다음주
        //today면 오늘 날짜로 넘김 안 됨.
        binding.checkliAfterBtn.setOnClickListener {
            if (selectedDay < today) {
                selectedDay = selectedDay.plusWeeks(1)
                val yearMonth = YearMonth.from(selectedDay)
                binding.checkliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"

                if (selectedDay == today) {
                    setWeek(selectedDay, service, myId!!)
                } else {
                    setSelectedWeek(selectedDay, service, myId!!)
                }
            }
        }

        selectedDateText = SimpleDateFormat("yyyy-MM-dd").format(Date())

        return binding.root
    }

    fun readApi(service : ChecklistInterface, myId : String) {
        val readCall : Call<Root> = service.getChecklist(
            myId!!,
            false,
            selectedDay.toString()
        )

        readCall.enqueue(object : Callback<Root> {

            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                Log.d("Checklist ReadService code", "${response.code()}")
                Log.d("Checklist ReadService body", "${response.body()}")

                val checklist = ArrayList<Checklist>()
                checklistRVAdapter.setChecklistData(checklist)
                checklistRVAdapter.notifyDataSetChanged()
                val context = requireContext()

                if (response.isSuccessful) {
                    val root : Root? = response.body()
                    val result : List<Checklist>? = root?.checklist

                    result?.let {
                        checklistRVAdapter.setChecklistData(it)
                        checklistRVAdapter.notifyDataSetChanged()
                    }
                }
                if (response.code() == 500) {
                    AnddeulErrorToast.createToast(context, "인터넷 연결이 불안정합니다")?.show()
                }

                if (response.code() == 451) {
                    AnddeulToast.createToast(context, "해당 날짜에 만들어진 체크리스트가 없습니다.")?.show()
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                val checklist = ArrayList<Checklist>()
                checklistRVAdapter.setChecklistData(checklist)
                checklistRVAdapter.notifyDataSetChanged()
                val context = requireContext()

                Log.d("Checklist ReadService Fail", "readCall: ${t.message}")
                AnddeulErrorToast.createToast(context!!, "서버 연결이 불안정합니다")?.show()
            }
        })
    }

    //오늘 날짜 동그라미 함수
    private fun setWeek(startOfWeek: LocalDate, service : ChecklistInterface, spfMyId : String) {
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

            if (isTodayInWeek) {
                if (today == currentDateForDay) {
                    binding.checkliTodayCircle.visibility = View.VISIBLE
                    binding.checklistSelectCircle.visibility = View.GONE
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
                else {
                    dateTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                    dateTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                }
            }

            // 날짜 선택 시
            dateTextView?.setOnClickListener {
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                selectedDateText = currentDateForDay.format(dateFormat)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val selectDate = LocalDate.parse(selectedDateText, formatter)
                selectedDay = selectDate
                if (selectedDay == today) {
                    setWeek(selectedDay, service, spfMyId)
                }
                else {
                    setSelectedWeek(selectedDay, service, spfMyId)
                }
            }
        }
        readApi(service, spfMyId!!)
    }

    //내가 선택한 날짜로 넘어가기 및 동그라미
    private fun setSelectedWeek(startOfWeek: LocalDate, service : ChecklistInterface, spfMyId : String) {
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

            // 선택한 날짜에 동그라미 표시
            val isSelectedDay = startOfWeek <= selectedDay && selectedDay <= startOfWeek.plusDays(6)

            if (isSelectedDay) {
                if (selectedDay == currentDateForDay) {
                    binding.checklistSelectCircle.visibility = View.VISIBLE
                    binding.checkliTodayCircle.visibility = View.GONE
                    dateTextView?.viewTreeObserver?.addOnPreDrawListener(object :
                        ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            dateTextView.viewTreeObserver.removeOnPreDrawListener(this)
                            val dateTextViewX = dateTextView.x
                            val dateTextViewWidth = dateTextView.width.toFloat()
                            val circleWidth = binding.checklistSelectCircle.width.toFloat()
                            binding.checklistSelectCircle.x =
                                dateTextViewX + (dateTextViewWidth - circleWidth) / 2
                            return true
                        }
                    })
                    dateTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    dayTextView?.setTextColor(Color.parseColor("#1D1D1D"))
                    dayTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                    dateTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                }
                else {
                    dateTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                    dateTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                }
            }

            // 날짜 선택 시
            dateTextView?.setOnClickListener {
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                selectedDateText = currentDateForDay.format(dateFormat)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val selectDate = LocalDate.parse(selectedDateText, formatter)
                selectedDay = selectDate
                if (selectedDay == today) {
                    setWeek(selectedDay, service, spfMyId)
                }
                else {
                    setSelectedWeek(selectedDay, service, spfMyId)
                }
            }
        }
        readApi(service, spfMyId!!)
    }

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        return date.format(formatter)
    }
}