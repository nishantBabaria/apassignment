package com.assignment.base.network

import com.assignment.home.modal.ModelMedia
import retrofit2.Response

class MainRepository {
    suspend fun hitGetMedia(): Response<List<ModelMedia>> {
        return NetworkUtils.api.getMedia()
    }
}