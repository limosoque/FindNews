package com.example.findnews.api

import java.net.URL

data class News(
    val article_id: String,
    val title: String,
    val link: URL,
    val keywords: List<String>,
    val creator: List<String>,
    val video_url: URL,
    val description: String,
    val content: String,
    val pubDate: String,
    val image_url: URL,
    val source_id: String,
    val source_priority: Int,
    val country: List<String>,
    val category: List<String>,
    val language: String
)