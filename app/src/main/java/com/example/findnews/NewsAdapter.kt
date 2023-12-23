package com.example.findnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class NewsAdapter(private val shoppingList: List<String>) :
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
        //создание объекта, размеченного в shopping_item
        val shoppingView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(shoppingView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val data = shoppingList[position]
        //Обновление данных recyclerView
        holder.newsItem.text = data
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
        return shoppingList.size
    }
}