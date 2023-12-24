package com.example.findnews

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val newsImage: ImageView = itemView.findViewById<ImageView>(R.id.newsImage)
    val newsTitle: TextView = itemView.findViewById<TextView>(R.id.newsTitle)
    val newsDescription: TextView = itemView.findViewById<TextView>(R.id.newsDescription)
}