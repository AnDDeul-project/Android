package com.umc.anddeul.postbox

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentPopupRecordBinding
import java.util.Date

class RecordPopupFragment(private val context: Context) {
    private lateinit var binding: FragmentPopupRecordBinding
    private val dlg = Dialog(context)
    private var outputPath: String? = null
    private var mediaRecorder : MediaRecorder? = null
    private var state : Boolean = false
    private var hasRecorded : Boolean = false

    fun show(){
        binding = FragmentPopupRecordBinding.inflate(LayoutInflater.from(context))

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.window?.setGravity(Gravity.BOTTOM)

        dlg.setCanceledOnTouchOutside(true)

        // 녹음 시작 버튼
        binding.recordPlayBtn.setOnClickListener {
            // 권한 부여 여부
            val isEmpower = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

            // 권한 부여 되지 않았을경우
            if (isEmpower) {
                empowerRecordAudioAndWriteReadStorage()

                // 권한 부여 되었을 경우
            } else {
                startRecording()
                binding.recordPlayBtn.visibility = View.GONE
                binding.recordPauseBtn.visibility = View.VISIBLE
            }

        }

        // 녹음 일시정지 버튼
        binding.recordPauseBtn.setOnClickListener {
            pauseRecording()
            binding.recordPlayBtn.visibility = View.VISIBLE
            binding.recordPauseBtn.visibility = View.GONE
        }

        // 확인 버튼
        binding.okBtn.setOnClickListener {
            stopRecording()

            val postboxFragment = PostboxFragment()

            val bundle = Bundle()
            bundle.putString("recordFilePath", outputPath)
            postboxFragment.arguments = bundle

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, postboxFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()

            dlg.dismiss()
        }

        // 재녹음 버튼
        binding.restartBtn.setOnClickListener {
            val restartFragment = DialogRecordRestartFragment(context)
            restartFragment.show()
        }

        dlg.show()
    }

    // 레코딩, 파일 읽기 쓰기 권한부여
    private fun empowerRecordAudioAndWriteReadStorage(){

        val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(context as Activity, permissions,0)

    }

    private fun startRecording(){

        if (hasRecorded == false) {
            hasRecorded = true

            val fileName = "Anddeul" + Date().getTime().toString() + ".mp3"

            outputPath =
                Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileName
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
            mediaRecorder?.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(outputPath)


            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(context, "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show()
        }
        else{
            state = true
            mediaRecorder?.resume()
            Toast.makeText(context, "녹음이 다시 시작되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 일시정지
    private fun pauseRecording(){
        if(state){
            mediaRecorder?.pause()
            Toast.makeText(context, "녹음이 정지되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "녹음 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 정지
    private fun stopRecording(){
        if(state){
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            state = false
            Toast.makeText(context, "녹음이 되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "녹음 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
        }
    }
}