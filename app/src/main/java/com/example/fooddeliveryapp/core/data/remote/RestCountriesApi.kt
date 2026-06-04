package com.example.fooddeliveryapp.core.data.remote

import com.example.fooddeliveryapp.core.data.models.RestCounttriesDto
import com.google.firebase.firestore.pipeline.Field
import retrofit2.http.GET
import retrofit2.http.Query

interface RestCountriesApi {
    @GET("v3.1/all")
    suspend fun  getAll(
        @Query("fields") field: String = "name,idd,flags,cca2"
    ): List<RestCounttriesDto>
}