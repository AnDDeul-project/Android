package com.umc.anddeul.start.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.umc.anddeul.start.signin.service.SigninService
import com.umc.anddeul.databinding.ActivitySignupBinding
import com.umc.anddeul.start.StartActivity
import com.umc.anddeul.start.terms.TermsActivity

class SignupActivity: AppCompatActivity()  {
    val TAG = "SignupActivity"
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 뒤로 가기
        binding.signupBackBtn.setOnClickListener {
            val startIntent = Intent(this, StartActivity::class.java)
            startActivity(startIntent)
        }

        val signinService = SigninService()

        //// 기존 카카오톡으로 회원가입
        binding.oldSignUpBtn.setOnClickListener {
            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오계정으로 회원가입 실패", error)
                } else if (token != null) {
                    Log.i(TAG, "카카오계정으로 회원가입 성공 ${token.accessToken}")
                    signinService.executeSignIn(token.accessToken) { signinResponse ->
                        if (signinResponse != null) {
                            if (signinResponse.isSuccess.toString() == "true") {
                                saveJwt(signinResponse.accessToken.toString())
                                val termsIntent = Intent(this, TermsActivity::class.java)
                                startActivity(termsIntent)
                            }
                        } else {
                        }
                    }
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 회원가입 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.i(TAG, "카카오톡으로 회원가입 성공 ${token.accessToken}")
                        signinService.executeSignIn(token.accessToken) { signinResponse ->
                            if (signinResponse != null) {
                                if (signinResponse.isSuccess.toString() == "true") {
                                    saveJwt(signinResponse.accessToken.toString())
                                    val termsIntent = Intent(this, TermsActivity::class.java)
                                    startActivity(termsIntent)
                                }
                            } else {
                            }
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        //// 다른 카카오 계정으로 회원가입
        binding.newSignUpBtn.setOnClickListener {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(this, prompts = listOf(Prompt.LOGIN)) { token, error ->
                if (error != null) {
                    Log.e(TAG, "회원가입 실패", error)
                }
                else if (token != null) {
                    Log.i(TAG, "회원가입 성공 ${token.accessToken}")
                    signinService.executeSignIn(token.accessToken) { signinResponse ->
                        if (signinResponse != null) {
                            if (signinResponse.isSuccess.toString() == "true") {
                                saveJwt(signinResponse.accessToken.toString())
                                val termsIntent = Intent(this, TermsActivity::class.java)
                                startActivity(termsIntent)
                            }
                        } else {
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
}