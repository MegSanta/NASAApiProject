package com.example.apiproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import okhttp3.Headers
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler


class MainActivity : AppCompatActivity() {
    //Define imageURL as a null string
    var imageURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val API_search_button = findViewById<Button>(R.id.search_button)
        val app_reset_button = findViewById<Button>(R.id.reset_button)
        API_search_button.setOnClickListener{
            find_pic()
        }
        app_reset_button.setOnClickListener{
            reset_app()
        }
    }
    private fun get_pic() {
        val picture = findViewById<ImageView>(R.id.photo)
        //makes API call and loads image
        Log.d("get_pic", "This is the url $imageURL")
        //Check if imageURL is null
        if (imageURL.isNullOrEmpty()){
            Log.d("get_pic", "imageURL is null or empty")
            showToast("Enter a valid date")
        } else {
            picture.visibility= View.VISIBLE
            Glide.with(this)
                .load(imageURL)
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk cache
                        .skipMemoryCache(true) // Disable memory cache
                )
                .fitCenter()
                .into(picture)
            val app_reset_button = findViewById<Button>(R.id.reset_button)
            app_reset_button.visibility= View.VISIBLE
            findViewById<Button>(R.id.search_button).visibility = View.GONE
        }
    }

    private fun find_pic() {
        //makes API call
        val picDate = findViewById<EditText>(R.id.editTextDate).text.toString()
        val client = AsyncHttpClient()
        val params = RequestParams()

        params["earth_date"] = picDate
        client["https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?earth_date=$picDate&api_key=36iraEY8DMOJQM3FKZYymlVrNG5PCNLLXhd9kP4h", params, object :
            JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                // called when response HTTP status is "200 OK"
                Log.d("API", "response successful for $picDate")
                val photos = json.jsonObject.getJSONArray("photos")
                if (photos.length() > 0) {
                    val first_photo = photos.getJSONObject(0)
                    var httpImageURL = first_photo.getString("img_src")
                    val charToAdd = 's'
                    val indexToAddAt = 4
                    imageURL = httpImageURL.substring(0, indexToAddAt) + charToAdd + httpImageURL.substring(indexToAddAt)
                    //showToast(imageURL)
                    get_pic()

                } else {
                    Log.d("API", "No photos for this date")
                    showToast("There are no photos for this date, try another")

                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                t: Throwable?

            ) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                showToast("There are no photos for this date, try another")
                Log.d("API", "response failure")
            }
        }]
        Log.d("find_pic", "This is the returned URL $imageURL")
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun reset_app() {
        val photoImageView: ImageView = findViewById<ImageView>(R.id.photo)
        Glide.with(this)
            .load(R.drawable.ic_launcher_foreground)
            .fitCenter()
            .into(photoImageView)
        findViewById<ImageView>(R.id.photo).visibility = View.GONE
        findViewById<Button>(R.id.reset_button).visibility = View.GONE
        findViewById<Button>(R.id.search_button).visibility = View.VISIBLE
        findViewById<EditText>(R.id.editTextDate).text.clear()
        imageURL = null
    }


}