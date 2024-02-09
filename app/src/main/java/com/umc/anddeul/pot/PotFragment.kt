package com.umc.anddeul.postbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.checklist.ChecklistFragment
import com.umc.anddeul.databinding.FragmentPotBinding
import com.umc.anddeul.pot.GardenFragment

class PotFragment : Fragment() {

    lateinit var binding: FragmentPotBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPotBinding.inflate(inflater, container, false)

        binding.potImgGarden.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, GardenFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.potImgBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
        }
        return binding.root
    }
}