class User1 {
    var fName="Alex"
    var sName="Johan"

    fun printing(){
        println("fun priting ${fName} ${sName}")
    }
}
class User2(fName:String, sName:String){
    var first=fName
    var second=sName

    fun printing(){
        println("fun priting ${first} ${second}")
    }
}

class User3(fName:String, sName:String){
    constructor():this("Name","Surnme"){
       println("Не введены данные")
    }
    constructor(fName: String):this(fName,"Surnme"){
        println("только имя")
    }
/*    constructor(sName: String):this("Name",sName){
        println("только фамилия")
    }*/
}
class User4(var fName:String="Peter", var sName:String="Parker") {
    var login:String?=null

        set(value){
            if (value=="login")
                field="None"
            else
                field=value
            println("значение переменной - $field")
        }
        get(){
            val loginField=field?:"неизвестно"
            println("получаемая переменная - $loginField")
            return field
        }
}