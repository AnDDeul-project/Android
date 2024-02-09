package com.umc.anddeul.checklist

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.R
import com.umc.anddeul.databinding.ItemCalendarCellBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class CalendarRVAdapter(private val date: ArrayList<DateSet>) : RecyclerView.Adapter<CalendarRVAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return date.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(date[position])
        holder.itemView.setOnClickListener {

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemCalendarCellBinding = ItemCalendarCellBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemCalendarCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date : DateSet) {
            binding.calendarTvDate.text = date.date
            binding.calendarTvDay.text = date.day

            if (date.day == "토") {
                Log.d("캘린더", "${date.day}요일 이프문")
                binding.calendarTvDate.setTextColor(Color.parseColor("#6873D3"))
            }
            else if (date.day == "일")
                binding.calendarTvDate.setTextColor(Color.parseColor("#EB6B59"))
            else
                binding.calendarTvDate.setTextColor(Color.parseColor("#666666"))

            val dateStamp : String = SimpleDateFormat("dd").format(Date())
            if (date.date == dateStamp) {
                binding.calendarImgTodayCircle.visibility = View.VISIBLE
                binding.calendarTvDate.setTextColor(Color.parseColor("#F7F7FB"))
            }
            else {
                binding.calendarImgTodayCircle.visibility = View.INVISIBLE
            }
        }
    }
}