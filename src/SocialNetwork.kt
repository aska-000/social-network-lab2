object SocialNetwork {

    private val users = mutableMapOf<String, User>()

    fun register(name: String): User = users.getOrPut(name) { User(name) }
    fun findUser(name: String): User? = users[name]

    fun feedFor(user: User, limit: Int = 10): List<Post> =
        user.subscriptionsList
            .flatMap { it.posts }
            .sortedByDescending { it.createdAt }
            .take(limit)
}