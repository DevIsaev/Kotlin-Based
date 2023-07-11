interface UserInterface {
    val info:String

    fun printInfo(user: UserClass6){
        println("метод вызван")
        /*        user.fName="BaseName"
                user.sName="BaseSurname"*/
        println(info)
        user.printing()
    }

}
interface DBConnection{
    fun getConnection():String
}





class CreateInterface:UserInterface,DBConnection{
    override val info: String
        get() = "переменная была изменена"

    override fun printInfo(user: UserClass6) {
        /*println("метод вызван")
*//*        user.fName="BaseName"
        user.sName="BaseSurname"*//*
        user.printing()*/
        super.printInfo(user)
        println("дополнительный код функции")
    }

    override fun getConnection(): String {
        return "DB connected"
    }
}

