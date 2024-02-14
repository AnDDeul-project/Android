package com.umc.anddeul.start.terms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityTermsBinding
import com.umc.anddeul.invite.InviteStartActivity
import com.umc.anddeul.start.setprofile.SetProfileActivity
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