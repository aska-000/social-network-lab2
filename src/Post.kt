import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Post(
    val author: User,
    val text: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    private val _ratings = mutableListOf<Rating>()
    val ratings: List<Rating> get() = _ratings.toList()

    val averageRating: Double
        get() = if (_ratings.isEmpty()) 0.0 else _ratings.map { it.value }.average()

    fun rate(by: User, value: Int): Boolean {
        if (by == author) return false
        _ratings.removeAll { it.user == by }
        _ratings.add(Rating(by, value))
        return true
    }

    override fun toString(): String {
        val fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm")
        return "[${createdAt.format(fmt)}] ${author.name}: \"$text\" " +
                "(средняя: ${"%.2f".format(averageRating)}, оценок: ${ratings.size})"
    }
}