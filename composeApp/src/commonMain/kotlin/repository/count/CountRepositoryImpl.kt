package repository.count

import domain.Counter
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class CountRepositoryImpl : CountRepository {
    override val mutableCountFlow = MutableSharedFlow<Int>(
        replay = 1,
    )

    private val config: RealmConfiguration =
        RealmConfiguration.create(schema = setOf(Counter::class))
    private val realm: Realm = Realm.open(config)
    private val counter: Counter =
        realm.query<Counter>().first().find() ?: realm.writeBlocking {
            copyToRealm(
                Counter()
            )
        }

    override var count: Int = 0
        set(value) {
            field = value
            CoroutineScope(Dispatchers.Default).launch {
                mutableCountFlow.emit(value)
            }
            realm.writeBlocking {
                findLatest(counter)?.apply {
                    count = value
                }
            }
        }

    init{
        count = counter.count
    }
}
