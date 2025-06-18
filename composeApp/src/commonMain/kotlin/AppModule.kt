import org.koin.dsl.module
import viewmodel.KmpViewModel

val AppModule = module {

    single<KmpViewModel> {
        KmpViewModel()
    }
}
