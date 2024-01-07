package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat

class MusicSevice: Service() {

    private var myBinder=MyBinder()
    var mediaPlayer:MediaPlayer?=null
    private lateinit var mediasssion:MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder {
        mediasssion= MediaSessionCompat(baseContext,"My Music")
        return  myBinder
    }

    inner class  MyBinder:Binder(){
        fun currentService():MusicSevice{
            return this@MusicSevice
        }
    }

    @SuppressLint("ForegroundServiceType")
    fun showNotification(){
        val notification = androidx.core.app.NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerFragment.musicListPA[PlayerFragment.songPosition].title)
            .setContentText(PlayerFragment.musicListPA[PlayerFragment.songPosition].artist)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediasssion.sessionToken))
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.baseline_skip_previous_24, "Previous", null)
            .addAction(R.drawable.baseline_play_arrow_24, "Play", null)
            .addAction(R.drawable.baseline_skip_next_24, "Next", null)
            .addAction(R.drawable.baseline_clear_24, "Exit", null)
            .build()
        startForeground(13,notification)
    }
}