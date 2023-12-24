package com.example.findnews.api

import java.net.URL

data class News(
    val articleId: String,
    val title: String,
    val link: URL,
    val keywords: List<String>,
    val creator: List<String>,
    val videoURL: URL,
    val description: String,
    val content: String,
    val pubDate: String,
    val imageURL: URL,
    val sourceId: String,
    val sourcePriority: Int,
    val country: List<String>,
    val category: List<String>,
    val language: String
)