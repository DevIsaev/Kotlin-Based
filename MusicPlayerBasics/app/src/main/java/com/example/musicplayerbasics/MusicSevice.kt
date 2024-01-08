package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat

class MusicSevice: Service() {
    //сервис, принимающий поток и позволящий воспроизводить фоном
    private var myBinder=MyBinder()
    var mediaPlayer:MediaPlayer?=null
    private lateinit var mediasssion:MediaSessionCompat
    private lateinit var runnable:Runnable

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
    fun showNotification(PlayPause: Int){
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        }
        else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        //связь с кнопками из ApplicationClass
        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)


    //загрузка изображения
    var imgArt= getImage(PlayerFragment.musicListPA[PlayerFragment.songPosition].path)
    val Art =if (imgArt!=null){
        BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
    }
    else{
        BitmapFactory.decodeResource(resources, R.drawable.icon)
    }


//построение элемента уведомления
    val notification = androidx.core.app.NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)//канал
            .setContentTitle(PlayerFragment.musicListPA[PlayerFragment.songPosition].title)
            .setContentText(PlayerFragment.musicListPA[PlayerFragment.songPosition].artist)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setLargeIcon(Art)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediasssion.sessionToken))
            .setOnlyAlertOnce(true)
            .setSound(null)
            .addAction(R.drawable.baseline_skip_previous_24, "Previous", prevPendingIntent)
            .addAction(PlayPause, "Play", playPendingIntent)
            .addAction(R.drawable.baseline_skip_next_24, "Next", nextPendingIntent)
            .addAction(R.drawable.baseline_clear_24, "Exit", exitPendingIntent)
            .build()

    startForeground(13, notification)

    }
    fun createMP(){
        try {
            if (PlayerFragment.musicService!!.mediaPlayer == null) PlayerFragment.musicService!!.mediaPlayer = MediaPlayer()
            PlayerFragment.musicService!!.mediaPlayer!!.reset()
            PlayerFragment.musicService!!.mediaPlayer!!.setDataSource(PlayerFragment.musicListPA[PlayerFragment.songPosition].path)
            PlayerFragment.musicService!!.mediaPlayer!!.prepare()

            PlayerFragment.binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
            PlayerFragment.musicService!!.showNotification(R.drawable.baseline_pause_24)

            PlayerFragment.binding.durationCURRENT.text= DurationFormat(mediaPlayer!!.currentPosition.toLong())
            PlayerFragment.binding.durationEND.text= DurationFormat(mediaPlayer!!.duration.toLong())
            PlayerFragment.binding.SeekBarDuration.progress=0
            PlayerFragment.binding.SeekBarDuration.max= mediaPlayer!!.duration
        }
        catch (ex:Exception){
            return
        }
    }

    fun seekBarSetup(){
        runnable= Runnable {
            PlayerFragment.binding.durationCURRENT.text= DurationFormat(mediaPlayer!!.currentPosition.toLong())
            PlayerFragment.binding.SeekBarDuration.progress=mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }
}