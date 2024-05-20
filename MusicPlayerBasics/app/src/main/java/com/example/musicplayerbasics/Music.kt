package com.example.musicplayerbasics

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AlertDialog
import com.google.android.material.color.MaterialColors
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(val id:String,val title:String,val artist:String,val album:String, val duration:Long=0, val path:String, val artURI:String,var count:Int=0){
    // Метод для увеличения счетчика воспроизведений
//    fun incrementPlayCount() {
//        count++
//    }
    fun incrementPlayCount(context: Context) {
        // Загрузка значения count из SharedPreferences
        val prefs: SharedPreferences = context.getSharedPreferences("MusicCountPrefs", Context.MODE_PRIVATE)
        count = prefs.getInt(id, 0)

        // Увеличение значения count на единицу
        count++

        // Сохранение обновленного значения count в SharedPreferences
        val editor = prefs.edit()
        editor.putInt(id, count)
        editor.apply()
    }
}

class Playlist{
    lateinit var name:String
    lateinit var playlist:ArrayList<Music>
    lateinit var createdBy:String
    lateinit var createdOn:String
}
class PlaylistMusic{
    var ref:ArrayList<Playlist> = ArrayList()
}

fun  DurationFormat(duration: Long):String{
    val minutes=TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds=(TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-
            minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
    return String.format("%02d:%02d",minutes, seconds)
}
fun getImage(path: String): ByteArray? {
    var retriver=MediaMetadataRetriever()
    retriver.setDataSource(path)
    return retriver.embeddedPicture
}
fun songPositionPN(increment: Boolean){
    if(!PlayerFragment.repeat){
        if(increment)
        {
            if(PlayerFragment.musicListPA.size - 1 == PlayerFragment.songPosition)
                PlayerFragment.songPosition = 0
            else ++PlayerFragment.songPosition
        }else{
            if(0 == PlayerFragment.songPosition)
                PlayerFragment.songPosition = PlayerFragment.musicListPA.size-1
            else --PlayerFragment.songPosition
        }
    }
}

fun favouriteCheck(id: String): Int{
    PlayerFragment.isFavourite = false
    FavouritesFragment.favSong.forEachIndexed { index, music ->
        if(id == music.id){
            PlayerFragment.isFavourite = true
            return index
        }
    }
    return -1
}

fun playlistCheck(playlist: ArrayList<Music>):ArrayList<Music>{

    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist

}
fun exitApp(){
    if(PlayerFragment.musicService!=null){
        PlayerFragment.musicService!!.audioManager.abandonAudioFocus(PlayerFragment.musicService)
        PlayerFragment.musicService!!.stopForeground(true)
        PlayerFragment.musicService!!.mediaPlayer!!.release()
        PlayerFragment.musicService=null
    }
    exitProcess(1)
}


//fun getMainColor(img: Bitmap): Int {
//    val newImg = Bitmap.createScaledBitmap(img, 1,1 , false)
//    val color = newImg.getPixel(0, 0)
//    newImg.recycle()
//    return color
//}
fun getMainColor(img: Bitmap): Int {
    val maxAttempts = 50 // Максимальное количество попыток
    var attempts = 0 // Счетчик попыток
    var dstWidth = 1
    var dstHeight = 1
    var newImg = Bitmap.createScaledBitmap(img, dstWidth, dstHeight, false)
    var color = newImg.getPixel(0, 0)

    // Проверяем, является ли цвет белым или похожим на белый
    while (isWhite(color) && attempts < maxAttempts) {
        // Генерируем случайные значения для dstWidth и dstHeight
        dstWidth = (Math.random() * img.width).toInt() + 1
        dstHeight = (Math.random() * img.height).toInt() + 1
        // Пересоздаем изображение с новыми размерами
        newImg.recycle()
        newImg = Bitmap.createScaledBitmap(img, dstWidth, dstHeight, false)
        color = newImg.getPixel(0, 0)
        attempts++
    }

    newImg.recycle()
    return color
}

// Функция для проверки, является ли цвет белым или похожим на белый
fun isWhite(color: Int): Boolean {
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return red >= 200 && green >= 200 && blue >= 200
}
fun setDialogBtnBackground(context: Context, dialog: AlertDialog){
    //setting button text
    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(
        MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
    )
    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
        MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
    )

    //setting button background
    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
    )
    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
    )
}
fun saveMusicCounts(ALM: ArrayList<Music>,context:Context) {
    val prefs: SharedPreferences = context.getSharedPreferences("MusicCountPrefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    for (music in ALM) {
        editor.putInt(music.title, music.count)
    }
    editor.apply()
}

fun loadMusicCounts(ALM: ArrayList<Music>,context:Context) {
    val prefs: SharedPreferences =  context.getSharedPreferences("MusicCountPrefs", Context.MODE_PRIVATE)
    for (music in ALM) {
        music.count = prefs.getInt(music.id, 0)
    }
}

//fun saveMusicListMA(context: Context, musicList: ArrayList<Music>) {
//    val sharedPreferences = context.getSharedPreferences("MusicListMAPref", Context.MODE_PRIVATE)
//    val editor = sharedPreferences.edit()
//    val gson = Gson()
//    val json = gson.toJson(musicList)
//    editor.putString("MusicListMA", json)
//    editor.apply()
//    Toast.makeText(context,"saved!",Toast.LENGTH_SHORT).show()
//}
//fun loadMusicListMA(context: Context): ArrayList<Music> {
//    val sharedPreferences = context.getSharedPreferences("MusicListMAPref", Context.MODE_PRIVATE)
//    val gson = Gson()
//    val json = sharedPreferences.getString("MusicListMA", null)
//    val type = object : TypeToken<ArrayList<Music>>() {}.type
//    return gson.fromJson(json, type) ?: ArrayList()
//    Toast.makeText(context,"load!",Toast.LENGTH_SHORT).show()
//}