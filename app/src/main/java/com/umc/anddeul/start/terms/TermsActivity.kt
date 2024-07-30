package com.umc.anddeul.start.terms

import android.content.Intent
import android.os.Bundle
import android.view.View
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
    private var termsChecked3 : Boolean = false
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
            
            제1장 총칙

제1조 (목적) 이 약관은 “안뜰”이 모바일 기기를 통해 제공하는 온라인 SNS 서비스 및 이에 부수하는 네트워크, 웹사이트, 기타 서비스(이하 “서비스”라 합니다)의 이용에 대한 회사와 서비스 이용자의 권리ㆍ의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.

제2조 (용어의 정의) ① 이 약관에서 사용하는 용어의 정의는 다음과 같습니다.
 1. “회사”라 함은 모바일 기기를 통하여 서비스를 제공하는 사업자를 의미합니다.
 2. “회원”이란 이 약관에 따라 이용계약을 체결하고, 회사가 제공하는 서비스를 이용하는 자를 의미합니다.
 3. “모바일 기기”란 콘텐츠를 다운로드 받거나 설치하여 사용할 수 있는 기기로서, 휴대폰, 스마트폰, 휴대정보단말기(PDA), 태블릿 등을 의미합니다. 
 4. “계정정보”란 회원의 회원번호와 외부계정정보, 기기정보, 별명, 프로필 사진, 친구목록 등 회원이 회사에 제공한 정보 등을 통칭합니다.
 5. “콘텐츠”란 모바일 기기로 이용할 수 있도록 회사가 서비스 제공과 관련하여 디지털 방식으로 제작한 유료 또는 무료의 내용물 일체를 의미합니다. 
 6.  “애플리케이션”이란 회사가 제공하는 서비스를 이용하기 위하여 모바일 기기를 통해 다운로드 받거나 설치하여 사용하는 프로그램 일체를 의미합니다.
 7. “SNS 서비스”라 함은 회사가 제공하는 서비스의 하나로서 회원이 모바일 기기에서 실행하는 사용자 간 상호작용 및 이에 부수하는 서비스를 의미합니다.
② 이 약관에서 사용하는 용어의 정의는 본 조 제1항에서 정하는 것을 제외하고는 관계법령 및 서비스별 정책에서 정하는 바에 의하며, 이에 정하지 아니한 것은 일반적인 상 관례에 따릅니다.

제3조 (회사정보 등의 제공) 회사는 다음 각 호의 사항을 회원이 알아보기 쉽도록  서비스 내에 표시합니다. 다만, 개인정보처리방침과 약관은 회원이 연결화면을 통하여 볼 수 있도록 할 수 있습니다.
 1. 상호 및 대표자의 성명 
 2. 개인정보처리방침 
 3. 서비스 이용약관 

제4조 (약관의 효력 및 변경) ① 회사는 이 약관의 내용을 회원이 알 수 있도록 서비스 내 또는 그 연결화면에 게시합니다. 이 경우 이 약관의 내용 중 서비스 중단, 청약철회, 환급, 계약 해제․해지, 회사의 면책사항 등과 같은 중요한 내용은 굵은 글씨, 색채, 부호 등으로 명확하게 표시하거나 별도의 연결화면 등을 통하여 회원이 알아보기 쉽게 처리합니다.
② 회사가 약관을 개정할 경우에는 적용일자 및 개정내용, 개정사유 등을 명시하여 최소한 그 적용일 7일 이전부터 서비스 내 또는 그 연결화면에 게시하여 회원에게 공지합니다. 다만, 변경된 내용이 회원에게 불리하거나 중대한 사항의 변경인 경우에는 그 적용일 30일 이전까지 본문과 같은 방법으로 공지하고 제28조 제1항의 방법으로 회원에게 통지합니다. 이 경우 개정 전 내용과 개정 후 내용을 명확하게 비교하여 회원이 알기 쉽도록 표시합니다.
③ 회사가 약관을 개정할 경우 개정약관 공지 후 개정약관의 적용에 대한 회원의 동의 여부를 확인합니다. 회사는 제2항의 공지 또는 통지를 할 경우 회원이 개정약관에 대해 동의 또는 거부의 의사표시를 하지 않으면 동의한 것으로 볼 수 있다는 내용도 함께 공지 또는 통지를 하며, 회원이 이 약관 시행일까지 거부의 의사표시를 하지 않는다면 개정약관에 동의한 것으로 볼 수 있습니다. 회원이 개정약관에 대해 동의하지 않는 경우 회사 또는 회원은 서비스 이용계약을 해지할 수 있습니다. 
④ 회사는 회원이 회사와 이 약관의 내용에 관하여 질의 및 응답을 할 수 있도록 조치를 취합니다.
⑤ 회사는 「전자상거래 등에서의 소비자보호에 관한 법률」, 「약관의 규제에 관한 법률」, 「정보통신망이용촉진 및 정보보호 등에 관한 법률」, 「콘텐츠산업진흥법」 등 관련 법령에 위배하지 않는 범위에서 이 약관을 개정할 수 있습니다.

