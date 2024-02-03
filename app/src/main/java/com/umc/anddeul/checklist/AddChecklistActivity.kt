package com.umc.anddeul.checklist

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityAddChecklistBinding

class AddChecklistActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddChecklistBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        binding = ActivityAddChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}