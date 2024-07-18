package com.umc.anddeul.start.signin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.umc.anddeul.MainActivity
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.start.signin.service.SigninService
import com.umc.anddeul.databinding.ActivityLoginBinding
import com.umc.anddeul.invite.JoinGroupSendActivity
import com.umc.anddeul.start.StartActivity
import com.umc.anddeul.start.service.RequestService
import com.umc.anddeul.start.terms.TermsActivity

class LoginActivity: AppCompatActivity()  {
    val TAG = "LoginActivity"
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 뒤로 가기
        binding.loginBackBtn.setOnClickListener {
            val startIntent = Intent(this, StartActivity::class.java)
            startActivity(startIntent)
        }

        val signinService = SigninService()

        //// 기존 카카오톡으로 로그인
        binding.oldLoginBtn.setOnClickListener {
            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    AnddeulErrorToast.createToast(this, "요청을 처리할 수 없습니다")?.show()
                } else if (token != null) {
                    signinService.executeSignIn(token.accessToken) { signinResponse ->
                        if (signinResponse != null) {
                            if (signinResponse.isSuccess.toString() == "true") {
                                saveJwt(signinResponse.accessToken.toString())
                                if (signinResponse.has == true){
                                    checkFamilyRequest(loadJwt())
                                }
                                else {
                                    val termsIntent = Intent(this, TermsActivity::class.java)
                                    startActivity(termsIntent)
                                }
                            } else {
                                AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                            }
                        } else {
                            AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                        }
                    }
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        signinService.executeSignIn(token.accessToken) { signinResponse ->
                            if (signinResponse != null) {
                                if (signinResponse.isSuccess.toString() == "true") {
                                    saveJwt(signinResponse.accessToken.toString())
                                    if (signinResponse.has == true){
                                        checkFamilyRequest(loadJwt())
                                    }
                                    else {
                                        val termsIntent = Intent(this, TermsActivity::class.java)
                                        startActivity(termsIntent)
                                    }
                                } else {
                                    AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                                }
                            } else {
                                AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                            }
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        //// 다른 카카오 계정으로 로그인
        binding.newLoginBtn.setOnClickListener {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(this, prompts = listOf(Prompt.LOGIN)) { token, error ->
                if (error != null) {
                    AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                }
                else if (token != null) {
                    signinService.executeSignIn(token.accessToken) { signinResponse ->
                        if (signinResponse != null) {
                            if (signinResponse.isSuccess.toString() == "true") {
                                saveJwt(signinResponse.accessToken.toString())
                                if (signinResponse.has == true){
                                    checkFamilyRequest(loadJwt())
                                }
                                else {
                                    val termsIntent = Intent(this, TermsActivity::class.java)
                                    startActivity(termsIntent)
                                }
                            } else {
                                AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                            }
                        } else {
                            AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                        }
                    }
                }
            }
        }
    }

    //// 토큰 저장
    private fun saveJwt(jwt: String){
        val spf = getSharedPreferences("myToken", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putString("jwtToken", jwt)
        editor.apply()
    }

    // 토큰 불러오기
    private fun loadJwt(): String {
        val spf = getSharedPreferences("myToken", AppCompatActivity.MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }

    // 가족 유무, 가족 요청 유무 확인
    private fun checkFamilyRequest(accessToken: String) {
        val requestService = RequestService()
        requestService.requestInfo(loadJwt()) { requestDTO ->
            if (requestDTO != null) {
                if (requestDTO.isSuccess.toString() == "true") {
                    if (requestDTO.infamily){   // 가족이 있을 때
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainIntent)
                        StartActivity._startActivity.finish()
                        finish()
                    }
                    else if (requestDTO.request){   // 가족이 없고 요청이 있을 때
                        val joinIntent = Intent(this, JoinGroupSendActivity::class.java)
                        startActivity(joinIntent)
                    }
                } else {
                    AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                }
            }
        }
    }
}