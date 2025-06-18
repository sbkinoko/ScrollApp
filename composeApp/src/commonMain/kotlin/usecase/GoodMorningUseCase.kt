package usecase

import repository.GreetingRepository

class GoodMorningUseCase(
    private val repository: GreetingRepository
) {
    operator fun invoke(): String {
        return repository.greeting()
    }
}
