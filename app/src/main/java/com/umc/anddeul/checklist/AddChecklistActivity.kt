package com.umc.anddeul.checklist

import android.os.Bundle
import android.os.PersistableBundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.anddeul.databinding.ActivityAddChecklistBinding
import com.umc.anddeul.databinding.FragmentChecklistBinding
import java.text.SimpleDateFormat
import java.util.Date

class AddChecklistActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddChecklistBinding
    private var checklist = ArrayList<Checklist>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dateStamp : String = SimpleDateFormat("MM월 dd일").format(Date())
        binding.checkliAddTvDate.text = dateStamp

        checklist.add(Checklist("메모메모", "율", "image", true))
        checklist.add(Checklist("달력 UI 수정할 예정이에요", "율", "image", false))

        //리사이클러뷰 연결
        val addChecklistRVAdapter = AddChecklistRVAdapter(checklist)
        binding.checklistAddRecylerView.adapter = addChecklistRVAdapter
        binding.checklistAddRecylerView.layoutManager = LinearLayoutManager(this@AddChecklistActivity, LinearLayoutManager.VERTICAL, false)

        binding.checkliEtContents.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                //체크리스트 추가 코드
            }
            true
        }
    }

}