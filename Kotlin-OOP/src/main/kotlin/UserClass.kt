class UserClass1 {
    var fName:String="Name"
    var sName:String="Surnamee"
    fun priting(){
        println("$fName $sName")
    }
}
class UserClass2(fName:String,sName:String) {
    var f:String
    var s:String
    init {
        f=fName
        s=sName
    }
    fun priting(){
        println("$f $s")
    }
}
class UserClass3(var fName:String="Name",var sName:String ="Surname") {

    fun priting(){
        println("$fName $sName")
    }
}
class UserClass4(var fName:String,var sName:String) {

   constructor():this("Name","Surname"){
       println("пусто")
   }
    constructor(fName: String):this(fName,"Surname"){
        println("только имя")
    }
}
class UserClass5(var fName:String="Name",var sName:String ="Surname"){
    var login:String?=null

        set(value) {
            field=value
            if (value==null||value==""){
                println("логин пустой")
            }
            else{
                field=value
                println("значение $field")
            }
        }
        get(){
            println("значение $field")
            return field
        }
}
class UserClass6(var fName:String="Name",var sName:String ="Surname"){
    var login:String?=null

        set(value) {
            field=value
            if (value==null||value==""){
                println("логин пустой")
            }
            else{
                field=value
                println("значение $field")
            }
        }
        get(){
            val loginField=field?:"неизвестно"
            println("значение $loginField")
            return field
        }
    fun printing(){
        println("$fName $sName")
    }
}