package com.umc.anddeul.postbox

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentPostboxBinding

class PostboxFragment : Fragment() {
    private lateinit var binding: FragmentPostboxBinding
    private lateinit var postAdapter: LetterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostboxBinding.inflate(inflater, container, false)

        //// 화분 키우기 페이지로 이동
        binding.gotoPotBtn.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frm, PotFragment())
//                .addToBackStack(null)
//                .commitAllowingStateLoss()
        }


        //// 달력



        //// 편지 리스트
        postAdapter = LetterAdapter()

        // 테스트용 더미 데이터
        val dummyPosts = listOf(
            Letter(1, "아티"),
            Letter(2, "도라"),
            Letter(3, "지나"),
            Letter(4, "율"),
            Letter(5, "도도"),
            Letter(6, "훈"),
            Letter(7, "빈온"),
            Letter(8, "세흐"),
        )

        postAdapter.letters = dummyPosts


        //// 편지 보기(팝업)
        val onClickListener = object: LetterAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
                val postPopupFragment = LetterPopupFragment(requireContext())
                postPopupFragment.show(dummyPosts[pos])
            }
        }
        postAdapter.setOnItemClickListener(onClickListener)

        binding.rvLetters.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvLetters.adapter = postAdapter


        //// 편지 작성 (음성)
        binding.voiceIv.setOnClickListener{
            if(binding.letterEt.text.toString().isEmpty()){   // 작성된 텍스트 없을 때
                val recordPopupFragment = RecordPopupFragment(requireContext())
                recordPopupFragment.show()
            } else {    // 작성된 텍스트 있을 때
                val dialogFragment = DialogLetterFragment(requireContext())
                dialogFragment.show("voice")
            }
        }


        //// 편지 작성 (텍스트)
        binding.mailIv.setOnClickListener{
            // 편지 보내는 기능 추가

            //텍스트 초기화
            binding.letterEt.setText("")
        }

        return binding.root
    }
}
