package com.example.we_vote.domain.model

enum class ApplicationStatus(val displayName: String) {
    PENDING("Опрос подан на рассмотрение"),
    ACCEPTED("Опрос принят"),
    REJECTED("Опрос отклонён"),
    CANCELLED("Заявка отменена")
}
