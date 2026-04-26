package com.example.we_vote.domain.usecase.city

import com.example.we_vote.domain.repository.CityRepository

class DeleteCityUseCase(private val repository: CityRepository) {
    suspend operator fun invoke(name: String) = repository.deleteCity(name)
}
