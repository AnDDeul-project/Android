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
    private var currentStartOfWeek: LocalDate = LocalDate.now()
    lateinit var selectedDateText : String
    private var selectedDay: LocalDate = LocalDate.now()
    lateinit var addChecklistRVAdapter: AddChecklistRVAdapter
    val today = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인텐트 정보 추출
        val checkUserId = intent.getStringExtra("checkUserId")
        val checkUserName = intent.getStringExtra("checkUserName")

        //토큰 가져오기
        val spf: SharedPreferences = this!!.getSharedPreferences("myToken", Context.MODE_PRIVATE)
        val token = spf.getString("jwtToken", "")
        val spfMyId = this.getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val myId = spfMyId.getString("myId", "")
        Log.d("myId", "${myId}")

        val retrofit = Retrofit.Builder()
            .baseUrl("http://umc-garden.store")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token.orEmpty())
                            .build()
                        Log.d("retrofitBearer", "Token: " + token.orEmpty())
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()

        //서비스 생성
        val service = retrofit.create(ChecklistInterface::class.java)
        val serviceUser = retrofit.create(UserProfileInterface::class.java)

        //주인 이름
        val profileCall : Call<UserProfileDTO> = serviceUser.getUserProfile(checkUserId!!)
        profileCall.enqueue(object : Callback<UserProfileDTO> {
            override fun onResponse(
                call: Call<UserProfileDTO>,
                response: Response<UserProfileDTO>
            ) {
                if (response.isSuccessful) {
                    val root : UserProfileDTO? = response.body()
                    val result : UserProfileData? = root?.result

                    result.let {
                        binding.checkliAddTvName.text = result?.nickname
                        binding.addCheckliEtReader.text = result?.nickname + "님에게 할 일을 남겨보세요"
                    }
                }
            }

            override fun onFailure(call: Call<UserProfileDTO>, t: Throwable) {
                Log.d("구성원 조회", "${t.message}")
            }
        })

        //날짜
        val dateStamp : String = SimpleDateFormat("MM월 dd일").format(Date())
        binding.addCheckliSelectDateTv.text = dateStamp
        Log.d("날짜", "오늘 날짜: ${today}")

        //리사이클러뷰 연결
        addChecklistRVAdapter = AddChecklistRVAdapter()
        binding.checklistAddRecylerView.adapter = addChecklistRVAdapter
        binding.checklistAddRecylerView.layoutManager = LinearLayoutManager(this@AddChecklistActivity, LinearLayoutManager.VERTICAL, false)

        // 초기 세팅
        setWeek(currentStartOfWeek, service, checkUserId!!)

        // 저번주
        binding.addCheckliBeforeBtn.setOnClickListener {
            selectedDay = selectedDay.minusWeeks(1)
            val yearMonth = YearMonth.from(selectedDay)
            binding.addCheckliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            Log.d("날짜", "selectedDateText ${selectedDateText}")
            Log.d("날짜", "selectedDay ${selectedDay}")
            if (selectedDay == today) {
                setWeek(selectedDay, service, myId!!)
            }
            else {
                setSelectedWeek(selectedDay, service, myId!!)
            }
        }

        // 다음주
        binding.addCheckliAfterBtn.setOnClickListener {
            selectedDay = selectedDay.plusWeeks(1)
            val yearMonth = YearMonth.from(selectedDay)
            binding.addCheckliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            Log.d("날짜", "selectedDateText ${selectedDateText}")
            Log.d("날짜", "selectedDay ${selectedDay}")
            if (selectedDay == today) {
                setWeek(selectedDay, service, myId!!)
            }
            else {
                setSelectedWeek(selectedDay, service, myId!!)
            }
        }

        selectedDateText = SimpleDateFormat("yyyy-MM-dd").format(Date())
        Log.d("날짜", "selectedDateText ${selectedDateText}")
        Log.d("날짜", "selectedDay ${selectedDay}")


        //현재 체크리스트 불러오기
        readApi(service, checkUserId!!)

        //체크리스트 추가 동작
//        binding.addCheckliEtContents.setOnEditorActionListener { _, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP)) {
                //체크리스트 객체 생성 코드
