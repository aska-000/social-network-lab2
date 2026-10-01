import java.util.Scanner

class ConsoleApp(private val scanner: Scanner) {

    private var currentUser: User? = null

    fun run() {
        printHelp()
        while (true) {
            print("\n> ")
            val line = scanner.nextLine().trim()
            if (line.isEmpty()) continue

            val parts = line.split(" ", limit = 2)
            val command = parts[0].lowercase()
            val arg = parts.getOrNull(1)?.trim() ?: ""

            try {
                when (command) {
                    "reg"    -> register(arg)
                    "logout" -> logout()
                    "post"   -> publish(arg)
                    "my"     -> myPosts()
                    "sub"    -> subscribe(arg)
                    "feed"   -> showFeed()
                    "rate"   -> rate(arg)
                    "notes"  -> showNotifications()   // ← новая команда (по желанию)
                    "exit", "quit" -> { println("Выход."); return }
                    else -> println("Неизвестная команда. Введите 'exit' для выхода.")
                }
            } catch (e: Exception) {
                println("Ошибка: ${e.message}")
            }
        }
    }



    private fun register(name: String) {
        if (name.isBlank()) { println("Укажите имя: reg <имя>"); return }
        val u = SocialNetwork.register(name)
        currentUser = u
        println("Пользователь '${u.name}' зарегистрирован и выполнен вход.")
        showNotifications()
    }

    private fun logout() {
        if (currentUser == null) println("Вы не авторизованы.")
        else { println("Выход: ${currentUser!!.name}"); currentUser = null }
    }

    private fun publish(text: String) {
        val user = requireUser() ?: return
        if (text.isBlank()) { println("Введите текст: post <текст>"); return }
        val post = user.publish(text)
        println("Опубликовано: $post")
    }

    private fun myPosts() {
        val user = requireUser() ?: return
        if (user.posts.isEmpty()) { println("У вас нет записей."); return }
        println("Мои записи")
        user.posts.reversed().forEachIndexed { i, p -> println("${i + 1}. $p") }
    }

    private fun subscribe(name: String) {
        val user = requireUser() ?: return
        if (name.isBlank()) { println("Укажите имя: sub <имя>"); return }
        if (name == user.name) { println("Нельзя подписаться на себя."); return }
        val target = SocialNetwork.findUser(name)
        if (target == null) { println("Пользователь '$name' не найден."); return }
        user.subscribeTo(target)
        println("${user.name} подписан на ${target.name}.")
    }

    private fun showFeed() {
        val user = requireUser() ?: return
        val feed = SocialNetwork.feedFor(user)
        if (feed.isEmpty()) { println("Лента пуста."); return }
        println("Лента ${user.name}")
        feed.forEach { println(it) }
    }

    private fun rate(arg: String) {
        val user = requireUser() ?: return
        val tokens = arg.split(" ")
        if (tokens.size != 3) {
            println("Формат: rate <автор> <номер_записи> <1..5>"); return
        }
        val author = SocialNetwork.findUser(tokens[0])
            ?: run { println("Автор '${tokens[0]}' не найден."); return }

        val index = tokens[1].toIntOrNull()
        val value = tokens[2].toIntOrNull()
        if (index == null || value == null) { println("Некорректные числа."); return }

        val posts = author.posts.reversed()
        if (index !in 1..posts.size) { println("Записи с номером $index нет."); return }

        val ok = posts[index - 1].rate(user, value)
        if (ok) println("Оценка $value выставлена: ${posts[index - 1]}")
        else println("Нельзя оценивать собственную запись.")
    }

    private fun showNotifications() {
        val user = currentUser ?: return
        val notes = user.pullNotifications()
        if (notes.isEmpty()) return
        println("Новые уведомления для ${user.name}")
        notes.forEach { println("$it") }
    }


    private fun requireUser(): User? {
        val u = currentUser
        if (u == null) println("Сначала зарегистрируйтесь: reg <имя>")
        return u
    }

    private fun printHelp() {
        println(
            """
            Социальная сеть 
            reg <имя>                  — создать пользователя и войти
            logout                     — выйти из аккаунта
            post <текст>               — опубликовать запись
            my                         — мои записи
            sub <имя>                  — подписаться на пользователя
            feed                       — лента подписок
            rate <автор> <№> <1..5>    — оценить запись
            notes                      — показать новые уведомления
            exit                       — выход
            """.trimIndent()
        )
    }
}