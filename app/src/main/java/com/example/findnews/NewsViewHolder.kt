package com.example.findnews

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var newsItem: TextView

    init {
        newsItem = itemView.findViewById<TextView>(R.id.newsItem)
    }
}