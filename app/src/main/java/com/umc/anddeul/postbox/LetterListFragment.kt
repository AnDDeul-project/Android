package com.umc.anddeul.postbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.FragmentLetterlistBinding

class LetterListFragment : Fragment() {
    private lateinit var binding: FragmentLetterlistBinding
    private lateinit var letterlistAdapter: LetterListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLetterlistBinding.inflate(inflater, container, false)

        //// 편지 리스트
        letterlistAdapter = LetterListAdapter()

        // 테스트용 더미 데이터
        val dummyPosts = listOf(
            Letter(1, "아티", 0, "어쩌구저쩌구"),
            Letter(2, "도라", 0, "어쩌구저쩌구"),
            Letter(3, "지나", 0, "어쩌구저쩌구"),
            Letter(4, "율", 1, "음성 메세지가 도착했습니다."),
            Letter(5, "도도", 1, "음성 메세지가 도착했습니다."),
            Letter(6, "훈", 1, "음성 메세지가 도착했습니다."),
            Letter(7, "빈온", 0, "어쩌구저쩌구"),
            Letter(8, "세흐", 0, "어쩌구저쩌구"),
        )

        letterlistAdapter.letters = dummyPosts

        //// 편지 보기(팝업)
        val onClickListener = object: LetterListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, pos: Int) {
                val postPopupFragment = LetterPopupFragment(requireContext())
                postPopupFragment.show(dummyPosts[pos])
            }
        }
        letterlistAdapter.setOnItemClickListener(onClickListener)

        binding.rvLetterlist.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvLetterlist.adapter = letterlistAdapter

        return binding.root
    }

}