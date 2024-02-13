package com.umc.anddeul.postbox

import FamilyAdapter
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
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
import com.umc.anddeul.databinding.FragmentPostboxBinding
import com.umc.anddeul.postbox.model.FamilyDTO
import com.umc.anddeul.postbox.service.MailService
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class PostboxFragment : Fragment() {
    private lateinit var binding: FragmentPostboxBinding
    private lateinit var postAdapter: LetterAdapter
    private lateinit var familyAdapter: FamilyAdapter
    private var currentStartOfWeek: LocalDate = LocalDate.now()
    private var letterType: String = ""
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostboxBinding.inflate(inflater, container, false)

        //// 화분 키우기 페이지로 이동
        binding.gotoPotBtn.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, PotFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }


        //// 달력

        // 초기 세팅
        setWeek(currentStartOfWeek)

        // 저번주
        binding.beforeBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.minusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.selectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(currentStartOfWeek)
        }
        
        // 다음주
        binding.afterBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.plusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.selectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            setWeek(currentStartOfWeek)
        }

        //// 편지 리스트
        postAdapter = LetterAdapter()

        // api 연결
        val currentDate = LocalDate.now()
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = currentDate.format(dateFormat)
//        val today = "2024-02-08"
        Log.d("오늘날짜", currentDate.format(dateFormat))
        val loadedToken = loadJwt() // jwt토큰
        val mailService = MailService()
        mailService.todayMail(loadedToken, today.toString()) { mailDTO ->
            if (mailDTO != null) {
                if (mailDTO.isSuccess.toString() == "true") {
                    postAdapter.letters = mailDTO.post
                    postAdapter.notifyDataSetChanged()
                }
            } else {
            }
        }
//
//        // 테스트용 더미 데이터
//        val dummyPosts = listOf(
//            Letter(1, "아티", 0, "편지 내용을 보여준다. 예) 전염병이 발생 초기와 비슷하게 접촉 차단, 추적 관찰로 추가 감염을 막는 데 집중하고 사태가 커지지 않도록 방역, 격리시설 수용, 치료 등이 이루어질 것이다. 물론 좀비는 공격성과 높은 전염성을 가지기에 중무장한 인원이 투입될 것이며 인권 문제를 감안해 최대한 죽이지 않고 생포하려고 시도하겠지만 그러면서도 언제든지 반격을 전제로 깔고 행동할 것이다. (나무위키 좀비 검색 내용)"),
//            Letter(2, "도라", 0, "어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구"),
//            Letter(3, "지나", 0, "어쩌구저쩌구"),
//            Letter(4, "율", 1, "음성 메세지가 도착했습니다."),
//            Letter(5, "도도", 1, "음성 메세지가 도착했습니다."),
//            Letter(6, "훈", 1, "음성 메세지가 도착했습니다."),
//            Letter(7, "빈온", 0, "어쩌구저쩌구"),
//            Letter(8, "세흐", 0, "어쩌구저쩌구"),
//        )
//
//        postAdapter.letters = dummyPosts


        //// 편지 보기(팝업)
        val onClickListener = object: LetterAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
                val postPopupFragment = LetterPopupFragment(requireContext())
