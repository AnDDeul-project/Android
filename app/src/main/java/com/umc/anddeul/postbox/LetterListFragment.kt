package com.umc.anddeul.postbox

import android.os.Bundle
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
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentLetterlistBinding
import com.umc.anddeul.databinding.ItemCalendarBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class LetterListFragment : Fragment() {
    private lateinit var binding: FragmentLetterlistBinding
    private lateinit var letterlistAdapter: LetterListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLetterlistBinding.inflate(inflater, container, false)

        //// 달력
        val selectedDateStr = arguments?.getString("selectedDate")
        val selectedDate = selectedDateStr.let {
            LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = ItemCalendarBinding.bind(view)
            lateinit var day: WeekDay

            // 날짜 클릭 시
            init {
                view.setOnClickListener {
                    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                    if (day.date.format(dateFormat) == LocalDate.now().format(dateFormat)) {
                        (context as MainActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, PostboxFragment())
                            .addToBackStack(null)
                            .commitAllowingStateLoss()
                    }
                    else {
                        val selectedDateText = day.date.format(dateFormat)

                        val letterListFragment = LetterListFragment()

                        val bundle = Bundle()
                        bundle.putString("selectedDate", selectedDateText)
                        letterListFragment.arguments = bundle

                        (context as MainActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, letterListFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss()
                    }
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
                    else -> if (day.date == selectedDate) R.color.white else R.color.black
                }

                if (day.date == selectedDate) {
                    bind.calDateTv.setBackgroundResource(R.drawable.calendar_circle)
                } else {
                    bind.calDateTv.background = null
                }

                bind.calDateTv.setTextColor(ContextCompat.getColor(view.context, textColorRes))
            }
        }

        // 달력에 적용
        binding.rvCalendar.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
        }

        val selectedYearMonth = YearMonth.from(selectedDate)
        binding.rvCalendar.setup(
            selectedYearMonth.minusMonths(5).atStartOfMonth(),
            selectedYearMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeek = DayOfWeek.MONDAY,
        )
        binding.rvCalendar.scrollToDate(selectedDate)


        //// 편지 리스트
        letterlistAdapter = LetterListAdapter()

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

        letterlistAdapter.letters = dummyPosts

        //// 편지 보기(팝업)
        val onClickListener = object: LetterListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
                val postPopupFragment = LetterPopupFragment(requireContext())
                postPopupFragment.show(dummyPosts[pos])
            }
        }
        letterlistAdapter.setOnItemClickListener(onClickListener)

        binding.rvLetterlist.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvLetterlist.adapter = letterlistAdapter

        return binding.root
    }

}