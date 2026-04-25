package com.example.we_vote.domain.usecase.application

import com.example.we_vote.domain.model.ApplicationStatus
import com.example.we_vote.domain.repository.ApplicationRepository

class UpdateApplicationStatusUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(id: Int, status: ApplicationStatus) =
        repository.updateApplicationStatus(id, status)
}
