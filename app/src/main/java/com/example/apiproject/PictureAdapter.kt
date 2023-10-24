package com.example.apiproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PictureAdapter(val picList:MutableList<List<Any>>) : RecyclerView.Adapter<PictureAdapter.PicViewHolder>(){

    class PicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roverImage : ImageView
        val roverText : TextView

        init {
            roverImage = view.findViewById<ImageView>(R.id.photo)
            roverText = view.findViewById<TextView>(R.id.photo_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureAdapter.PicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pictures_rv, parent, false)
        return PicViewHolder(view)
    }

    override fun onBindViewHolder(holder: PicViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(picList[position][0])
            .centerCrop()
            .into(holder.roverImage)

        val id_display = picList[position][1]
        val camera_name_display = picList[position][2]
        holder.roverText.text = "id: $id_display camera name: $camera_name_display"
        //can set an on click listener here if you want a toast to pop up on click

    }
    override fun getItemCount(): Int {
        //display the amount of photos in the list
        return picList.size

    }

}