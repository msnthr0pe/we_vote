package com.example.we_vote.domain.usecase.city

import com.example.we_vote.domain.repository.CityRepository

class GetCitiesUseCase(private val repository: CityRepository) {
    suspend operator fun invoke(): List<String> = repository.getCities()
}
