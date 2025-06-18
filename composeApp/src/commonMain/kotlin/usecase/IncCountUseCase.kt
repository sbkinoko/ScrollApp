package usecase

import repository.count.CountRepository

class IncCountUseCase(
    private val countRepository: CountRepository
) {
    operator fun invoke() {
        countRepository.count++
    }
}