제5조 (이용계약의 체결 및 적용) ① 이용계약은 회원이 되고자 하는 자(이하 “가입신청자”라 합니다.)가 이 약관의 내용에 대하여 동의를 한 다음 서비스 이용 신청을 하고, 회사가 그 신청에 대해서 승낙함으로써 체결됩니다. 
② 회사는 가입신청자의 신청에 대하여 승낙함을 원칙으로 합니다. 다만, 회사는 다음 각 호의 어느 하나에 해당하는 이용 신청에 대해서는 승낙을 거절할 수 있습니다.
 1. 이용신청서 내용을 허위로 기재하거나 이용신청 요건을 충족하지 못한 경우 
 2. 회사가 서비스를 제공하지 않은 국가에서 비정상적이거나 우회적인 방법을 통해 서비스를 이용하는 경우 
 3. 관련 법령에서 금지하는 행위를 할 목적으로 신청 하는 경우 
 4. 사회의 안녕과 질서 또는 미풍양속을 저해할 목적으로 신청한 경우 
 5. 부정한 용도로 서비스를 이용하고자 하는 경우 
 6. 영리를 추구할 목적으로 서비스를 이용하고자 하는 경우 
 7. 그 밖에 각 호에 준하는 사유로서 승낙이 부적절하다고 판단되는 경우 
③ 회사는 다음 각 호의 어느 하나에 해당하는 경우 그 사유가 해소될 때까지 승낙을 유보할 수 있습니다. 
 1. 회사의 설비에 여유가 없거나, 특정 모바일 기기의 지원이 어렵거나, 기술적 장애가 있는 경우 
 2. 서비스 상의 장애 또는 서비스 이용요금, 결제수단의 장애가 발생한 경우 
 3. 그 밖의 각 호에 준하는 사유로서 이용신청의 승낙이 어렵다고 판단되는 경우
 
제6조 (약관 외 준칙) 이 약관에서 정하지 아니한 사항과 이 약관의 해석에 관하여는 「전자상거래 등에서의 소비자보호에 관한 법률」,「약관의 규제에 관한 법률」,「게임산업진흥에 관한 법률」,「정보통신망이용촉진 및 정보보호 등에 관한 법률」,「콘텐츠산업진흥법」 등 관련 법령 또는 상 관례에 따릅니다.

