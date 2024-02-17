package com.umc.anddeul.start.terms

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityTermsBinding
import com.umc.anddeul.invite.InviteStartActivity
import com.umc.anddeul.start.signin.SignupActivity

class TermsActivity: AppCompatActivity()  {
    private lateinit var binding: ActivityTermsBinding
    private var termsCheckedCnt : Int = 0
    private var termsOpenedCnt : Int = 0
    private var termsChecked1 : Boolean = false
    private var termsChecked2 : Boolean = false
    private var termsOpen1 : Boolean = false
    private var termsOpen2 : Boolean = false
    private var termsOpen3 : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTermsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 뒤로 가기
        binding.termsBackBtn.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }

        //// 약관 내용
        // 첫번째
        val termText1 = """
            
            <1. 개인정보 처리 약관 동의>
            
            안뜰은 정보주체의 자유와 권리 보호를 위해 「개인정보 보호법」 및 관계 법령이 정한 바를 준수하여, 적법하게 개인정보를 처리하고 안전하게 관리하고 있습니다. 이에 「개인정보 보호법」 제30조에 따라 정보주체에게 개인정보 처리에 관한 절차 및 기준을 안내하고, 이와 관련한 고충을 신속하고 원활하게 처리할 수 있도록 하기 위하여 다음과 같이 개인정보 처리방침을 수립·공개합니다.

                개인정보의 처리목적 
                안뜰 은(는) 다음의 목적을 위하여 개인정보를 처리합니다. 
                처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 
                이용되지 않으며, 이용 목적이 변경되는 경우에는 
                「개인정보 보호법」 제18조에 따라 별도의 동의를 받는 등 
                필요한 조치를 이행할 예정입니다. 

                1. 회원 가입 및 관리 
                회원 가입의사 확인, 회원제 서비스 제공에 따른 
                본인 식별·인증, 회원자격 유지·관리, 서비스 부정이용 방지, 
                만 14세 미만 아동의 개인정보 처리 시 법정대리인의 
                동의여부 확인, 각종 고지·통지, 고충처리 목적으로 
                개인정보를 처리합니다

                수집 목적: 안뜰 서비스 제공을 위한 회원가입 및 이용자 
                식별, 로그인 수단 변경에 따른 신청 및 관련 사항 안내, 
                소셜(SNS) 간편로그인 회원가입(카카오, 구글)

                수집되는 항목: 닉네임, 생년월일, 가족 초대 코드, 
                (카카오)이메일/닉네임/ 프로필 사진/


                2. 서비스 제공
                서비스 제공, 계약서·청구서 발송, 콘텐츠 제공, 
                맞춤서비스 제공, 본인인증, 연령인증, 요금 결제·정산, 
                채권추심의 목적으로 개인정보를 처리합니다


                개인정보의 처리 및 보유기간
                ① 안뜰은 법령에 따른 개인정보 보유·이용기간 또는 
                정보주체로부터 개인정보를 수집 시에 동의 받은 개인정보 
                보유·이용기간 내에서 개인정보를 처리·보유합니다. 
                ② 각각의 개인정보 처리 및 보유 기간은 다음과 같습니다. 
                    1. 홈페이지 회원 가입 및 관리 : 사업자/단체 홈페이지 
                    탈퇴 시까지 
                    다만, 다음의 사유에 해당하는 경우에는 
                    해당 사유 종료 시까지 
                        1) 관계 법령 위반에 따른 수사·조사 등이 진행 중인 
                        경우에는 해당 수사·조사 종료 시까지
                        2) 홈페이지 이용에 따른 채권·채무관계 잔존 시에는 
                        해당 채권·채무관계 정산 시까지

        """.trimIndent()
        val spannableString1 = SpannableString(termText1)

        val boldText1_1 = "개인정보의 처리목적"
        val startIndex1_1 = termText1.indexOf(boldText1_1)
        val endIndex1_1 = startIndex1_1 + boldText1_1.length

        val boldText1_2 = "개인정보의 처리 및 보유기간"
        val startIndex1_2 = termText1.indexOf(boldText1_2)
        val endIndex1_2 = startIndex1_2 + boldText1_2.length

        spannableString1.setSpan(StyleSpan(Typeface.BOLD), startIndex1_1, endIndex1_1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString1.setSpan(StyleSpan(Typeface.BOLD), startIndex1_2, endIndex1_2, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.termsText1.text = spannableString1

        // 첫번째
        val termText2 = """
            
            <2. 데이터 정책 동의>
            
            본 정책에는 저희가 수집하는 정보의 종류가 나열되어 있습니다. 주요 사항은 다음과 같습니다. 
            저희가 수집하는 정보:
            •회원님이 제품에 가입하고 프로필을 만들 때 저희에게 제공하는 정보(예: 이메일 주소 또는 전화번호)
            •저희 제품에서 회원님이 하는 활동. 여기에는 회원님이 클릭하거나 이모지를 표시한 항목, 회원님의 게시물 및 사진, 회원님이 보내는 메시지가 포함됩니다.
            저희는 특정 정보를 다음 대상과 공유합니다.
            저희 제품에 광고를 게재하는 광고주
            저희가 고객 서비스 제공, 설문 조사 실시 등을 위해 고용한 비즈니스
            혁신, 기술 발전 또는 사람들의 안전 개선 등을 위해 해당 정보를 이용하는 연구자
            저희는 회원님의 정보를 판매하지 않으며 앞으로도 판매하지 않을 것입니다.
            •회원님은 저희가 보유하고 있는 회원님에 대한 정보를 보고 다운로드할 수 있는 권리가 있습니다.
            
            저희는 기능 또는 서비스를 제공하기 위해 필요한 경우 정보를 보유합니다.
            •하지만 회원님은 저희에게 회원님의 정보를 삭제하도록 요청할 수 있습니다.
            •저희는 법적 이유 등 다른 이유로 보유해야 하는 경우가 아니면 해당 정보를 삭제합니다. 해당 정보의 내용은 아래를 참고해주세요.
            -회원님이 만드는 콘텐츠(예: 게시물, 반응)
            -회원님이 저희의 카메라 기능 또는 회원님의 카메라 롤 설정 또는 저희의 음성 지원 기능을 통해 제공하는 콘텐츠
            -관련 법률에 따라 메시지의 콘텐츠를 비롯하여 회원님이 주고받는 메시지
            -관련 법률에 따라 콘텐츠 및 메시지에 관한 메타데이터.
            -회원님이 보거나 상호작용하는 콘텐츠의 유형 및 상호작용 방식
            -회원님이 사용하는 앱과 기능 및 회원님이 해당 앱과 기능에서 취하는 행동
            -회원님이 저희 제품을 사용하는 활동 시간, 빈도, 기간
            -기기 특성 및 기기 소프트웨어
            -회원님의 기기를 다른 이용자의 기기와 구분하는 식별자
            -기기 신호
            -회원님이 회원님의 기기 설정을 통해 공유한 정보
            -위치 관련 정보
            -IP 주소를 비롯하여 회원님의 기기를 연결한 네트워크 관련 정보
            -쿠키 및 유사 기술에서 얻은 정보

        """.trimIndent()

        binding.termsText2.text = termText2

        //// 약관 펼치기 접기

        // 첫 번째 약관
        binding.terms1.setOnClickListener {
            if (!termsOpen1) {
                termsOpen1 = true
                binding.termChild1.visibility = View.VISIBLE
                binding.termMore1.visibility = View.GONE
                binding.termClose1.visibility = View.VISIBLE
                termsOpenedCnt += 1
            }
            else{
                termsOpen1 = false
                binding.termChild1.visibility = View.GONE
                binding.termMore1.visibility = View.VISIBLE
                binding.termClose1.visibility = View.GONE
                termsOpenedCnt -= 1
            }
            openCount()
        }

        // 두 번째 약관
        binding.terms2.setOnClickListener {
            if (!termsOpen2) {
                termsOpen2 = true
                binding.termChild2.visibility = View.VISIBLE
                binding.termMore2.visibility = View.GONE
                binding.termClose2.visibility = View.VISIBLE
                termsOpenedCnt += 1
            }
            else{
                termsOpen2 = false
                binding.termChild2.visibility = View.GONE
                binding.termMore2.visibility = View.VISIBLE
                binding.termClose2.visibility = View.GONE
                termsOpenedCnt -= 1
            }
            openCount()
        }

        // 세 번째 약관
        binding.terms3.setOnClickListener {
            if (!termsOpen3) {
                termsOpen3 = true
                binding.termChild3.visibility = View.VISIBLE
                binding.termMore3.visibility = View.GONE
                binding.termClose3.visibility = View.VISIBLE
                termsOpenedCnt += 1
            }
            else{
                termsOpen3 = false
                binding.termChild3.visibility = View.GONE
                binding.termMore3.visibility = View.VISIBLE
                binding.termClose3.visibility = View.GONE
                termsOpenedCnt -= 1
            }
            openCount()
        }

        //// 약관 체크 버튼

        // 전체 동의
        binding.termTotalNotCheckedBtn.setOnClickListener {
            binding.termChecked1.visibility = View.VISIBLE
            binding.termChecked2.visibility = View.VISIBLE
            binding.termChecked3.visibility = View.VISIBLE

            binding.termNotChecked1.visibility = View.GONE
            binding.termNotChecked2.visibility = View.GONE
            binding.termNotChecked3.visibility = View.GONE

            termsCheckedCnt = 3
            termsChecked1 = true
            termsChecked2 = true
            isAllChecked()
        }

        // 전체 동의 취소
        binding.termTotalCheckedBtn.setOnClickListener {
            binding.termChecked1.visibility = View.GONE
            binding.termChecked2.visibility = View.GONE
            binding.termChecked3.visibility = View.GONE

            binding.termNotChecked1.visibility = View.VISIBLE
            binding.termNotChecked2.visibility = View.VISIBLE
            binding.termNotChecked3.visibility = View.VISIBLE

            termsCheckedCnt = 0
            termsChecked1 = false
            termsChecked2 = false
            isAllChecked()
        }

        // 첫번째 약관 동의
        binding.termNotChecked1.setOnClickListener {
            binding.termChecked1.visibility = View.VISIBLE
            binding.termNotChecked1.visibility = View.GONE

            termsCheckedCnt += 1
            termsChecked1 = true
            isAllChecked()
        }

        // 첫번째 약관 동의 취소
        binding.termChecked1.setOnClickListener {
            binding.termChecked1.visibility = View.GONE
            binding.termNotChecked1.visibility = View.VISIBLE

            termsCheckedCnt -= 1
            termsChecked1 = false
            isAllChecked()
        }

        // 두번째 약관 동의
        binding.termNotChecked2.setOnClickListener {
            binding.termChecked2.visibility = View.VISIBLE
            binding.termNotChecked2.visibility = View.GONE

            termsCheckedCnt += 1
            termsChecked2 = true
            isAllChecked()
        }

        // 두번째 약관 동의 취소
        binding.termChecked2.setOnClickListener {
            binding.termChecked2.visibility = View.GONE
            binding.termNotChecked2.visibility = View.VISIBLE

            termsCheckedCnt -= 1
            termsChecked2 = false
            isAllChecked()
        }

        // 세번째 약관 동의
        binding.termNotChecked3.setOnClickListener {
            binding.termChecked3.visibility = View.VISIBLE
            binding.termNotChecked3.visibility = View.GONE

            termsCheckedCnt += 1
            isAllChecked()
        }

        // 세번째 약관 동의 취소
        binding.termChecked3.setOnClickListener {
            binding.termChecked3.visibility = View.GONE
            binding.termNotChecked3.visibility = View.VISIBLE

            termsCheckedCnt -= 1
            isAllChecked()
        }
        

        //// 동의 완료 버튼
        binding.termsAgreeBtn.setOnClickListener {
            if(termsChecked1 && termsChecked2) {
                val inviteIntent = Intent(this, InviteStartActivity::class.java)
                startActivity(inviteIntent)
            }
        }

    }
    
    // 전체 체크 여부 확인 함수
    private fun isAllChecked(){
        // 전체 체크되었을 때
        if (termsCheckedCnt == 3){
            binding.termTotalNotCheckedBtn.visibility = View.GONE
            binding.termTotalCheckedBtn.visibility = View.VISIBLE
        }

        // 전체 체크되지 않았을 때
        if (termsCheckedCnt != 3){
            binding.termTotalNotCheckedBtn.visibility = View.VISIBLE
            binding.termTotalCheckedBtn.visibility = View.GONE
        }
    }

    // 약관 펼침 여부 확인 함수
    private fun openCount(){
        // 전체 체크되었을 때
        if (termsOpenedCnt == 0){
            val imageView = binding.termsAgreeBtn
            val layoutParams = imageView.layoutParams as ViewGroup.MarginLayoutParams
            val marginInDp = 229f
            val marginInPixel = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginInDp,
                resources.displayMetrics
            ).toInt()

            layoutParams.topMargin = marginInPixel

            binding.termsAgreeBtn.layoutParams = layoutParams
        }
        else if (termsOpenedCnt == 1){
            val imageView = binding.termsAgreeBtn
            val layoutParams = imageView.layoutParams as ViewGroup.MarginLayoutParams
            val marginInDp = 59f
            val marginInPixel = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginInDp,
                resources.displayMetrics
            ).toInt()

            layoutParams.topMargin = marginInPixel

            binding.termsAgreeBtn.layoutParams = layoutParams
        }
        else if (termsOpenedCnt == 2){
            val imageView = binding.termsAgreeBtn
            val layoutParams = imageView.layoutParams as ViewGroup.MarginLayoutParams
            val marginInDp = 35f
            val marginInPixel = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginInDp,
                resources.displayMetrics
            ).toInt()

            layoutParams.topMargin = marginInPixel

            binding.termsAgreeBtn.layoutParams = layoutParams

        }
        else{
            val imageView = binding.termsAgreeBtn
            val layoutParams = imageView.layoutParams as ViewGroup.MarginLayoutParams
            val marginInDp = 35f
            val marginInPixel = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginInDp,
                resources.displayMetrics
            ).toInt()

            layoutParams.topMargin = marginInPixel

            binding.termsAgreeBtn.layoutParams = layoutParams

        }
    }
}