package com.example.findnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.net.URL

import com.bumptech.glide.Glide


class NewsAdapter(private val newsImages: List<URL>,
                  private val newsTitles: List<String>,
                  private val newsDescriptions: List<String>,
                    ) :
    RecyclerView.Adapter<NewsViewHolder>() {
    private var itemClickListener: OnItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int): Boolean
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        itemLongClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        //создание объекта, размеченного в news_item
        val newsView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(newsView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
/*        val data = newsList[position]
        //Обновление данных recyclerView
        holder.newsItem.text = data*/

        holder.newsTitle.text = newsTitles[position]
        holder.newsDescription.text = newsDescriptions[position]

        Glide.with(holder.newsImage)
            .load(newsImages[position])
            .into(holder.newsImage)

        // бинд обработчика клика на элемент
        holder.itemView.setOnClickListener {
            if (itemClickListener != null) {
                itemClickListener!!.onItemClick(holder.adapterPosition)
            }
        }
        // бинд обработчика долгого клика на элемент
        holder.itemView.setOnLongClickListener {
            if (itemLongClickListener != null) {
                return@setOnLongClickListener itemLongClickListener!!.onItemLongClick(holder.adapterPosition)
            }
            false
        }
    }

    override fun getItemCount(): Int {
        return newsTitles.size
    }
}