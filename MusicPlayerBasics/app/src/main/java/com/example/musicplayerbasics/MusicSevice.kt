package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.graphics.drawable.toIcon

class MusicSevice: Service(),AudioManager.OnAudioFocusChangeListener {
    //сервис, принимающий поток и позволящий воспроизводить фоном
    private var myBinder=MyBinder()
    var mediaPlayer:MediaPlayer?=null
    private lateinit var mediasssion:MediaSessionCompat
    private lateinit var runnable:Runnable
    lateinit var audioManager:AudioManager

    override fun onBind(intent: Intent?): IBinder {
        mediasssion= MediaSessionCompat(baseContext,"My Music")
        return  myBinder
    }

    inner class  MyBinder:Binder(){
        fun currentService():MusicSevice{
            return this@MusicSevice
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(PlayPause: Int){

        val intent = Intent(baseContext, MainActivity::class.java)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        //открытие вне приложения
        val contentIntent=PendingIntent.getActivity(this@MusicSevice,0,intent,flag)

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
        } else{
            BitmapFactory.decodeResource(resources, R.drawable.icon)
        }

//построение элемента уведомления
        val notification = androidx.core.app.NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerFragment.musicListPA[PlayerFragment.songPosition].title)
            .setContentText(PlayerFragment.musicListPA[PlayerFragment.songPosition].artist)
            .setSmallIcon(R.drawable.logo)
            .setLargeIcon(Art.toIcon())
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediasssion.sessionToken))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.baseline_skip_previous_24, "Previous", prevPendingIntent)
            .addAction(PlayPause, "Play", playPendingIntent)
            .addAction(R.drawable.baseline_skip_next_24, "Next", nextPendingIntent)
            .addAction(R.drawable.baseline_clear_24, "Exit", exitPendingIntent)
            .build()


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val playbackSpeed = if(PlayerFragment.isPlaying) 1F else 0F
            mediasssion.setMetadata(MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                .build())
            val playBackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
            mediasssion.setPlaybackState(playBackState)
            mediasssion.setCallback(object: MediaSessionCompat.Callback(){


                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer!!.seekTo(pos.toInt())
                    val playBackStateNew = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
                    mediasssion.setPlaybackState(playBackStateNew)
                }
            })
        }

        startForeground(13, notification)
    }
    //вызов плеера
    fun createMP(){
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(PlayerFragment.musicListPA[PlayerFragment.songPosition].path)
            mediaPlayer!!.prepare()
            PlayerFragment.binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
            showNotification(R.drawable.baseline_pause_24)
            PlayerFragment.binding.durationCURRENT.text = DurationFormat(mediaPlayer!!.currentPosition.toLong())
            PlayerFragment.binding.durationEND.text = DurationFormat(mediaPlayer!!.duration.toLong())
            PlayerFragment.binding.SeekBarDuration.progress = 0
            PlayerFragment.binding.SeekBarDuration.max = mediaPlayer!!.duration
            PlayerFragment.nowPlayingId = PlayerFragment.musicListPA[PlayerFragment.songPosition].id
            PlayerFragment.loudnessEnhancer = LoudnessEnhancer(mediaPlayer!!.audioSessionId)
            PlayerFragment.loudnessEnhancer.enabled = true
        }catch (e: Exception){return}
    }

    //SeekBar
    fun seekBarSetup(){
        runnable= Runnable {
            PlayerFragment.binding.durationCURRENT.text= DurationFormat(mediaPlayer!!.currentPosition.toLong())
            PlayerFragment.binding.SeekBarDuration.progress=mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    //если происходит звонок(?)
    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange <= 0){
            //pause music
            PlayerFragment.binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_play_arrow_24)
            NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_play_arrow_24)
            PlayerFragment.isPlaying = false
            mediaPlayer!!.pause()
            showNotification(R.drawable.icon)

        }
//        else{
//            //воспроизведение
//            PlayerFragment.binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
//            NowPlaying.binding.playPauseBTNNP.setImageResource(R.drawable.baseline_pause_24)
//            PlayerFragment.isPlaying = true
//            mediaPlayer!!.start()
//            showNotification(R.drawable.baseline_pause_24)
//        }
    }
    //for making persistent
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}