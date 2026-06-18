package com.example.fooddeliveryapp.core.data.remote

import com.example.fooddeliveryapp.core.data.models.RestCounttriesDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RestCountriesApi {
    @GET("all")
    suspend fun getAll(
        @Query("fields") fields: String = "name,idd,flags,cca2"
    ): List<RestCounttriesDto>
}
