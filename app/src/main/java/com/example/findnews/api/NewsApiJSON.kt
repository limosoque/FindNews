package com.example.findnews.api

data class NewsApiJSON(
    val status: String,
    val totalResults: Int,
    val results: List<News>,
    val nextPage: ULong
)