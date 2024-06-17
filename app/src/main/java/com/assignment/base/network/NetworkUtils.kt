package com.assignment.base.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkUtils {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://acharyaprashant.org/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}