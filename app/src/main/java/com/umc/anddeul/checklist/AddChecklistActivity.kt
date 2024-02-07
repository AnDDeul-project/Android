package com.umc.anddeul.checklist

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.anddeul.R
import com.umc.anddeul.databinding.ActivityAddChecklistBinding
import com.umc.anddeul.databinding.FragmentChecklistBinding
import com.umc.anddeul.postbox.LetterListFragment
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
    private var checklist = ArrayList<Checklist>()
    private var currentStartOfWeek: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dateStamp : String = SimpleDateFormat("MM월 dd일").format(Date())
        binding.addCheckliSelectDateTv.text = dateStamp

        checklist.add(Checklist("메모메모", "율", "image", true))
        checklist.add(Checklist("달력 UI 수정할 예정이에요", "율", "image", false))

        //리사이클러뷰 연결
        val addChecklistRVAdapter = AddChecklistRVAdapter(checklist)
        binding.checklistAddRecylerView.adapter = addChecklistRVAdapter
        binding.checklistAddRecylerView.layoutManager = LinearLayoutManager(this@AddChecklistActivity, LinearLayoutManager.VERTICAL, false)

        // 초기 세팅
        setWeek(currentStartOfWeek)

        // 저번주
        binding.addCheckliBeforeBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.minusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.addCheckliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(currentStartOfWeek)
        }

        // 다음주
        binding.addCheckliAfterBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.plusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.addCheckliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(currentStartOfWeek)
        }

        binding.addCheckliEtContents.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                //체크리스트 추가 코드
            }
            true
        }
    }

    private fun setWeek(startOfWeek: LocalDate) {
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
                    dateTextView?.setTextColor(ContextCompat.getColor(this@AddChecklistActivity, R.color.white))
                    dayTextView?.setTextColor(Color.parseColor("#1D1D1D"))
                    dayTextView?.typeface = ResourcesCompat.getFont(this@AddChecklistActivity, R.font.font_pretendard_bold)
                    dateTextView?.typeface = ResourcesCompat.getFont(this@AddChecklistActivity, R.font.font_pretendard_bold)
                }
            } else {
                dateTextView?.setTextColor(Color.parseColor("#666666"))
                dayTextView?.setTextColor(Color.parseColor("#666666"))
                dayTextView?.typeface = ResourcesCompat.getFont(this@AddChecklistActivity, R.font.font_pretendard_regular)
                dateTextView?.typeface = ResourcesCompat.getFont(this@AddChecklistActivity, R.font.font_pretendard_regular)
                binding.addCheckliTodayCircle.visibility = View.GONE
            }

            // 날짜 선택 시
            dateTextView?.setOnClickListener {
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val selectedDateText = currentDateForDay.format(dateFormat)

                val letterListFragment = LetterListFragment()

                val bundle = Bundle()
                bundle.putString("selectedDate", selectedDateText)
                letterListFragment.arguments = bundle

                // 날짜별 편지 체크리스트 분리
            }

        }
    }

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        return date.format(formatter)
    }

}