package repository

class GoodMorningRepository : GreetingRepository {
    override fun greeting(): String {
        return "Good Morning"
    }
}
