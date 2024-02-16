package com.umc.anddeul.postbox

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.umc.anddeul.databinding.FragmentPopupLetterBinding
import com.umc.anddeul.postbox.model.Post
import com.umc.anddeul.postbox.service.ReadMailService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LetterPopupFragment(private val context: Context, private val onDismissCallback: () -> Unit)  {
    private lateinit var binding : FragmentPopupLetterBinding
    private val dlg = Dialog(context)
    private var mediaPlayer: MediaPlayer? = null
    private var isMediaPaused: Boolean = false

    fun show(content: Post) {
        binding = FragmentPopupLetterBinding.inflate(LayoutInflater.from(context))

        dlg.setOnDismissListener {
            releaseMediaPlayer()
            onDismissCallback.invoke()
        }

        // api 연결
        val idx = content.postboxIdx
        val loadedToken = loadJwt() // jwt토큰
        val readMailService = ReadMailService()
        readMailService.readMail(loadedToken, idx.toInt()) { mailDTO ->
            if (mailDTO != null) {
                if (mailDTO.isSuccess.toString() == "true") {
                    binding.userTv.text = mailDTO.post[0].receiverIdx
                    binding.familyTv.text = mailDTO.post[0].senderIdx
                    binding.letterDateTv.text = "${mailDTO.post[0].sendDate.substring(0, 10)} 의 질문"
                    binding.letterTitleTv.text = mailDTO.post[0].question.toString()

                    if (content.voice == 0.toLong()){
                        binding.letterPop1.text = mailDTO.post[0].content
                        binding.letterPop1.visibility = View.VISIBLE
                        binding.recordPop2.visibility = View.GONE
                        binding.recordPop3.visibility = View.GONE
                        binding.recordPop4.visibility = View.GONE
                    }
                    else {
                        binding.letterPop1.visibility = View.GONE
                        binding.recordPop2.visibility = View.VISIBLE
                        binding.recordPop3.visibility = View.VISIBLE
                        binding.recordPop4.visibility = View.VISIBLE

                        // 음성 재생
                        binding.recordPop3.setOnClickListener {
                            binding.recordPop3.visibility = View.GONE
                            binding.recordPop4.visibility = View.GONE
                            binding.recordPop5.visibility = View.VISIBLE
                            val myUri: Uri = Uri.parse(mailDTO.post[0].content)
                            if (mediaPlayer == null) {  // 일지정지한 적 없을 때
                                mediaPlayer = MediaPlayer().apply {
                                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                                    setDataSource(context, myUri)
                                    prepare()
                                    start()

                                    // 음성 실행 완료 시 (다시 재생 버튼 등장)
                                    setOnCompletionListener {
                                        binding.recordPop3.visibility = View.VISIBLE
                                        binding.recordPop4.visibility = View.VISIBLE
                                        binding.recordPop5.visibility = View.GONE
                                        mediaPlayer?.reset()
                                        mediaPlayer = null
                                    }
                                }
                            }
                            else if (isMediaPaused) {
                                mediaPlayer?.start()
                                isMediaPaused = false
                            }
                        }

                        // 음성 일시정지
                        binding.recordPop5.setOnClickListener {
                            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                                mediaPlayer?.pause()
                                isMediaPaused = true
                                binding.recordPop3.visibility = View.VISIBLE
                                binding.recordPop4.visibility = View.VISIBLE
                                binding.recordPop5.visibility = View.GONE
                            }
                        }
                    }

                }
            } else {
            }
        }



        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setCanceledOnTouchOutside(true)

        dlg.show()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        isMediaPaused = false
    }

    // 토큰 불러오기
    private fun loadJwt(): String {
        val spf = context.getSharedPreferences("myToken", AppCompatActivity.MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }
}