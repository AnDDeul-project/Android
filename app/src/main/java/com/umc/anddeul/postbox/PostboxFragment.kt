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
import android.widget.AdapterView
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
import com.umc.anddeul.postbox.model.Family
import com.umc.anddeul.postbox.model.TextRequest
import com.umc.anddeul.postbox.model.VoiceRequest
import com.umc.anddeul.postbox.service.FamilyService
import com.umc.anddeul.postbox.service.MailService
import com.umc.anddeul.postbox.service.QuestionService
import com.umc.anddeul.postbox.service.TextService
import com.umc.anddeul.postbox.service.VoiceService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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
//        val currentDate = LocalDate.now()
//        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val today = currentDate.format(dateFormat)
////        val today = "2024-02-08"
//        Log.d("오늘날짜", currentDate.format(dateFormat))
        val loadedToken = loadJwt() // jwt토큰
//        val mailService = MailService()
//        mailService.todayMail(loadedToken, today.toString()) { mailDTO ->
//            if (mailDTO != null) {
//                if (mailDTO.isSuccess.toString() == "true") {
//                    postAdapter.letters = mailDTO.post
//                    postAdapter.notifyDataSetChanged()
//                }
//            } else {
//            }
//        }


        //// 편지 보기(팝업)
        val onClickListener = object: LetterAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
                val selectedPost = postAdapter.letters?.get(pos)
                val postPopupFragment = LetterPopupFragment(requireContext())
                selectedPost?.let { postPopupFragment.show(it) }
            }
        }
        postAdapter.setOnItemClickListener(onClickListener)

        binding.rvLetters.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvLetters.adapter = postAdapter


        //// 랜덤 질문
//        // api 연결
//        val questionService = QuestionService()
//        questionService.randomQuestion(loadedToken) { questionDTO ->
//            if (questionDTO != null) {
//                if (questionDTO.isSuccess.toString() == "true") {
//                    binding.randomQTv.text = questionDTO.question[0].content
//                }
//            }
//        }


        //// 가족 리스트
        familyAdapter = FamilyAdapter()

        // api 연결
        val familyService = FamilyService()
        familyService.getFamilyList(loadedToken) { familyDTO ->
            if (familyDTO != null) {
                if (familyDTO.isSuccess.toString() == "true") {
                    familyAdapter.families = familyDTO.result.family
                    binding.sFamily.adapter = familyAdapter
                    binding.userTitleTv.text = familyDTO.result.me.nickname
                }
            }
        }

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

        
        //// 편지 보내기
        binding.mailIv.setOnClickListener{

            // 선택한 가족 확인
            val selectedPosition = binding.sFamily.selectedItemPosition
            if (selectedPosition != AdapterView.INVALID_POSITION) {
                val selectedFamily: Family? = familyAdapter.getItem(selectedPosition) as? Family
                selectedFamily?.let {

                    // 편지 보내는 기능 추가
                    if (letterType == "record"){   // 녹음일 때
                        val myUri: Uri = Uri.parse("file://$recordFilePath")
                        val file = File(myUri.path)
                        val requestFile = file.asRequestBody("audio/*".toMediaTypeOrNull())
                        val recordPart = MultipartBody.Part.createFormData("record", file.name, requestFile)
                        val request = VoiceRequest(it.snsId, binding.randomQTv.text.toString(), recordPart)
                        // api 연결
                        val voiceService = VoiceService()
                        val memberRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), it.snsId)
                        val questionRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.randomQTv.text.toString())

                        voiceService.sendVoice(loadedToken, memberRequestBody, questionRequestBody, recordPart) { voiceDTO ->
                            Log.d("확2", voiceDTO.toString())
                            if (voiceDTO != null) {
                                if (voiceDTO.isSuccess.toString() == "true") {
                                    binding.recordInfo1.visibility = View.GONE
                                    binding.recordInfo2.visibility = View.GONE
                                    binding.recordInfo3.visibility = View.GONE
                                    binding.recordInfo4.visibility = View.GONE
                                    letterType = ""
                                }
                            }
                        }
                    }
                    else {  // 텍스트일 때
                        if (binding.letterEt.text.isNotBlank()) {   // 텍스트가 있을 때
                            val request = TextRequest(it.snsId, binding.randomQTv.text.toString(),binding.letterEt.text.toString())
                            // api 연결
                            val textService = TextService()
                            textService.sendText(loadedToken, request) { textDTO ->
                                if (textDTO != null) {
                                    if (textDTO.isSuccess.toString() == "true") {
                                        //텍스트 초기화
                                        binding.letterEt.setText("")
                                        letterType = ""
                                    }
                                }
                            }
                        }
                    }
                }
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
                1 -> binding.postDate1
                2 -> binding.postDate2
                3 -> binding.postDate3
                4 -> binding.postDate4
                5 -> binding.postDate5
                6 -> binding.postDate6
                7 -> binding.postDate7
                else -> null
            }
            val dayTextView = when (i) {
                1 -> binding.postDay1
                2 -> binding.postDay2
                3 -> binding.postDay3
                4 -> binding.postDay4
                5 -> binding.postDay5
                6 -> binding.postDay6
                7 -> binding.postDay7
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
