package repository

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GoodMorningRepositoryTest {
    private lateinit var repository: GreetingRepository

    @BeforeTest
    fun beforeTest() {
        repository = GoodMorningRepository()
    }

    @Test
    fun checkValue() {
        assertEquals(
            "Good Morning",
            repository.greeting(),
        )
    }
}
