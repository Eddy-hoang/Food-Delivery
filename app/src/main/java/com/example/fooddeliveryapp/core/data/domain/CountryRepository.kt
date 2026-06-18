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
                // Nếu API trả về lỗi cấu trúc (Object thay vì Array), 
                // Retrofit sẽ ném Exception và nhảy xuống khối catch.
                api.getAll()
                    .mapNotNull { it.toCountryOrNull() }
                    .distinctBy { it.code }
                    .sortedBy { it.name }
            }
            emit(RequestState.Success(countries))
        } catch (e: Exception) {
            // Luôn trả về danh sách dự phòng để tính năng Profile không bị treo/lỗi
            emit(RequestState.Success(getFallbackCountries()))
        }
    }

    private fun getFallbackCountries(): List<Country> {
        return listOf(
            Country("VN", "Vietnam", 84, "https://flagcdn.com/w320/vn.png"),
            Country("US", "United States", 1, "https://flagcdn.com/w320/us.png"),
            Country("KR", "South Korea", 82, "https://flagcdn.com/w320/kr.png"),
            Country("JP", "Japan", 81, "https://flagcdn.com/w320/jp.png"),
            Country("SG", "Singapore", 65, "https://flagcdn.com/w320/sg.png"),
            Country("AU", "Australia", 61, "https://flagcdn.com/w320/au.png"),
            Country("GB", "United Kingdom", 44, "https://flagcdn.com/w320/gb.png"),
            Country("CA", "Canada", 1, "https://flagcdn.com/w320/ca.png"),
            Country("FR", "France", 33, "https://flagcdn.com/w320/fr.png"),
            Country("DE", "Germany", 49, "https://flagcdn.com/w320/de.png")
        ).sortedBy { it.name }
    }
}
