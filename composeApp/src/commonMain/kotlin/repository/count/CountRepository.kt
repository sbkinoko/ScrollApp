package repository.count

import kotlinx.coroutines.flow.Flow

interface CountRepository {
    val mutableCountFlow: Flow<Int>
    var count: Int
}
