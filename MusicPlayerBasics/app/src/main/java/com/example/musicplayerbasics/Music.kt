package com.example.musicplayerbasics

import android.media.MediaMetadataRetriever
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(val id:String,val title:String,val artist:String,val album:String, val duration:Long=0, val path:String, val artURI:String)

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
fun songPosition(increment: Boolean){
    if(!PlayerFragment.repeat){
        if (increment){
            if(PlayerFragment.musicListPA.size-1== PlayerFragment.songPosition)
                PlayerFragment.songPosition =0
            else ++PlayerFragment.songPosition
        }
        else{
            if(0== PlayerFragment.songPosition)
                PlayerFragment.songPosition = PlayerFragment.musicListPA.size-1
            else --PlayerFragment.songPosition
        }
    }
}

fun favouriteCheck(id: String): Int{
    PlayerFragment.isFavourite = false
    FavouritesActivity.favSong.forEachIndexed { index, music ->
        if(id == music.id){
            PlayerFragment.isFavourite = true
            return index
        }
    }
    return -1
}

fun playlistCheck(playlist: ArrayList<Music>):ArrayList<Music>{

//    var index: Int
//    index = 0
//    while (index < playlist.size) {
//        val file=File(playlist[index].path)
//        if (!file.exists()){
//            playlist.removeAt(index)
//        }
//        index++
//    }
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist

}
fun exitApp(){
    if(PlayerFragment.musicService!=null){
        //PlayerFragment.musicService!!.audioManager.abandonAudioFocus(PlayerFragment.musicService)
        PlayerFragment.musicService!!.stopForeground(true)
        PlayerFragment.musicService!!.mediaPlayer!!.release()
        PlayerFragment.musicService=null
    }
    exitProcess(1)
}

