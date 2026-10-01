data class Rating(val user: User, val value: Int) {
    init {
        require(value in 1..5) { "Оценка должна быть в диапазоне 1..5" }
    }
}