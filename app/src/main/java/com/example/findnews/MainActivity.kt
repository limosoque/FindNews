package com.example.findnews

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle;
import android.util.Log
import android.view.View;
import android.widget.EditText;
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findnews.api.NewsApiJSON
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

import java.util.ArrayList;

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.SocketTimeoutException
import java.net.URL


class MainActivity : AppCompatActivity() {
    private var newsRecyclerView: RecyclerView? = null
    private var newsAdapter: NewsAdapter? = null

    private var newsImages: MutableList<URL> = ArrayList()
    private var newsTitles: MutableList<String> = ArrayList()
    private var newsDescriptions: MutableList<String> = ArrayList()
    private var newsLinks: MutableList<URL> = ArrayList()

    private var currentJob: Job? = null
    private lateinit var newsApiService: NewsApiService

    interface NewsApiService{
        @GET("news")
        suspend fun getNews(@Query("apikey") apikey: String, @Query("q") request: String): NewsApiJSON
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            //debug
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // Создаем экземпляр интерфейса API
            newsApiService = Retrofit.Builder()
                //.baseUrl("@string/news_source")
                //TODO: hardcode, ибо иначе не работает
                .baseUrl("https://newsdata.io/api/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(NewsApiService::class.java)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        //RecyclerView
        newsRecyclerView = findViewById<RecyclerView>(R.id.newsRecyclerView)
        newsAdapter = NewsAdapter(newsImages, newsTitles, newsDescriptions)
        newsRecyclerView!!.adapter = newsAdapter
        newsRecyclerView!!.layoutManager = LinearLayoutManager(this)

        // бинд кнопки поиска
        findViewById<View>(R.id.searchButton)
        .setOnClickListener { showSearchDialog() }

        // бинд обработчика клика на элемент для редактирования
        newsAdapter!!.setOnItemClickListener(object : NewsAdapter.OnItemClickListener {
            @SuppressLint("QueryPermissionsNeeded")
            override fun onItemClick(position: Int) {
                // Поиск новости в браузере
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsLinks[position].toString()))
                startActivity(intent)
            }
        })
    }

    private fun showSearchDialog() {
        val editText = EditText(this)
        //Создание окна с вводом запроса
        android.app.AlertDialog.Builder(this)
            .setTitle("Поиск новостей")
            .setView(editText) //Создание лисенера-лямбда-выражение, который добавляет новый элемент
            .setPositiveButton("Поиск") { _, _ ->
                val newItem = editText.text.toString()
                //не добавляем пустую строку
                if (newItem != "") {
                    //currentJob?.cancel()
                    currentJob = CoroutineScope(Dispatchers.Main).launch {
                        //очистить массивы перед новым добавлением
                        newsImages.clear()
                        newsTitles.clear()
                        newsDescriptions.clear()
                        newsLinks.clear()
                        sendRequest(newItem)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private suspend fun sendRequest(request: String){
        try {
            val news = withContext(Dispatchers.IO){
                newsApiService.getNews("pub_35234445641e29ee8b0f18ff7d1fe8971761a", request.toString())
            }

            Log.i("MainActivity", "Status = ${news.status}")
            for(article in news.results){
                Log.i("MainActivity", "Result = ${article.title}")

                newsImages.add(article.image_url)
                newsAdapter?.notifyItemInserted(newsImages.size - 1);
                newsTitles.add(article.title)
                newsAdapter?.notifyItemInserted(newsTitles.size - 1);
                newsDescriptions.add(article.description)
                newsAdapter?.notifyItemInserted(newsDescriptions.size - 1);
                newsLinks.add(article.link)
                newsAdapter?.notifyItemInserted(newsLinks.size - 1);

            }

        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            Toast.makeText(applicationContext,
                "Сервер новостей недоступен, возвращайтесь позднее",
                Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext,
                "Новостей нет, спите спокойно",
                Toast.LENGTH_LONG).show()
        }
    }
}