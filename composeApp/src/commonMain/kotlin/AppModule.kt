import org.koin.dsl.module
import repository.GoodMorningRepository
import repository.GreetingRepository
import usecase.GoodMorningUseCase
import viewmodel.KmpViewModel

val AppModule = module {
    single<GreetingRepository> {
        GoodMorningRepository()
    }

    single<GoodMorningUseCase> {
        GoodMorningUseCase(
            repository = get(),
        )
    }

    single<KmpViewModel> {
        KmpViewModel(
            useCase = get(),
        )
    }
}
