import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
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
        Log.d("이미지 url", "imageUrl: ${imageUrl}")
        var bitmap: Bitmap? = null
        try {
            val url = URL(imageUrl)
            Log.d("url", "url: ${url}")
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            Log.d("connection", "connection: ${connection}")
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
            Log.d("onPostExecute", "result: ${result}")
            imageView.setImageBitmap(result)
        }
    }
}
