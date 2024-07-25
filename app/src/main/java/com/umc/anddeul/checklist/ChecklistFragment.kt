package com.umc.anddeul.checklist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.anddeul.R
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.checklist.model.Root
import com.umc.anddeul.checklist.network.ChecklistInterface
import com.umc.anddeul.checklist.service.ChecklistService
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.common.toast.AnddeulToast
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentChecklistBinding
import com.umc.anddeul.home.PermissionDialog
import com.umc.anddeul.home.model.UserProfileDTO
import com.umc.anddeul.home.model.UserProfileData
import com.umc.anddeul.home.network.UserProfileInterface
import com.umc.anddeul.start.StartActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Date
import java.util.Locale

class ChecklistFragment : Fragment() {
    lateinit var binding: FragmentChecklistBinding
    var token: String? = null
    lateinit var retrofit: Retrofit

    private var currentStartOfWeek: LocalDate = LocalDate.now()
    lateinit var selectedDateText: String
    lateinit var checklistRVAdapter: ChecklistRVAdapter
    val today = LocalDate.now()
    private var selectedDay: LocalDate = LocalDate.now()
    private var currentCheckId: Int? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentCheckId?.let { ChecklistService(requireActivity()).imgApi(it, uri) }
        } else {
            // 선택한 이미지가 없을 경우
        }
    }

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                // 선택한 이미지의 경로 데이터를 관리하는 Uri 객체를 추출
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let { uri ->
                    // 선택한 이미지가 있을 경우
                    currentCheckId?.let { checkId ->
                        ChecklistService(requireActivity()).imgApi(checkId, uri)
                    }
                } ?: run {
                    // 선택한 이미지가 없을 경우 처리
                }
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startAlbumLauncher()
            } else {
                val permissionDialog = PermissionDialog()
                permissionDialog.isCancelable = false
                permissionDialog.show(parentFragmentManager, "permission dialog")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChecklistBinding.inflate(inflater, container, false)

        //리사이클러뷰 연결
        checklistRVAdapter = ChecklistRVAdapter(requireContext()) {
            currentCheckId = it
            if (isPhotoPickerAvailable()) {
                startPhotoPicker()
            } else {
                checkPermission()
            }
        }
        binding.checklistRecylerView.adapter = checklistRVAdapter
        binding.checklistRecylerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //spf 받아오기
        token = TokenManager.getToken()
        val spfMyId = requireActivity().getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val myId = spfMyId.getString("myId", "")

        retrofit = RetrofitManager.getRetrofitInstance()

        val service = retrofit.create(ChecklistInterface::class.java)
        val serviceUser = retrofit.create(UserProfileInterface::class.java)

        val profileCall: Call<UserProfileDTO> = serviceUser.getUserProfile(myId!!)
        profileCall.enqueue(object : Callback<UserProfileDTO> {
            override fun onResponse(
                call: Call<UserProfileDTO>,
                response: Response<UserProfileDTO>
            ) {
                if (response.isSuccessful) {
                    val root: UserProfileDTO? = response.body()
                    val result: UserProfileData? = root?.result

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

                if (response.code() == 401) {
                    val startIntent = Intent(context, StartActivity::class.java)
                    context!!.startActivity(startIntent)
                }
            }

            override fun onFailure(call: Call<UserProfileDTO>, t: Throwable) {
                AnddeulErrorToast.createToast(context!!, "서버 연결이 불안정합니다")?.show()
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
            } else {
                setSelectedWeek(selectedDay, service, myId!!)
            }
        }

        // 다음주
        binding.checkliAfterBtn.setOnClickListener {
            if (selectedDay < today) {
                val tempDay = selectedDay.plusWeeks(1)
                var tempMonday = tempDay

                when (tempDay.dayOfWeek.toString()) {
                    "TUESDAY" -> {
                        tempMonday = tempDay.minusDays(1)
                    }

                    "WEDNESDAY" -> {
                        tempMonday = tempDay.minusDays(2)
                    }

                    "THURSDAY" -> {
                        tempMonday = tempDay.minusDays(3)
                    }

                    "FRIDAY" -> {
                        tempMonday = tempDay.minusDays(4)
                    }

                    "SATURDAY" -> {
                        tempMonday = tempDay.minusDays(5)
                    }

                    "SUNDAY" -> {
                        tempMonday = tempDay.minusDays(6)
                    }
                }

                if (tempDay == today) {
                    setWeek(tempDay, service, myId)
                } else {
                    if (tempMonday <= today) {
                        selectedDay = tempDay
                        setSelectedWeek(selectedDay, service, myId)
                    }
                }
                val yearMonth = YearMonth.from(selectedDay)
                binding.checkliSelectDateTv.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            }
        }

        selectedDateText = SimpleDateFormat("yyyy-MM-dd").format(Date())

        return binding.root
    }

    fun readApi(service: ChecklistInterface, myId: String) {
        val readCall: Call<Root> = service.getChecklist(
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
                    val root: Root? = response.body()
                    val result: List<Checklist>? = root?.checklist

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

                if (response.code() == 401) {
                    val startIntent = Intent(context, StartActivity::class.java)
                    context!!.startActivity(startIntent)
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
    private fun setWeek(startOfWeek: LocalDate, service: ChecklistInterface, spfMyId: String) {
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
                    dateTextView?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    dayTextView?.setTextColor(Color.parseColor("#1D1D1D"))
                    dayTextView?.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                    dateTextView?.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                } else {
                    dateTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                    dateTextView?.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
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
                } else {
                    setSelectedWeek(selectedDay, service, spfMyId)
                }
            }
        }
        readApi(service, spfMyId!!)
    }

    //내가 선택한 날짜로 넘어가기 및 동그라미
    private fun setSelectedWeek(
        startOfWeek: LocalDate,
        service: ChecklistInterface,
        spfMyId: String
    ) {
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
                    dateTextView?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    dayTextView?.setTextColor(Color.parseColor("#1D1D1D"))
                    dayTextView?.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                    dateTextView?.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_bold)
                } else {
                    dateTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.setTextColor(Color.parseColor("#666666"))
                    dayTextView?.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
                    dateTextView?.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.font_pretendard_regular)
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
                } else {
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

    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
        } else {
            false
        }
    }

    private fun checkPermission() {
        val permissionReadExternal = android.Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionReadExternalGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionReadExternal
        ) == PackageManager.PERMISSION_GRANTED

        // 포토피커를 사용하지 못하는 버전만 권한 확인 (SDK 30 미만)
        if (permissionReadExternalGranted) {
            startAlbumLauncher()
        } else {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    @SuppressLint("IntentReset")
    fun startAlbumLauncher() {
        val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        albumIntent.type = "image/*"
        albumLauncher.launch(albumIntent)
    }

    private fun startPhotoPicker() {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

}