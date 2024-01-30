package com.umc.anddeul.postbox

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentPostboxBinding
import com.umc.anddeul.databinding.ItemCalendarBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class PostboxFragment : Fragment() {
    private lateinit var binding: FragmentPostboxBinding
    private lateinit var postAdapter: LetterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostboxBinding.inflate(inflater, container, false)

        //// 화분 키우기 페이지로 이동
        binding.gotoPotBtn.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frm, PotFragment())
//                .addToBackStack(null)
//                .commitAllowingStateLoss()
        }


        //// 달력
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = ItemCalendarBinding.bind(view)
            lateinit var day: WeekDay

            // 날짜 클릭 시
            init {
                view.setOnClickListener {
                    // 달력에서 날짜 클릭 시 동작 추가

                    // 제대로 선택되었는지 확인 코드 (추후 삭제)
                    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val selectedDateText = day.date.format(dateFormat)
                    Toast.makeText(view.context, "Selected Date: $selectedDateText", Toast.LENGTH_SHORT).show()

                }
            }

            // 날짜 넣기
            fun bind(day: WeekDay) {
                val dateFormatter = DateTimeFormatter.ofPattern("dd")
                this.day = day
                bind.calDateTv.text = dateFormatter.format(day.date)
                bind.calDayTv.text = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN).toString()

                // 요일별 색상 다르게
                val textColorRes = when (day.date.dayOfWeek) {
                    DayOfWeek.SATURDAY -> R.color.system_informative
                    DayOfWeek.SUNDAY -> R.color.system_error
                    else -> if (day.date == LocalDate.now()) R.color.white else R.color.black
                }

                if (day.date == LocalDate.now()) {
                    bind.calDateTv.setBackgroundResource(R.drawable.calendar_circle)
                } else {
                    bind.calDateTv.background = null // Remove the background
                }

                bind.calDateTv.setTextColor(ContextCompat.getColor(view.context, textColorRes))
            }
        }

        // 달력에 적용
        binding.rvCalendar.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
        }

        val currentMonth = YearMonth.now()
        binding.rvCalendar.setup(
            currentMonth.minusMonths(5).atStartOfMonth(),
            currentMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeek = DayOfWeek.MONDAY,
        )
        binding.rvCalendar.scrollToDate(LocalDate.now())


        //// 편지 리스트
        postAdapter = LetterAdapter()

        // 테스트용 더미 데이터
        val dummyPosts = listOf(
            Letter(1, "아티", 0, "어쩌구저쩌구"),
            Letter(2, "도라", 0, "어쩌구저쩌구"),
            Letter(3, "지나", 0, "어쩌구저쩌구"),
            Letter(4, "율", 1, "음성 메세지가 도착했습니다."),
            Letter(5, "도도", 1, "음성 메세지가 도착했습니다."),
            Letter(6, "훈", 1, "음성 메세지가 도착했습니다."),
            Letter(7, "빈온", 0, "어쩌구저쩌구"),
            Letter(8, "세흐", 0, "어쩌구저쩌구"),
        )

        postAdapter.letters = dummyPosts


        //// 편지 보기(팝업)
        val onClickListener = object: LetterAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
                val postPopupFragment = LetterPopupFragment(requireContext())
                postPopupFragment.show(dummyPosts[pos])
            }
        }
        postAdapter.setOnItemClickListener(onClickListener)

        binding.rvLetters.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvLetters.adapter = postAdapter


        //// 편지 작성 (음성)
        binding.voiceIv.setOnClickListener{
            if(binding.letterEt.text.toString().isEmpty()){   // 작성된 텍스트 없을 때
                val recordPopupFragment = RecordPopupFragment(requireContext())
                recordPopupFragment.show()
            } else {    // 작성된 텍스트 있을 때
                val dialogFragment = DialogLetterFragment(requireContext())
                dialogFragment.show("voice")
            }
        }


        //// 편지 작성 (텍스트)
        binding.mailIv.setOnClickListener{
            // 편지 보내는 기능 추가

            //텍스트 초기화
            binding.letterEt.setText("")
        }

        return binding.root
    }
}
