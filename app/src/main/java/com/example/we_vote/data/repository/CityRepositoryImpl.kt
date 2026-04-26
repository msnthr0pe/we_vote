package com.example.we_vote.data.repository

import com.example.we_vote.data.remote.api.AuthApi
import com.example.we_vote.data.remote.dto.CityDto
import com.example.we_vote.domain.repository.CityRepository

class CityRepositoryImpl(private val api: AuthApi) : CityRepository {
    override suspend fun getCities(): List<String> = api.getCities().map { it.name }
    override suspend fun addCity(name: String) { api.addCity(CityDto(name)) }
    override suspend fun deleteCity(name: String) { api.deleteCity(CityDto(name)) }
}
