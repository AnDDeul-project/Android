package com.umc.anddeul.postbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc.anddeul.databinding.FragmentPotBinding

class PotFragment : Fragment() {

    lateinit var binding: FragmentPotBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPotBinding.inflate(inflater, container, false)

        binding.potImgBack.setOnClickListener {

        }


        return binding.root
    }
}