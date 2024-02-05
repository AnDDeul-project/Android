package com.umc.anddeul.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {
    lateinit var binding: FragmentUserProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        // 게시글 더미 데이터
        val feedList = listOf("123", "123", "123", "123")

        val userProfileRVAdapter = UserProfileRVAdapter(feedList)
        binding.userProfilePostRv.layoutManager = GridLayoutManager(requireContext(),3)
        binding.userProfilePostRv.adapter = userProfileRVAdapter

        binding.userProfileToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                // homeFragment로 이동
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()

            }
        }

        return  binding.root
    }
}