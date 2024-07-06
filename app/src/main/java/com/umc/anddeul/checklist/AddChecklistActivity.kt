package com.umc.anddeul.checklist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.anddeul.R
import com.umc.anddeul.checklist.model.AddChecklist
import com.umc.anddeul.checklist.model.AddRoot
import com.umc.anddeul.checklist.model.Check
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.checklist.model.Root
import com.umc.anddeul.checklist.network.ChecklistInterface
import com.umc.anddeul.checklist.service.ChecklistService
import com.umc.anddeul.common.AnddeulErrorToast
import com.umc.anddeul.common.AnddeulToast
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.ActivityAddChecklistBinding
import com.umc.anddeul.home.model.UserProfileDTO
import com.umc.anddeul.home.model.UserProfileData
import com.umc.anddeul.home.network.UserProfileInterface
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Date
import java.util.Locale

class AddChecklistActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddChecklistBinding
    var token : String? = null
    lateinit var retrofit: Retrofit

    private var currentStartOfWeek: LocalDate = LocalDate.now()
    lateinit var selectedDateText : String
    private var selectedDay: LocalDate = LocalDate.now()
    lateinit var addChecklistRVAdapter: AddChecklistRVAdapter
    val today = LocalDate.now()

    //인텐트 정보 추출
    lateinit var checkUserId : String
    lateinit var  checkUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUserId = intent.getStringExtra("checkUserId")!!
        checkUserName = intent.getStringExtra("checkUserName")!!

        token = TokenManager.getToken()
        retrofit = RetrofitManager.getRetrofitInstance()

        //서비스 생성
        val service = retrofit.create(ChecklistInterface::class.java)

        binding.checkliAddTvName.text = checkUserName
        binding.addCheckliEtReader.text = checkUserName + "님에게 할 일을 남겨보세요"

        //날짜
        val dateStamp : String = SimpleDateFormat("MM월 dd일").format(Date())
        binding.addCheckliSelectDateTv.text = dateStamp

        //리사이클러뷰 연결
        addChecklistRVAdapter = AddChecklistRVAdapter()
        binding.checklistAddRecylerView.adapter = addChecklistRVAdapter
        binding.checklistAddRecylerView.layoutManager = LinearLayoutManager(this@AddChecklistActivity, LinearLayoutManager.VERTICAL, false)

        // 초기 세팅
        setWeek(currentStartOfWeek, service, checkUserId)

        // 저번주
        binding.addCheckliBeforeBtn.setOnClickListener {
            selectedDay = selectedDay.minusWeeks(1)
            val yearMonth = YearMonth.from(selectedDay)
            binding.addCheckliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            if (selectedDay == today) {
                setWeek(selectedDay, service, checkUserId)
            }
            else {
                setSelectedWeek(selectedDay, service, checkUserId)
            }
        }

        // 다음주
        binding.addCheckliAfterBtn.setOnClickListener {
            if (selectedDay < today) {
                selectedDay = selectedDay.plusWeeks(1)
                val yearMonth = YearMonth.from(selectedDay)
                binding.addCheckliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"

                if (selectedDay == today) {
                    setWeek(selectedDay, service, checkUserId)
                } else {
                    setSelectedWeek(selectedDay, service, checkUserId)
                }
            }
        }

        selectedDateText = SimpleDateFormat("yyyy-MM-dd").format(Date())

        //현재 체크리스트 불러오기
        readApi(service)

        binding.addCheckliEtContents.setOnEditorActionListener {v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = binding.addCheckliEtContents.text.toString()
                val dateList = selectedDateText.split("-")
                val addChecklist = AddChecklist(checkUserId, dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt(), text)

                //체크리스트 추가 api
                addApi(service, addChecklist)
                binding.addCheckliEtContents.setText(null)
            }
            readApi(service)
            handled
        }
    }

    private fun addApi(service : ChecklistInterface, addChecklist: AddChecklist) {
        val addCall : Call<AddRoot> = service.addCheckliist(
            addChecklist
        )

        addCall.enqueue(object : Callback<AddRoot> {
            override fun onResponse(call: Call<AddRoot>, response: Response<AddRoot>) {
                Log.d("Checklist AddService code", "${response.code()}")
                Log.d("Checklist AddService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : AddRoot? = response.body()
                    val checklist: List<Check>? = root?.check

                    Log.d("확", "${root}, ${checklist}")
                }
            }

            override fun onFailure(call: Call<AddRoot>, t: Throwable) {
                Log.d("Checklist AddService Fail", "readCall: ${t.message}")
            }
        })
    }

    fun readApi(service : ChecklistInterface) {
        val readCall : Call<Root> = service.getChecklist(
            checkUserId!!,
            true,
            selectedDay.toString()
        )
        readCall.enqueue(object : Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                Log.d("Checklist ReadService code", "${response.code()}")
                Log.d("Checklist ReadService body", "${response.body()}")

                if (response.isSuccessful) {
                    val root : Root? = response.body()
                    val result : List<Checklist>? = root?.checklist
                    Log.d("확", "${root}, ${result}")

                    result?.let {
                        addChecklistRVAdapter.setChecklistData(it)
                        addChecklistRVAdapter.notifyDataSetChanged()
                    }
                }
                if (response.code() == 500) {
                    val checklist = ArrayList<Checklist>()
                    addChecklistRVAdapter.setChecklistData(checklist)
                    addChecklistRVAdapter.notifyDataSetChanged()
                    AnddeulErrorToast.createToast(this@AddChecklistActivity, "인터넷 연결이 불안정합니다")?.show()
                }

                if (response.code() == 451) {
                    val checklist = ArrayList<Checklist>()
                    addChecklistRVAdapter.setChecklistData(checklist)
                    addChecklistRVAdapter.notifyDataSetChanged()
                    AnddeulToast.createToast(this@AddChecklistActivity, "해당 날짜에 만들어진 체크리스트가 없습니다.")?.show()
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                Log.d("Checklist ReadService Fail", "readCall: ${t.message}")
            }
        })
    }

    //오늘 날짜 동그라미 함수
    private fun setWeek(startOfWeek: LocalDate, service : ChecklistInterface, checkUserId : String) {
        val nearestMonday = startOfWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val yearMonth = YearMonth.from(nearestMonday)
        binding.addCheckliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"

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
                    binding.addCheckliTodayCircle.visibility = View.VISIBLE
                    binding.addChecklistSelectCircle.visibility = View.INVISIBLE
                    dateTextView?.viewTreeObserver?.addOnPreDrawListener(object :
                        ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            dateTextView.viewTreeObserver.removeOnPreDrawListener(this)
                            val dateTextViewX = dateTextView.x
                            val dateTextViewWidth = dateTextView.width.toFloat()
                            val circleWidth = binding.addCheckliTodayCircle.width.toFloat()
                            binding.addCheckliTodayCircle.x =
                                dateTextViewX + (dateTextViewWidth - circleWidth) / 2
                            return true
                        }
                    })
                    dateTextView?.setTextColor(ContextCompat.getColor(this, R.color.white))
                    dayTextView?.setTextColor(Color.parseColor("#1D1D1D"))
                    dayTextView?.typeface = ResourcesCompat.getFont(this, R.font.font_pretendard_bold)
                    dateTextView?.typeface = ResourcesCompat.getFont(this, R.font.font_pretendard_bold)
                }
                else {
                    dateTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.typeface = ResourcesCompat.getFont(this, R.font.font_pretendard_regular)
                    dateTextView?.typeface = ResourcesCompat.getFont(this, R.font.font_pretendard_regular)
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
                    setWeek(selectedDay, service, checkUserId)
                }
                else {
                    setSelectedWeek(selectedDay, service, checkUserId)
                }
            }
        }
        readApi(service)
    }

    //내가 선택한 날짜로 넘어가기 및 동그라미
    private fun setSelectedWeek(startOfWeek: LocalDate, service : ChecklistInterface, checkUserId : String) {
        val nearestMonday = startOfWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val yearMonth = YearMonth.from(nearestMonday)
        binding.addCheckliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"

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
                    binding.addChecklistSelectCircle.visibility = View.VISIBLE
                    binding.addCheckliTodayCircle.visibility = View.INVISIBLE
                    dateTextView?.viewTreeObserver?.addOnPreDrawListener(object :
                        ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            dateTextView.viewTreeObserver.removeOnPreDrawListener(this)
                            val dateTextViewX = dateTextView.x
                            val dateTextViewWidth = dateTextView.width.toFloat()
                            val circleWidth = binding.addChecklistSelectCircle.width.toFloat()
                            binding.addChecklistSelectCircle.x =
                                dateTextViewX + (dateTextViewWidth - circleWidth) / 2
                            return true
                        }
                    })
                    dateTextView?.setTextColor(ContextCompat.getColor(this, R.color.white))
                    dayTextView?.setTextColor(Color.parseColor("#1D1D1D"))
                    dayTextView?.typeface = ResourcesCompat.getFont(this, R.font.font_pretendard_bold)
                    dateTextView?.typeface = ResourcesCompat.getFont(this, R.font.font_pretendard_bold)
                }
                else {
                    dateTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.typeface = ResourcesCompat.getFont(this, R.font.font_pretendard_regular)
                    dateTextView?.typeface = ResourcesCompat.getFont(this, R.font.font_pretendard_regular)
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
                    setWeek(selectedDay, service, checkUserId)
                }
                else {
                    setSelectedWeek(selectedDay, service, checkUserId)
                }
            }
        }
        readApi(service)
    }

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        return date.format(formatter)
    }
}