package com.example.findnews

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
import java.net.URL


class MainActivity : AppCompatActivity() {
    private var newsRecyclerView: RecyclerView? = null
    private var newsAdapter: NewsAdapter? = null

    private var newsImages: MutableList<URL> = ArrayList()
    private var newsTitles: MutableList<String> = ArrayList()
    private var newsDescription: MutableList<String> = ArrayList()

    private var currentJob: Job? = null
    private lateinit var newsApiService: NewsApiService

    interface NewsApiService{
/*        @GET("{request}")
        suspend fun getNews(@Path("request") request: String): String*/
    @GET("news")
    suspend fun getNews(@Query("apikey") apikey: String, @Query("q") request: String): NewsApiJSON
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
/*            // Создаем объект Retrofit
            val retrofit = Retrofit.Builder()
                //.baseUrl("@string/news_source")
                //TODO: hardcode, ибо иначе не работает
                .baseUrl("https://newsdata.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()*/

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

/*            Toast.makeText(applicationContext,
                "инит успех",
                Toast.LENGTH_SHORT).show()*/
        } catch (e: Exception) {
            e.printStackTrace()
/*            Toast.makeText(applicationContext,
                " инит Провал",
                Toast.LENGTH_SHORT).show()*/
        }



        //RecyclerView
        newsRecyclerView = findViewById<RecyclerView>(R.id.newsRecyclerView)
        newsAdapter = NewsAdapter(newsImages, newsTitles, newsDescription)
        newsRecyclerView!!.adapter = newsAdapter
        newsRecyclerView!!.layoutManager = LinearLayoutManager(this)

        //TODO: Listener, в котором и будет запрос

        // бинд кнопки добавления нового элемента
        findViewById<View>(R.id.searchButton)
        .setOnClickListener { showAddItemDialog() }

/*        // бинд обработчика клика на элемент для редактирования
        newsAdapter!!.setOnItemClickListener(object : NewsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                showEditItemDialog(position)
            }
        })*/

/*        // бинд обработчика клика на элемент
        newsAdapter!!.setOnItemLongClickListener(object : NewsAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int): Boolean {
                newsList.removeAt(position)
                newsAdapter!!.notifyItemRemoved(position)
                return true
            }
        })*/
    }

    private fun showAddItemDialog() {
        val editText = EditText(this)
        //Создание окна с вводом нового продукта
        android.app.AlertDialog.Builder(this)
            .setTitle("Добавить продукт")
            .setView(editText) //Создание лисенера-лямбда-выражение, который добавляет новый элемент
            .setPositiveButton("Добавить") { _, _ ->
                val newItem = editText.text.toString()
                //не добавляем пустую строку
                if (newItem != "") {
                    //currentJob?.cancel()
                    currentJob = CoroutineScope(Dispatchers.Main).launch {
                        //sendRequest("@string/news_source_key$newItem")
                        //TODO: hardcode, ибо иначе не работает
/*                        Toast.makeText(applicationContext,
                            "api/1/news?apikey=pub_35234445641e29ee8b0f18ff7d1fe8971761a&q=$newItem",
                            Toast.LENGTH_LONG).show()*/
                        //sendRequest("api/1/news?apikey=pub_35234445641e29ee8b0f18ff7d1fe8971761a&q=$newItem")
                        sendRequest(newItem)
                    }

                    /*shoppingList.add(newItem)
                    //Сообщаем адаптеру, что элемент добавлен, и он перерисовывает эту позицию на экране
                    newsAdapter?.notifyItemInserted(shoppingList.size - 1)*/
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private suspend fun sendRequest(request: String){
        try {
            val news = withContext(Dispatchers.IO){
/*                Toast.makeText(applicationContext,
                    request,
                    Toast.LENGTH_SHORT).show()*/
                newsApiService.getNews("pub_35234445641e29ee8b0f18ff7d1fe8971761a", request.toString())
            }

            Log.i("MainActivity", "Status = ${news.status}")
            for(article in news.results){
                Log.i("MainActivity", "Result = ${article.title}")

                newsImages.add(article.imageURL)
                newsAdapter?.notifyItemInserted(newsImages.size - 1);
                newsTitles.add(article.title)
                newsAdapter?.notifyItemInserted(newsTitles.size - 1);
                newsDescription.add(article.description)
                newsAdapter?.notifyItemInserted(newsDescription.size - 1);
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext,
                "Провал",
                Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun showEditItemDialog(position: Int) {
        val editText = EditText(this)
        //Показываем старое значение из листа
        editText.setText(shoppingList[position])
        //Создание окна с редактированием продукта
        android.app.AlertDialog.Builder(this)
            .setTitle("Редактировать продукт")
            .setView(editText) //Создание лисенера-лямбда-выражение, который изменяет элемент
            .setPositiveButton("Сохранить") { _, _ ->
                val updatedItem = editText.text.toString()
                //если пользователь редактирует строку до пустой, удаляем ее
                if (updatedItem == "") {
                    shoppingList.removeAt(position)
                    newsAdapter?.notifyItemRemoved(position)
                } else {
                    shoppingList[position] = updatedItem
                    //Сообщаем адаптеру, что элемент изменен, и он перерисовывает эту позицию на экране
                    newsAdapter?.notifyItemChanged(position)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }*/
}