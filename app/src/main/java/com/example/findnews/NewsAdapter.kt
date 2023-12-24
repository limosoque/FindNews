package com.example.findnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import java.net.URL


class NewsAdapter(private val newsImages: List<URL>,
                  private val newsTitles: List<String>,
                  private val newsDescriptions: List<String>,
                    ) :
    RecyclerView.Adapter<NewsViewHolder>() {
    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        //создание объекта, размеченного в news_item
        val newsView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(newsView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.newsTitle.text = newsTitles[position]
        holder.newsDescription.text = newsDescriptions[position]

        //Прорисовка картинки
        Glide.with(holder.newsImage)
            .load(newsImages[position])
            .into(holder.newsImage)

        // бинд обработчика клика на элемент
        holder.itemView.setOnClickListener {
            if (itemClickListener != null) {
                itemClickListener!!.onItemClick(holder.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return newsTitles.size
    }
}