fun main(args: Array<String>) {
    //классы и объекты
    val class1=UserClass1()
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
    println(class6.login)

    //абстрактные классы и интерфейсы

    
}