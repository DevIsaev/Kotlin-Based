//глобальная переменная
var str1="Hello"
var num3=8
//основная функция
fun main(args: Array<String>) {

    //
    // println("printLN")
    // print("Kotlin")
    //


        //
        //типы переменных, локальные переменные
        //var
    // var age: Int=18
    // age=19
    // println("Мой возраст - $age")
    // var name: String="Илья"
    // var end:Char='s'
    // var isTrue:Boolean=true
    // var num:Float=4.5f
    // var num2:Double = 4.556
    // var null1: Int?=null
    // var name2="Григорий"
    // println(name+end)
    // println(isTrue)
    // println(num)
    // println(num2)
    // var res=num2+num3
    // res+=10
    // res--
    // println(res)

        //const(val)
    // val number:Int=4
        //


        //
        //условные операторы
        //if else
    /*
    var str:String?=null
    var isHas=true

    str="Hello"
    if(str==null) {
        println("str пустое")

        if (isHas==true) {
            println("isHas = true")
        }
        else{
            println("isHas=false")
        }

    }
    else if(str=="Hello"){
        println(str)
    }
    else{
        println("str не пустое")
    }

        //when
    val num=5
    when(num){
        null-> println("num = null")
        10-> println(num)
        5-> println(num)
        else -> println("не то")
    }

    var res=if (num>30) 1 else 0
    println(res)

    var words=when(num){
        null-> "null"
        else->num
    }
    println(words)*/
        //

        //
        //функции
    /*fun1()
    fun2("Test",1)
    println(fun3(56,44))
    println(fun4(true))*/
        //

        //
        //массивы
    var items: Array<Int>
    items= arrayOf(6,89,32,1,5)
    /*println(items.set(0,34))
    println(items.get(1))
    println(items.size)
    println(items[0])*/

/*for(el in items){
    println("el__________")
    println(el)
}
items.forEach {
    el2->
    println("el2----------")
    println(el2)
}
items.forEachIndexed{index,el3->
    println("el3-----------")
    println("el3 $index - $el3")
}*/

        //список данных
    /*var list= listOf(56,89,90,56)
    println( list.last())
    println(list.indexOf(89))
    println(list.lastIndexOf(90))

    var users= mapOf("Name" to "Billy", "age" to 15, "has" to true)
    users.forEach{key,value-> println("$key -> $value") }

    var list2 = mutableListOf(45,98,34,45,"5ty7")
    println(list2[0])
    list2.add(0,"tr")
    println(list2[0])

    var users2= mutableMapOf("Name" to "Billy", "age" to 15, "has" to true)
    users2.put("city","Novosibirsk")
    users2.forEach{key,value-> println("$key -> $value") }

    printArr(list2)
    println(users2)*/
        //

        //
        // параметры функции
    /*printSome2("Key","word")
    printSome2("Key","word","new")
    //printSome2("Key","word","new",8)

    var names= arrayOf("Bob","Alex","trertr")
    printSome2("Hi",*names)
    //printSome2(item = "Some", word = "Hi")*/
        //

        //циклы
    /*var items3=5
    while (items3<10){
        println(items3)
        items3++
    }

    do {
        println("i: $items3")
    }while (items3==9)

    *//*for (i in 1..10 step 2){
        println("for: $i")
    }

    for (i in 10 downTo 0 step 2){
        println("downTo: $i")
    }

    for (el in 'а'..'я' step 2){
        println(el)
    }

    for(el in 'z' downTo 'a' step 2){
        println(el)
    }*//*

    var x =20
    if (x in 5..30){
        println(x)
    }*/

        //

        //
        // классы и объекты
    /*val u1=User1()
    u1.printing()
    u1.fName="Ilya"
    u1.sName="Isaev"
    println("${u1.fName} ${u1.sName}")

    val u2=User2("Dio","Brando")
    u2.printing()

    val u31=User3()
    val u32=User3("Diko")

    val u4=User4()
    //u4.login="login"
    u4.login*/
        //

        //
        //абстрактные классы
        //
}
//
/*fun fun1(){
    println("fun1")
}
fun fun2(first:String, num:Int){
    println("$first $num")
}
fun fun3(x:Int,y:Int):Int{
    return x+y
}
fun fun4(b:Boolean):String = "Fun4-$b"*/
//

//
/*fun printArr(arr:List<Any>){
    arr.forEach{
        el->
        println("fun---------")
        println(el)
    }
}
fun printMap(arr:Map<String,Any>){
    arr.forEach{
            key,v->
        println("fun2---------")
        println("$key - $v")
    }
}*/
//

//
/*fun  printSome(vararg word:Any){
    word.forEach { el-> print("$el ")
    println("")
    }
}
fun  printSome2(item: String,vararg word:Any){
    print("$item:")
    word.forEach { el-> print("$el ")
        println("")
    }
}*/
//