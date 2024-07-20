package com.umc.anddeul.start.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.umc.anddeul.MainActivity
import com.umc.anddeul.alarm.DeviceTokenDTO
import com.umc.anddeul.alarm.DeviceTokenInterface
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.start.signin.service.SigninService
import com.umc.anddeul.databinding.ActivitySignupBinding
import com.umc.anddeul.invite.JoinGroupSendActivity
import com.umc.anddeul.start.StartActivity
import com.umc.anddeul.start.model.RequestResponse
import com.umc.anddeul.start.service.RequestService
import com.umc.anddeul.start.terms.TermsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                                AnddeulErrorToast.createToast(this, "요청을 처리할 수 없습니다")?.show()
                            }
                        } else {
                            AnddeulErrorToast.createToast(this, "요청을 처리할 수 없습니다")?.show()
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

        //// 다른 카카오 계정으로 회원가입
        binding.newSignUpBtn.setOnClickListener {
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
        TokenManager.initialize(this) // 토큰 매니저 초기화
        TokenManager.setToken(jwt)

        RetrofitManager.initialize("https://umc-garden.store") // RetrofitManager 초기화
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

                    // FCM 토큰 발급
//                    val TAG = "deviceToken"
//                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            val token = task.result
//                            Log.d(TAG, token)
//                            putDeviceToken(token) { success ->
//                                if (success) {
                                    navigateToNextScreen(requestDTO)
//                                } else {
//                                    Log.w(TAG, "Failed to send device token")
//                                }
//                            }
//                        } else {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                            AnddeulErrorToast.createToast(this, "디바이스 토큰 전송 실패").show()
//                        }
//                    }
                } else {
                    AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다").show()
                }
            }
        }
    }

    private fun navigateToNextScreen(requestDTO: RequestResponse) {
        if (requestDTO.infamily) { // 가족이 있을 때
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            StartActivity._startActivity.finish()
            finish()
        } else if (requestDTO.request) { // 가족이 없고 요청이 있을 때
            val joinIntent = Intent(this, JoinGroupSendActivity::class.java)
            startActivity(joinIntent)
        }
    }

    private fun putDeviceToken(deviceToken: String, callback: (Boolean) -> Unit) {
        val retrofitManager = RetrofitManager.getRetrofitInstance()
        val deviceTokenService = retrofitManager.create(DeviceTokenInterface::class.java)

        deviceTokenService.putDeviceToken(deviceToken).enqueue(object : Callback<DeviceTokenDTO> {
            override fun onResponse(
                call: Call<DeviceTokenDTO>,
                response: Response<DeviceTokenDTO>
            ) {
                Log.e("putDeviceToken response code : ", "${response.code()}")
                Log.e("putDeviceToken response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    Log.e("putDeviceToken", "디바이스 토큰 전송 완료")
                    callback(true)
                }
            }

            override fun onFailure(call: Call<DeviceTokenDTO>, t: Throwable) {
                AnddeulErrorToast.createToast(this@SignupActivity, "서버 연결이 불안정합니다").show()
                Log.e("putDeviceToken", "디바이스 토큰 전송 실패")
                Log.e("putDeviceToken", "Failure message: ${t.message}")
                callback(false)
            }
        })
    }
}