제7조 (운영정책) ① 약관을 적용하기 위하여 필요한 사항과 약관에서 구체적 범위를 정하여 위임한 사항을 서비스 운영정책(이하 “운영정책”이라 합니다)으로 정할 수 있습니다. 
② 회사는 운영정책의 내용을 회원이 알 수 있도록 서비스 내 또는 그 연결화면에 게시합니다. 
③ 운영정책을 개정하는 경우에는 제4조 제2항의 절차에 따릅니다. 다만, 운영정책 개정내용이 다음 각 호의 어느 하나에 해당하는 경우에는 제2항의 방법으로 사전에 공지합니다. 
 1. 약관에서 구체적으로 범위를 정하여 위임한 사항을 개정하는 경우 
 2. 회원의 권리·의무와 관련 없는 사항을 개정하는 경우 
 3. 운영정책의 내용이 약관에서 정한 내용과 근본적으로 다르지 않고 회원이 예측할 수 있는 범위 내에서 운영정책을 개정하는 경우
 
 
 
             제4장 서비스 이용 및 이용제한

 제11조 (서비스의 제공) ① 회사는 제5조의 규정에 따라 이용계약이 완료된 회원에게 그 즉시 서비스를 이용할 수 있도록 합니다. 다만, 일부 서비스의 경우 회사의 필요에 따라 지정된 일자부터 서비스를 개시할 수 있습니다.
 ② 회사는 회원에게 서비스를 제공할 때 이 약관에 정하고 있는 서비스를 포함하여 기타 부가적인 서비스를 함께 제공할 수 있습니다.
 ③ 회사는 회원의 등급을 구분하고 이용시간, 이용횟수, 제공 서비스의 범위 등을 세분화하여 이용에 차등을 둘 수 있습니다.

 제12조 (서비스의 이용) ① 회사는 다음 각 호의 경우에는 서비스의 전부 또는 일부를 일시 정지할 수 있습니다. 이 경우 회사는 사전에 그 정지의 사유와 기간을 애플리케이션 초기화면이나 서비스 공지사항 등에 공지합니다. 다만, 사전에 공지할 수 없는 부득이한 사정이 있는 경우 사후에 공지할 수 있습니다.
  1. 시스템 정기점검, 서버의 증설 및 교체, 네트워크의 불안정 등의 시스템 운영상 필요한 경우
  2. 정전, 서비스 설비의 장애, 서비스 이용폭주, 기간통신사업자의 설비 보수 또는 점검 등으로 인하여 정상적인 서비스 제공이 불가능한 경우
  3. 전시, 사변, 천재지변 또는 이에 준하는 국가비상사태 등 회사가 통제할 수 없는 상황이 발생한 경우
 ② 네트워크를 통해 애플리케이션을 다운로드하거나 서비스를 이용하는 경우에는 가입한 이동통신사에서 정한 별도의 요금이 발생할 수 있습니다.
 ③ 다운로드하여 설치한 애플리케이션 또는 네트워크를 통해 이용하는 서비스의 경우에는 모바일 기기 또는 이동통신사의 특성에 맞도록 제공됩니다. 모바일 기기의 변경․번호 변경 또는 해외 로밍의 경우에는 콘텐츠의 전부 또는 일부의 이용이 불가능할 수 있으며, 이 경우 회사는 책임을 지지 않습니다.
 ④ 다운로드하여 설치한 애플리케이션 또는 네트워크를 통해 이용하는 서비스의 경우에는 백그라운드 작업이 진행될 수 있습니다. 이 경우 모바일 기기 또는 이동통신사의 특성에 맞도록 추가요금이 발생할 수 있으며 이와 관련하여 회사는 책임을 지지 않습니다.

 제13조 (서비스의 변경 및 중단) ① 회사는 원활한 서비스 제공을 위해 운영상 또는 기술상의 필요에 따라 서비스를 변경할 수 있으며, 변경 전에 해당 내용을 서비스 내에 공지합니다. 다만, 버그․오류 등의 수정이나 긴급 업데이트 등 부득이하게 변경할 필요가 있는 경우 또는 중대한 변경에 해당하지 않는 경우에는 사후에 공지할 수 있습니다.
 ② 회사는 영업양도․분할․합병 등에 따른 영업의 폐지, 제공의 계약만료, 당해 서비스의 현저한 수익 악화 등 경영상의 중대한 사유로 인해 서비스를 지속하기 어려운 경우에는 서비스 전부를 중단할 수 있습니다. 이 경우 중단일자 30일 이전까지 중단일자․중단사유․보상조건 등을 애플리케이션 초기화면 또는 그 연결화면을 통해 공지하고 제28조 제1항의 방법으로 회원에게 통지합니다.

 제14조 (정보의 수집 등) ① 회사는 회원간에 이루어지는 채팅 내용을 저장․보관할 수 있으며 이 정보는 회사만이 보유합니다. 회사는 회원간의 분쟁 조정, 민원 처리 또는 운영 질서의 유지를 위한 경우에 한하여, 제3자는 법령에 따라 권한이 부여된 경우에 한하여 이 정보를 열람할 수 있습니다. 
 ② 회사 또는 제3자가 제1항에 따라 채팅 정보를 열람할 경우 회사는 사전에 열람의 사유 및 범위를 해당 회원에게 고지합니다. 다만, 제10조 제1항에 따른 금지행위의 조사․처리․확인 또는 그 행위로 인한 피해 구제와 관련하여 이 정보를 열람해야 할 경우에는 사후에 고지할 수 있습니다.
 ③ 회사는 서비스의 원활하고 안정적인 운영 및 서비스 품질의 개선을 위하여 회원의 개인정보를 제외한 회원의 모바일 기기 정보(설정, 사양, 운영체제, 버전 등)를 수집 ‧ 활용할 수 있습니다.
 ④ 회사는 서비스 개선 및 회원 대상 서비스 소개 등을 위한 목적으로 회원에게 추가정보를 요청할 수 있습니다. 이 요청에 대해 회원은 승낙하거나 거절할 수 있으며, 회사가 이 요청을 할 경우에는 회원이 이 요청을 거절할 수 있다는 뜻을 함께 고지합니다.

 제15조 (광고의 제공) ① 회사는 서비스의 운영과 관련하여 서비스 내에 광고를 게재할 수 있습니다. 또한 수신에 동의한 회원에 한하여 전자우편, 문자서비스(LMS/SMS), 푸시메시지(Push Notification) 등의 방법으로 광고성 정보를 전송할 수 있습니다. 이 경우 회원은 언제든지 수신을 거절할 수 있으며, 회사는 회원의 수신 거절 시 광고성 정보를 발송하지 아니합니다.  
 ② 회사가 제공하는 서비스 중의 배너 또는 링크 등을 통해 타인이 제공하는 광고나 서비스에 연결될 수 있습니다. 
 ③ 제2항에 따라 타인이 제공하는 광고나 서비스에 연결될 경우 해당 영역에서 제공하는 서비스는 회사의 서비스 영역이 아니므로 회사가 신뢰성, 안정성 등을 보장하지 않으며, 그로 인한 회원의 손해에 대하여도 회사는 책임을 지지 않습니다. 다만, 회사가 고의 또는 중과실로 손해의 발생을 용이하게 하거나 손해 방지를 위한 조치를 취하지 아니한 경우에는 그러하지 아니합니다.

 제16조 (저작권 등의 귀속) ① 회사가 제작한 서비스 내의 콘텐츠에 대한 저작권과 기타 지적재산권은 회사에 귀속합니다.
 ② 회원은 회사가 제공하는 서비스를 이용하여 얻은 정보 중에서 회사 또는 제공업체에 지적재산권이 귀속된 정보를 회사 또는 제공업체의 사전 동의 없이 복제⋅전송 등의 방법(편집, 공표, 공연, 배포, 방송, 2차적 저작물 작성 등을 포함합니다. 이하 같습니다)에 의하여 영리목적으로 이용하거나 타인에게 이용하게 하여서는 안 됩니다.
 ③ 회원은 서비스 내에서 보여지거나 서비스와 관련하여 회원 또는 다른 이용자가 애플리케이션 또는 서비스를 통해 업로드 또는 전송하는 대화 텍스트를 포함한 커뮤니케이션, 이미지, 사운드 및 모든 자료 및 정보(이하 “이용자 콘텐츠”라 합니다.)에 대하여 회사가 다음과 같은 방법과 조건으로 이용하는 것을 허락합니다.
 1. 해당 이용자 콘텐츠를 이용, 편집 형식의 변경 및 기타 변형하는 것(공표, 복제, 공연, 전송, 배포, 방송, 2차적 저작물 작성 등 어떠한 형태로든 이용 가능하며, 이용기간과 지역에는 제한이 없음)
 2. 이용자 콘텐츠를 제작한 이용자의 사전 동의 없이 거래를 목적으로 이용자 콘텐츠를 판매, 대여, 양도 행위를 하지 않음
 ④ 서비스 내에서 보여지지 않고 서비스와 일체화되지 않은 회원의 이용자 콘텐츠(예컨대, 일반게시판 등에서의 게시물)에 대하여 회사는 회원의 명시적인 동의가 없이 이용하지 않으며, 회원은 언제든지 이러한 이용자 콘텐츠를 삭제할 수 있습니다.
 ⑤ 회사는 회원이 게시하거나 등록하는 서비스 내의 게시물에 대해 제10조 제1항에 따른 금지행위에 해당된다고 판단되는 경우에는 사전 통지 없이 이를 삭제 또는 이동하거나 그 등록을 거절할 수 있습니다. 
 ⑥ 회사가 운영하는 게시판 등에 게시된 정보로 인하여 법률상 이익이 침해된 회원은 회사에 해당 정보의 삭제 또는 반박 내용의 게재를 요청할 수 있습니다. 이 경우 회사는 신속하게 필요한 조치를 취하고 이를 신청인에게 통지합니다.
 ⑦ 이 조는 회사가 서비스를 운영하는 동안 유효하며, 회원 탈퇴 후에도 지속적으로 적용됩니다.
 

        """.trimIndent()

        binding.termsText1.text = termText1

        // 두번째
        val termText2 = """
            
            제2장 개인정보 관리

