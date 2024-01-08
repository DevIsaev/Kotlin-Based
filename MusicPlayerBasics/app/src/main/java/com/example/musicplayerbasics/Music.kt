package com.example.musicplayerbasics

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit

data class Music(val id:String,val title:String,val artist:String,val album:String, val duration:Long=0, val path:String, val artURI:String)

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