//                val text = binding.addCheckliEtContents.text.toString()
//                val dateList = selectedDateText.split("-")
//                val addChecklist = AddChecklist(checkUserId, dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt(), text)
//                Log.d("체크리스트 값 확인", "${addChecklist}")
//
//                //체크리스트 추가 api
//                addApi(service, addChecklist)
//
//                //체크리스트 변환된 거 불러오기
//                readApi(service, checkUserId!!)
//                binding.addCheckliEtContents.text.clear()
//
//                // 키보드 숨기기
//                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(binding.addCheckliEtContents.windowToken, 0)
//
//                return@setOnEditorActionListener true
//            }
//            false
//        }


        //동그라미 클릭시 추가
        binding.addCheckliEtCircle.setOnClickListener {
            val text = binding.addCheckliEtContents.text.toString()
            val dateList = selectedDateText.split("-")
            val addChecklist = AddChecklist(checkUserId, dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt(), text)

            //체크리스트 추가 api
            addApi(service, addChecklist)

            //체크리스트 변환된 거 불러오기
            readApi(service, checkUserId!!)
            binding.addCheckliEtContents.text.clear()
        }

    }

    private fun addApi(service : ChecklistInterface, addChecklist: AddChecklist) {
        val addCall : Call<AddRoot> = service.addCheckliist(
            addChecklist
        )

        addCall.enqueue(object : Callback<AddRoot> {
            override fun onResponse(call: Call<AddRoot>, response: Response<AddRoot>) {
                Log.d("api 추가", "Response ${response}")

                if (response.isSuccessful) {
                    val root : AddRoot? = response.body()
                    val checklist: List<Check>? = root?.check
                }
            }

            override fun onFailure(call: Call<AddRoot>, t: Throwable) {
                Log.d("add 실패", "readCall: ${t.message}")
            }
        })
    }

    fun readApi(service : ChecklistInterface, spfMyId : String) {
        val readCall : Call<Root> = service.getChecklist(
            spfMyId!!,
            false,
            selectedDay.toString()
        )
        readCall.enqueue(object : Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                Log.d("api 조회", "Response ${response}")

                if (response.isSuccessful) {
                    val root : Root? = response.body()
                    val result : List<Checklist>? = root?.checklist

                    result?.let {
                        addChecklistRVAdapter.setChecklistData(it)
                        addChecklistRVAdapter.notifyDataSetChanged()
                    }
                }
                if (response.code() == 451) {
                    val checklist = ArrayList<Checklist>()
                    addChecklistRVAdapter.setChecklistData(checklist)
                    addChecklistRVAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                Log.d("read 실패", "readCall: ${t.message}")
            }
        })
    }

    //오늘 날짜 동그라미 함수
    private fun setWeek(startOfWeek: LocalDate, service : ChecklistInterface, spfMyId : String) {
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
                    binding.addCheckliTodayCircle.visibility = View.GONE
                }
            }

            // 날짜 선택 시
            dateTextView?.setOnClickListener {
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                selectedDateText = currentDateForDay.format(dateFormat)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val selectDate = LocalDate.parse(selectedDateText, formatter)
                selectedDay = selectDate
                Log.d("날짜 선택", "selectedDate ${selectedDateText}")
                Log.d("날짜 선택", "currentDateForDay ${currentDateForDay}")
                Log.d("날짜 선택", "selectDate ${selectDate}")
                Log.d("날짜 선택", "selectedDay ${selectedDay}")
                Log.d("날짜 선택", "today ${today}")
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
//                Log.d("날짜", "선택 날짜 ${selectedDay} 현재${currentDateForDay}")
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
                    binding.addCheckliTodayCircle.visibility = View.GONE
                }
            }

            // 날짜 선택 시
            dateTextView?.setOnClickListener {
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                selectedDateText = currentDateForDay.format(dateFormat)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val selectDate = LocalDate.parse(selectedDateText, formatter)
                selectedDay = selectDate
                Log.d("날짜 선택", "selectDate ${selectDate}")
                Log.d("날짜 선택", "selectedDate ${selectedDateText}")
                Log.d("날짜 선택", "currentDateForDay ${currentDateForDay}")
                Log.d("날짜 선택", "selectedDay ${selectedDay}")
                Log.d("날짜 선택", "today ${today}")
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