//                postPopupFragment.show(dummyPosts[pos])
            }
        }
        postAdapter.setOnItemClickListener(onClickListener)

        binding.rvLetters.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvLetters.adapter = postAdapter


        //// 가족 리스트
        familyAdapter = FamilyAdapter()

        // 테스트용 더미 데이터
        val dummyFams = listOf(
            FamilyDTO(1, "아티"),
            FamilyDTO(2, "도라"),
            FamilyDTO(3, "지나"),
            FamilyDTO(4, "율"),
            FamilyDTO(5, "도도"),
            FamilyDTO(6, "훈"),
            FamilyDTO(7, "빈온"),
            FamilyDTO(8, "세흐"),
        )

        familyAdapter.families = dummyFams
        binding.sFamily.adapter = familyAdapter


        //// 편지 작성 (음성)
        binding.voiceIv.setOnClickListener{
            if (letterType != "record") {   // 녹음을 하지 않았을 때
                if (binding.letterEt.text.toString().isEmpty()) {   // 작성된 텍스트 없을 때
                    val recordPopupFragment = RecordPopupFragment(requireContext())
                    recordPopupFragment.show()
                } else {    // 작성된 텍스트 있을 때
                    val dialogFragment = DialogLetterFragment(requireContext())
                    dialogFragment.show("voice")
                }
            }
        }

        // 작성한 음성 편지 있을 때
        val recordFilePath = arguments?.getString("recordFilePath")
        if (recordFilePath?.isNotBlank() == true){
            letterType = "record"
            binding.letterEt.visibility = View.GONE
            binding.recordInfo1.visibility = View.VISIBLE
            binding.recordInfo2.visibility = View.VISIBLE
            binding.recordInfo3.visibility = View.VISIBLE
            binding.recordInfo4.visibility = View.VISIBLE
        }

        // 작성한 음성 실행
        binding.recordInfo4.setOnClickListener {
            binding.recordInfo4.visibility = View.GONE
            val myUri: Uri = Uri.parse("file://$recordFilePath")
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(requireContext(), myUri)
                prepare()
                start()
                Toast.makeText(context, "음성이 시작되었습니다.", Toast.LENGTH_SHORT).show()

                // 음성 실행 완료 시 (다시 재생 버튼 등장)
                setOnCompletionListener {
                    binding.recordInfo4.visibility = View.VISIBLE
                    mediaPlayer?.reset()
                }
            }
        }

        // 작성한 음성 녹음 삭제
        binding.recordInfo3.setOnClickListener {
            val recordDeleteFragment = DialogRecordDeleteFragment(requireContext(), this)
            recordDeleteFragment.show()

        }



        //// 편지 작성 (텍스트)
        
        
        //// 편지 보내기
        binding.mailIv.setOnClickListener{
            // 편지 보내는 기능 추가
            if (letterType == "text") { // 텍스트일 때
                //텍스트 초기화
                binding.letterEt.setText("")
                letterType = ""
            }
            else if (letterType == "record"){   // 녹음일 때
                binding.recordInfo1.visibility = View.GONE
                binding.recordInfo2.visibility = View.GONE
                binding.recordInfo3.visibility = View.GONE
                binding.recordInfo4.visibility = View.GONE
                letterType = ""
            }
        }

        return binding.root
    }

    // 달력 세팅
    private fun setWeek(startOfWeek: LocalDate) {
        val nearestMonday = startOfWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val yearMonth = YearMonth.from(nearestMonday)
        binding.selectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"

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
                    binding.todayCircle.visibility = View.VISIBLE
                    dateTextView?.viewTreeObserver?.addOnPreDrawListener(object :
                        ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            dateTextView.viewTreeObserver.removeOnPreDrawListener(this)
                            val dateTextViewX = dateTextView.x
                            val dateTextViewWidth = dateTextView.width.toFloat()
                            val circleWidth = binding.todayCircle.width.toFloat()
                            binding.todayCircle.x =
                                dateTextViewX + (dateTextViewWidth - circleWidth) / 2
                            return true
                        }
                    })
                    // 색상&폰트 적용
                    dateTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    dayTextView?.setTextColor(Color.parseColor("#1D1D1D"))
                    dayTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                    dateTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                }
            } else {
                // 색상&폰트 적용
                dateTextView?.setTextColor(Color.parseColor("#666666"))
                dayTextView?.setTextColor(Color.parseColor("#666666"))
                dayTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                dateTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                binding.todayCircle.visibility = View.GONE
            }

            // 날짜 선택 시
            dateTextView?.setOnClickListener {
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val selectedDateText = currentDateForDay.format(dateFormat)

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

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        return date.format(formatter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun deleteRecording(){
        letterType = ""
        binding.recordInfo1.visibility = View.GONE
        binding.recordInfo2.visibility = View.GONE
        binding.recordInfo3.visibility = View.GONE
        binding.recordInfo4.visibility = View.GONE
    }

    // 토큰 불러오기
    private fun loadJwt(): String {
        val spf = requireActivity().getSharedPreferences("myToken", AppCompatActivity.MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }
}
