package com.umc.anddeul.postbox

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.umc.anddeul.databinding.FragmentPopupLetterBinding

class LetterPopupFragment(private val context: Context)  {
    private lateinit var binding : FragmentPopupLetterBinding
    private val dlg = Dialog(context)
    private var mediaPlayer: MediaPlayer? = null

    fun show(content: Letter) {
        binding = FragmentPopupLetterBinding.inflate(LayoutInflater.from(context))
        binding.familyTv.text = content.name
        if (content.type == 0){
            binding.letterPop1.text = content.content
            binding.letterPop1.visibility = View.VISIBLE
            binding.recordPop2.visibility = View.GONE
            binding.recordPop3.visibility = View.GONE
            binding.recordPop4.visibility = View.GONE
        }
        else{
            binding.letterPop1.visibility = View.GONE
            binding.recordPop2.visibility = View.VISIBLE
            binding.recordPop3.visibility = View.VISIBLE
            binding.recordPop4.visibility = View.VISIBLE

            // 음성 재생 - 추후 api 연결 후 주석 해제
//            binding.recordPop3.setOnClickListener {
//                binding.recordPop3.visibility = View.GONE
//                binding.recordPop4.visibility = View.GONE
//                val myUri: Uri = Uri.parse(Environment.getExternalStorageDirectory().absolutePath + "/Download/" + "Anddeul1707454972461.mp3")  // 더미 데이터
//                mediaPlayer = MediaPlayer().apply {
//                    setAudioStreamType(AudioManager.STREAM_MUSIC)
//                    setDataSource(context, myUri)
//                    prepare()
//                    start()
//                    Toast.makeText(context, "음성이 시작되었습니다.", Toast.LENGTH_SHORT).show()
//
//                    // 음성 실행 완료 시 (다시 재생 버튼 등장)
//                    setOnCompletionListener {
//                        binding.recordPop3.visibility = View.VISIBLE
//                        binding.recordPop4.visibility = View.VISIBLE
//                        mediaPlayer?.reset()
//                    }
//                }
//            }
        }

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setCanceledOnTouchOutside(true)

        dlg.show()
    }
}