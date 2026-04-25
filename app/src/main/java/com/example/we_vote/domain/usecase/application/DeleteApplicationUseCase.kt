package com.example.we_vote.domain.usecase.application

import com.example.we_vote.domain.repository.ApplicationRepository

class DeleteApplicationUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(id: Int) = repository.deleteApplication(id)
}
