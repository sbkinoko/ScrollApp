package viewmodel

import repository.count.CountRepositoryImpl

import org.koin.core.component.KoinComponent
import repository.count.CountRepository
import usecase.GoodMorningUseCase
import usecase.IncCountUseCase

class KmpViewModel(
    private val useCase: GoodMorningUseCase,
) : KoinComponent {
    val greeting: String
        get() = useCase()

    private val countRepository: CountRepository = CountRepositoryImpl()

    private val incCountUseCase = IncCountUseCase(
        countRepository = countRepository
    )

    val countFlow = countRepository.mutableCountFlow

    fun addCount() {
        incCountUseCase.invoke()
    }
}
