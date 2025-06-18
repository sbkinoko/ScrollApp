package domain

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Counter : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var count: Int = 0
}