제8조 (개인정보의 보호 및 사용) ① 회사는 관련 법령이 정하는 바에 따라 회원의 개인정보를 보호하기 위해 노력하며, 개인정보의 보호 및 사용에 대해서는 관련 법령 및 회사의 개인정보처리방침에 따릅니다. 다만, 회사가 제공하는 서비스 이외의 링크된 서비스에서는 회사의 개인정보처리방침이 적용되지 않습니다.
② 서비스의 특성에 따라 회원의 개인정보와 관련이 없는 별명‧캐릭터 사진‧상태정보 등 자신을 소개하는 내용이 공개될 수 있습니다.
③ 회사는 관련 법령에 의해 관련 국가기관 등의 요청이 있는 경우를 제외하고는 회원의 개인정보를 본인의 동의 없이 타인에게 제공하지 않습니다.
④ 회사는 회원의 귀책사유로 개인정보가 유출되어 발생한 피해에 대하여 책임을 지지 않습니다.


        """.trimIndent()

        binding.termsText2.text = termText2


        // 세번째
        val termText3 = """
            
            제3장 이용계약 당사자의 의무

제9조 (회사의 의무) ① 회사는 관련 법령, 이 약관에서 정하는 권리의 행사 및 의무의 이행을 신의에 따라 성실하게 준수합니다.
② 회사는 회원이 안전하게 서비스를 이용할 수 있도록 개인정보(신용정보 포함)보호를 위해 보안시스템을 갖추어야 하며 개인정보처리방침을 공시하고 준수합니다. 회사는 이 약관 및 개인정보처리방침에서 정한 경우를 제외하고는 회원의 개인정보가 제3자에게 공개 또는 제공되지 않도록 합니다. 
③ 회사는 계속적이고 안정적인 서비스의 제공을 위하여 서비스 개선을 하던 중 설비에 장애가 생기거나 데이터 등이 멸실․훼손된 때에는 천재지변, 비상사태, 현재의 기술로는 해결이 불가능한 장애나 결함 등 부득이한 사유가 없는 한 지체 없이 이를 수리 또는 복구하도록 최선의 노력을 다합니다.

