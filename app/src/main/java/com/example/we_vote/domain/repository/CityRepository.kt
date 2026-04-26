package com.example.we_vote.domain.repository

interface CityRepository {
    suspend fun getCities(): List<String>
    suspend fun addCity(name: String)
    suspend fun deleteCity(name: String)
}
