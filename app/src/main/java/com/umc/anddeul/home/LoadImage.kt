package com.umc.anddeul.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class LoadImage(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

    override fun doInBackground(vararg urls: String): Bitmap? {
        val imageUrl = urls[0]
        var bitmap: Bitmap? = null
        try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            connection.disconnect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun onPostExecute(result: Bitmap?) {
        result?.let { bitmap ->
            imageView.setImageBitmap(result)
        }
    }
}