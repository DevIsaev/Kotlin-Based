class NewClass:CreateInterface() {
    override val info: String
        get() = "изменение переменной в классе наследнике"

    override fun printInfo(user: UserClass6) {
        super.printInfo(user)
        println("текст из класса наследника")
    }

    override val db: String
        get() = "connected from class"
    override val close: String
        get() = "close only in class and interface"
}