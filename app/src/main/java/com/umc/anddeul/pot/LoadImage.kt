import android.graphics.Bitmap
import android.os.AsyncTask
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import okhttp3.OkHttpClient
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class LoadImage(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

    override fun doInBackground(vararg urls: String): Bitmap? {
        val imageUrl = urls[0]
        return if (imageUrl.endsWith(".svg")) {
            // SVG 이미지인 경우 Glide 라이브러리를 통해 로딩
            loadSvgImage(imageUrl)
        } else {
            // 기존 이미지 로딩 로직
            loadBitmapImage(imageUrl)
        }
    }

    override fun onPostExecute(result: Bitmap?) {
        result?.let { bitmap ->
            imageView.setImageBitmap(result)
        }
    }

    private fun loadSvgImage(svgUrl: String): Bitmap? {
        return try {
            val requestOptions = RequestOptions().override(imageView.width, imageView.height)
            Glide.with(imageView.context)
                .`as`(Bitmap::class.java)
                .load(svgUrl)
                .apply(requestOptions)
                .submit()
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadBitmapImage(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            val bitmap = Glide.with(imageView.context)
                .asBitmap()
                .load(inputStream)
                .submit()
                .get()
            inputStream.close()
            connection.disconnect()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
