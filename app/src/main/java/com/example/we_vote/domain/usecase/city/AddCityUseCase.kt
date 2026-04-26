package com.example.we_vote.domain.usecase.city

import com.example.we_vote.domain.repository.CityRepository

class AddCityUseCase(private val repository: CityRepository) {
    suspend operator fun invoke(name: String) = repository.addCity(name)
}
