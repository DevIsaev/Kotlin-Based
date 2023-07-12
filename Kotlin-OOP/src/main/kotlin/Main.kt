fun main(args: Array<String>) {
    //классы и объекты
    /*val class1=UserClass1()
    class1.priting()
    class1.fName="Ilya"
    class1.sName="Isaev"
    class1.priting()

    val class2= UserClass2("Joe","Done")
    class2.priting()

    val class3= UserClass3("Holly","Ban")
    class3.priting()

    val class4=UserClass4()
    val class41=UserClass4("Ilya")
    val class31= UserClass3()
    class31.priting()

    val class5=UserClass5()
    class5.login="log"
    println(class5.login)
    class5.login=null
    println(class5.login)

    val class6=UserClass6()
    class6.login="log"
    println(class6.login)
    class6.login=null
    println(class6.login)*/


    //абстрактные классы и интерфейсы
    /*val u1=CreateInterface()
    u1.printInfo(UserClass6())
    u1.printInfo(UserClass6("Ilya","Isaev"))
    println(u1.getConnection())

    CheckingDateTypes(u1)*/


    //наследование классов и перечисление
    //val u2=NewClass()
    //CheckingDateTypes(u2)

    /*val u3=object : CreateInterface(){
        override fun printInfo(user: UserClass6) {
            //super.printInfo(user)
            println("изменено через класс наследника")
        }

        override val db: String
            get() = "connected from object class"
    }
    CheckingDateTypes(u3)

    var a=Animals.BEAR

    when(a){
        Animals.BEAR-> println(a.tLC())
        Animals.LION-> println("Лев")
        else -> println("Что то другое")
    }*/


    // Классы данных, изолированные классы
    val test=Some()
    val test1=Some()
    val test2=Some()
    val test3=Some()

    val MySql=SealedClass.MySQL(345,"connection")
    val MongoDb=SealedClass.MongoDB(34345,"connection")
    val PostgressSQL=SealedClass.PostgressSQL(345,"connection",true)

    val db_copy=MySql.copy()
    if (MySql==db_copy){
        println("равны")
    }
    else{
        println("не равны")
    }

    val db_copy2=MySql.copy(con="Done")
    if (MySql==db_copy2){
        println("равны")
    }
    else{
        println("не равны")
    }

    if (MongoDb is SealedClass.MongoDB){
        MongoDb.prnting()
    }


    val list= listOf("PSP","PS2","PS3","PSVITA","PSONE")
    filterList(list,{it.endsWith("3")})


}

fun CheckingDateTypes(obj: UserInterface){
    if(obj is UserInterface){
        println(true)
        obj.printInfo(UserClass6())
    }
    if(obj is DBConnection){
        println(true)
        println(obj.getConnection())
    }
}

fun filterList(list: List<String>,filter: (String)->Boolean){
    list.forEach{el->if (filter(el)) println(el) }
}
