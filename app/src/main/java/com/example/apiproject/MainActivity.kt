package com.example.apiproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import okhttp3.Headers
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler


class MainActivity : AppCompatActivity() {
    //Define imageURL as a null string
    private var imageURL: String? = null
    private lateinit var picList: MutableList<List<Any>>
    private lateinit var recyclerViewPics: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewPics = findViewById<RecyclerView>(R.id.picture_recycler_view)
        picList = mutableListOf<List<Any>>() //list of photo URLs

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
        client["https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?earth_date=$picDate&api_key=redacted", params, object :
            JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                // called when response HTTP status is "200 OK"
                Log.d("API", "response successful for $picDate")
                val photos = json.jsonObject.getJSONArray("photos")
                if (photos.length() > 0) {
                    val photo_array_len = photos.length()
                    Log.d("photos", "there are $photo_array_len photos in this request")
                    var len = photos.length()
                    //only allow app to load the first 15 pictures
                    if (len > 15){
                        len = 15
                    }
                    for (i in 0 until len){
                        val current_photo = photos.getJSONObject(i)
                        var httpImageURL = current_photo.getString("img_src")
                        val charToAdd = 's'
                        val indexToAddAt = 4
                        val currentImageURL = httpImageURL.substring(0, indexToAddAt) + charToAdd + httpImageURL.substring(indexToAddAt)
                        Log.d("URL", currentImageURL)
                        val id = current_photo.getInt("id").toString()
                        Log.d("id", id)
                        val camera_object = current_photo.getJSONObject("camera")
                        val camera_name = camera_object.getString("name")
                        Log.d("cameraName", camera_name)
                        val current_photo_list = listOf(currentImageURL, id, camera_name)
                        Log.d("photoList", current_photo_list.size.toString())
                        //picList.add(currentImageURL)
                        picList.add(current_photo_list)

                    }
                    Log.d("listSize", picList.size.toString())

                    val adapter = PictureAdapter(picList)
                    recyclerViewPics.adapter = adapter
                    recyclerViewPics.layoutManager = LinearLayoutManager(this@MainActivity)
                    recyclerViewPics.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
                    findViewById<Button>(R.id.reset_button).visibility = View.VISIBLE
                    findViewById<Button>(R.id.search_button).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.picture_recycler_view).visibility = View.VISIBLE
                    //val first_photo = photos.getJSONObject(0)
                    //var httpImageURL = first_photo.getString("img_src")
                    //val charToAdd = 's'
                    //val indexToAddAt = 4
                    //imageURL = httpImageURL.substring(0, indexToAddAt) + charToAdd + httpImageURL.substring(indexToAddAt)
                    //showToast(imageURL)
                    //get_pic()

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
        //Log.d("find_pic", "This is the returned URL $imageURL")
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun reset_app() {
        findViewById<RecyclerView>(R.id.picture_recycler_view).visibility = View.GONE
        //findViewById<ImageView>(R.id.photo).visibility = View.GONE
        findViewById<Button>(R.id.reset_button).visibility = View.GONE
        findViewById<Button>(R.id.search_button).visibility = View.VISIBLE
        findViewById<EditText>(R.id.editTextDate).text.clear()
        picList.clear()
        //imageURL = null
    }


}