제10조 (회원의 의무) ① 회원은 회사에서 제공하는 서비스의 이용과 관련하여 다음 각 호에 해당하는 행위를 해서는 안 됩니다.
 1. 이용신청 또는 회원 정보 변경 시 허위사실을 기재하는 행위
 2. 회사가 제공하지 않는 서비스나 비정상적인 방법을 통해 사이버 자산(ID, 계정, 아이템, 포인트 등)을 매매 또는 증여하거나, 이를 취득하여 이용하는 행위
 3. 회사의 직원이나 운영자를 가장하거나 타인의 명의를 도용하여 글을 게시하거나 메일을 발송하는 행위, 타인으로 가장하거나 타인과의 관계를 허위로 명시하는 행위
 4. 타인의 신용카드⋅유/무선 전화⋅은행 계좌 등을 도용하여 유료 콘텐츠를 구매하는 행위, 다른 회원의 ID 및 비밀번호를 부정사용하는 행위
 5. 다른 회원의 개인정보를 무단으로 수집⋅저장⋅게시 또는 유포하는 행위
 6. 도박 등 사행행위를 하거나 유도하는 행위, 음란⋅저속한 정보를 교류⋅게재하거나 음란 사이트를 연결(링크)하는 행위, 수치심⋅혐오감 또는 공포심을 일으키는 말⋅소리⋅글⋅그림⋅사진 또는 영상을 타인에게 전송 또는 유포하는 행위 등 서비스를 불건전하게 이용하는 행위
 7. 서비스를 무단으로 영리, 영업, 광고, 홍보, 정치활동, 선거운동 등 본래의 용도 이외의 용도로 이용하는 행위
 8. 회사의 서비스를 이용하여 얻은 정보를 무단으로 복제․유통․조장하거나 상업적으로 이용하는 행위, 알려지거나 알려지지 않은 버그를 악용하여 서비스를 이용하는 행위
 9. 타인을 기망하여 이득을 취하는 행위, 회사의 서비스의 이용과 관련하여 타인에게 피해를 입히는 행위
 10. 회사나 타인의 지적재산권 또는 초상권을 침해하는 행위, 타인의 명예를 훼손하거나 손해를 가하는 행위
 11. 법령에 의하여 전송 또는 게시가 금지된 정보(컴퓨터 프로그램)나 컴퓨터 소프트웨어⋅하드웨어 또는 전기통신장비의 정상적인 작동을 방해⋅파괴할 목적으로 고안된 바이러스⋅컴퓨터 코드⋅파일⋅프로그램 등을 고의로 전송⋅게시⋅유포 또는 사용하는 행위
 12. 회사로부터 특별한 권리를 부여 받지 않고 애플리케이션을 변경하거나, 애플리케이션에 다른 프로그램을 추가⋅삽입하거나, 서버를 해킹⋅역설계하거나, 소스 코드나 애플리케이션 데이터를 유출⋅변경하거나, 별도의 서버를 구축하거나, 웹사이트의 일부분을 임의로 변경⋅도용하여 회사를 사칭하는 행위
 13. 그 밖에 관련 법령에 위반되거나 선량한 풍속 기타 사회통념에 반하는 행위 
