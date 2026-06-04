package com.example.fooddeliveryapp.core.data.domain

import com.example.fooddeliveryapp.core.data.models.Country
import com.example.fooddeliveryapp.core.data.models.toCountryOrNull
import com.example.fooddeliveryapp.core.data.remote.RestCountriesApi
import com.example.fooddeliveryapp.feature.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

interface CountryRepository {
    suspend fun fetchCountries(): Flow<RequestState<List<Country>>>
}

class CountryRepositoryImpl(
    private val api: RestCountriesApi
) : CountryRepository {
    override suspend fun fetchCountries(): Flow<RequestState<List<Country>>> = flow {
        try {


            emit(RequestState.Loading)
            val countries = withContext(Dispatchers.IO) {
                api.getAll()
                    .mapNotNull { it.toCountryOrNull() }
                    .distinctBy { it.code }
                    .sortedBy { it.name }
            }
            emit(RequestState.Success(countries))
        } catch (e: Exception) {
            emit(RequestState.Error("Can not access the API endpoint: ${e.message ?: "Unkown Error"}"))
        }
    }
}