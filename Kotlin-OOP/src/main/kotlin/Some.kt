import java.util.logging.Filter

class Some {
    companion object{
        var count=0
    }
    init {
        count++
        println("объектов $count")
    }
}


sealed class SealedClass{
    data class MySQL(val id:Int, val con:String):SealedClass()
    data class MongoDB(val id:Int, val con:String):SealedClass(){
        /*fun prnting(){
            println("MongoDB id:$id,con:$con")
        }*/
    }
    data class PostgressSQL(val id:Int, val con:String, val isDone:Boolean):SealedClass()

    object Help:SealedClass(){
        val con="Connection done"
    }
}

val SealedClass.MongoDB.info:String
    get()="MongoDB id:$id,con:$con"

fun SealedClass.MongoDB.prnting(){
    println(info)
}