② 회원의 계정 및 모바일 기기에 관한 관리 책임은 회원에게 있으며, 이를 타인이 이용하도록 하게 하여서는 안 됩니다. 모바일 기기의 관리 부실이나 타인에게 이용을 승낙함으로 인해 발생하는 손해에 대해서 회사는 책임을 지지 않습니다. 

회사는 다음 각 호의 행위의 구체적인 내용을 정할 수 있으며, 회원은 이를 따라야 합니다.
  1. 회원의 계정명, 기타 서비스 내에서 사용하는 명칭
  2. 채팅내용과 방법
  3. 게시판이용 및 서비스이용 방법
  4. 카카오, 페이스북, 구글플러스 등 외부 모바일 플랫폼 제휴 서비스 정책
  
  
        """.trimIndent()

        binding.termsText3.text = termText3

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
            termsChecked3 = true
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
            termsChecked3 = false
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
            termsChecked3 = true
            isAllChecked()
        }

        // 세번째 약관 동의 취소
        binding.termChecked3.setOnClickListener {
            binding.termChecked3.visibility = View.GONE
            binding.termNotChecked3.visibility = View.VISIBLE

            termsCheckedCnt -= 1
            termsChecked3 = false
            isAllChecked()
        }
        

        //// 동의 완료 버튼
        binding.termsAgreeBtn.setOnClickListener {
            if(termsChecked1 && termsChecked2 && termsChecked3) {
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

}