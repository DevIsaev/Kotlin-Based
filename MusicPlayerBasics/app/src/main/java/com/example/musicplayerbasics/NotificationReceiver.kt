package com.example.musicplayerbasics

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationReceiver:BroadcastReceiver() {
    //воспроизведение фоном в виде уведомления
    override fun onReceive(context:  Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PLAY-> {
                if (PlayerFragment.isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }
            ApplicationClass.EXIT->{
                exitApp()
            }
            ApplicationClass.NEXT-> {
                prevNextSong(increment = true,context=context!!)
            }
            ApplicationClass.PREVIOUS->{
                prevNextSong(increment = false,context=context!!)
            }
        }
    }
    private fun playMusic(){
    PlayerFragment.isPlaying=true
    PlayerFragment.musicService!!.mediaPlayer!!.start()
    PlayerFragment.musicService!!.showNotification(R.drawable.baseline_pause_24)
    PlayerFragment.binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
        NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_pause_24)
}
    private fun pauseMusic(){
        PlayerFragment.isPlaying=false
        PlayerFragment.musicService!!.mediaPlayer!!.pause()
        PlayerFragment.musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        PlayerFragment.binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_play_arrow_24)
        NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_play_arrow_24)
    }
    private  fun prevNextSong(increment:Boolean,context: Context){
        songPosition(increment=increment)
        PlayerFragment.musicService!!.createMP()
        Glide.with(context)
            .load(PlayerFragment.musicListPA[PlayerFragment.songPosition].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(PlayerFragment.binding.albumIMG)
        PlayerFragment.binding.songTITLE.text= PlayerFragment.musicListPA[PlayerFragment.songPosition].title +"\n"+ PlayerFragment.musicListPA[PlayerFragment.songPosition].artist
        Glide.with(context)
            .load(PlayerFragment.musicListPA[PlayerFragment.songPosition].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(NowPlaying.binding.albumNP)
        NowPlaying.binding.songNP.text = PlayerFragment.musicListPA[PlayerFragment.songPosition].title
        NowPlaying.binding.artistNP.text=PlayerFragment.musicListPA[PlayerFragment.songPosition].artist
        playMusic()

        PlayerFragment.fIndex= FavouriteCheck(PlayerFragment.musicListPA[PlayerFragment.songPosition].id)
        if (PlayerFragment.isFavourite){
            PlayerFragment.binding.favouriteBTN.setImageResource(R.drawable.baseline_favorite_24)
        }
        else{
            PlayerFragment.binding.favouriteBTN.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }
}