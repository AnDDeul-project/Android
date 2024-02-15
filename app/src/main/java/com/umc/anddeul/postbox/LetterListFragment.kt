package com.umc.anddeul.postbox

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentLetterlistBinding
import com.umc.anddeul.postbox.service.FamilyService
import com.umc.anddeul.postbox.service.MailService
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class LetterListFragment : Fragment() {
    private lateinit var binding: FragmentLetterlistBinding
    private lateinit var letterlistAdapter: LetterListAdapter
    private lateinit var selectedDay: LocalDate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLetterlistBinding.inflate(inflater, container, false)

        //// 화분 키우기 페이지로 이동
        binding.gotoPotBtn2.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, PotFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        //// 달력
        val selectedDateStr = arguments?.getString("selectedDate")
        var selectedDate = selectedDateStr.let {
            LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
        selectedDay = selectedDateStr?.let {
            LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } ?: LocalDate.now()

        // 초기 세팅
        setWeek(selectedDate)

        // 저번주
        binding.beforeBtn2.setOnClickListener {
            selectedDate = selectedDate.minusWeeks(1)
            val yearMonth = YearMonth.from(selectedDate)
            binding.selectDateTv2.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(selectedDate)
        }

        // 다음주
        binding.afterBtn2.setOnClickListener {
            selectedDate = selectedDate.plusWeeks(1)
            val yearMonth = YearMonth.from(selectedDate)
            binding.selectDateTv2.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(selectedDate)
        }

        //// 편지 리스트
        letterlistAdapter = LetterListAdapter()

        val today = selectedDate
        Log.d("해당날짜", today.toString())
        val loadedToken = loadJwt() // jwt토큰
        val mailService = MailService()
        mailService.todayMail(loadedToken, today.toString()) { mailDTO ->
            Log.d("확2", mailDTO.toString())
            if (mailDTO != null) {
                if (mailDTO.isSuccess.toString() == "true") {
                    letterlistAdapter.letters = mailDTO.post
                    letterlistAdapter.notifyDataSetChanged()
                }
            } else {
            }
        }


        //// 상단바 이름
        // api 연결
        val familyService = FamilyService()
        familyService.getFamilyList(loadedToken) { familyDTO ->
            if (familyDTO != null) {
                if (familyDTO.isSuccess.toString() == "true") {
                    binding.userTitleTv2.text = familyDTO.result.me.nickname
                }
            }
        }


        //// 편지 보기(팝업)
        val onClickListener = object: LetterListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
                val selectedPost = letterlistAdapter.letters?.get(pos)
                val postPopupFragment = LetterPopupFragment(requireContext()) {}
                selectedPost?.let { postPopupFragment.show(it) }
            }
        }
        letterlistAdapter.setOnItemClickListener(onClickListener)

        binding.rvLetterlist.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvLetterlist.adapter = letterlistAdapter

        return binding.root
    }


    // 달력 세팅
    private fun setWeek(startOfWeek: LocalDate) {
        val nearestMonday = startOfWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val yearMonth = YearMonth.from(nearestMonday)
        binding.selectDateTv2.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"

        for (i in 1..7) {
            val currentDateForDay = nearestMonday.plusDays(i.toLong() - 1)
            val dateTextView = when (i) {
                1 -> binding.letterDate1
                2 -> binding.letterDate2
                3 -> binding.letterDate3
                4 -> binding.letterDate4
                5 -> binding.letterDate5
                6 -> binding.letterDate6
                7 -> binding.letterDate7
                else -> null
            }
            val dayTextView = when (i) {
                1 -> binding.letterDay1
                2 -> binding.letterDay2
                3 -> binding.letterDay3
                4 -> binding.letterDay4
                5 -> binding.letterDay5
                6 -> binding.letterDay6
                7 -> binding.letterDay7
                else -> null
            }

            dateTextView?.text = formatDate(currentDateForDay)

            // 선택한 날짜에 동그라미 표시
            val isSelectedDay = startOfWeek <= selectedDay && selectedDay <= startOfWeek.plusDays(6)

            if (isSelectedDay) {
                if (selectedDay == currentDateForDay) {
                    binding.selectCircle.visibility = View.VISIBLE
                    dateTextView?.viewTreeObserver?.addOnPreDrawListener(object :
                        ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            dateTextView.viewTreeObserver.removeOnPreDrawListener(this)
                            val dateTextViewX = dateTextView.x
                            val dateTextViewWidth = dateTextView.width.toFloat()
                            val circleWidth = binding.selectCircle.width.toFloat()
                            binding.selectCircle.x =
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
                binding.selectCircle.visibility = View.GONE
            }

            // 날짜 선택 시
            dateTextView?.setOnClickListener {
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val selectedDateText = currentDateForDay.format(dateFormat)

                if (currentDateForDay == LocalDate.now()) {
                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, PostboxFragment())
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                }
                else {
                    val letterListFragment = LetterListFragment()

                    val bundle = Bundle()
                    bundle.putString("selectedDate", selectedDateText)
                    letterListFragment.arguments = bundle

                    // 날짜별 편지 확인 페이지로 이동
                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, letterListFragment)
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

    // 토큰 불러오기
    private fun loadJwt(): String {
        val spf = requireActivity().getSharedPreferences("myToken", AppCompatActivity.MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }

}