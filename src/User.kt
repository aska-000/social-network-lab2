class User(val name: String) {

    private val subscribers = mutableSetOf<Observer>()
    private val subscriptions = mutableSetOf<User>()
    private val _posts = mutableListOf<Post>()

    val posts: List<Post> get() = _posts.toList()
    val subscriptionsList: List<User> get() = subscriptions.toList()

    fun subscribeTo(other: User) {
        if (other == this) return
        if (subscriptions.add(other)) {
            other.subscribers.add(object : Observer {
                override fun onNewPost(post: Post) {
                    println("[уведомление для $name] ${post.author.name} опубликовал: \"${post.text}\"")
                }
            })
        }
    }

    fun publish(text: String): Post {
        val post = Post(this, text)
        _posts.add(post)
        subscribers.forEach { it.onNewPost(post) }
        return post
    }

    override fun equals(other: Any?) = other is User && other.name == name
    override fun hashCode() = name.hashCode()
    override fun toString() = name
}