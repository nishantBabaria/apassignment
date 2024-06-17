package com.assignment.base.network

import com.assignment.home.modal.ModelMedia
import retrofit2.http.GET
import retrofit2.Response

interface ApiService {
    @GET("content/misc/media-coverages?limit=100")
    suspend fun getMedia(): Response<List<ModelMedia>>
}