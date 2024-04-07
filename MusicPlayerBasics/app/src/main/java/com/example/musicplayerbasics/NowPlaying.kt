package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.FragmentNowPlayingBinding

class NowPlaying : Fragment() {

companion object{
    @SuppressLint("StaticFieldLeak")
    lateinit var binding:FragmentNowPlayingBinding

    fun newInstance(): NowPlaying {
        return NowPlaying()
    }

}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex],true)
        val  view=inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding=FragmentNowPlayingBinding.bind(view)
        binding.root.visibility=View.INVISIBLE
        binding.songNP.isSelected=true
        binding.playPauseBTNNP.setOnClickListener {
            if(!PlayerFragment.isPlaying){
                playMusic()
            }
            else{
                pauseMusic()
            }
        }
        binding.nextBTNNP.setOnClickListener {
           prevNextSong(true,requireContext())
        }
        binding.prevBTN.setOnClickListener {
           prevNextSong(false,requireContext())
        }
        binding.root.setOnClickListener {
            val playerFragment = PlayerFragment.newInstance()

            val bundle = Bundle()
            bundle.putInt("index", PlayerFragment.songPosition)
            bundle.putString("class", "NowPlaying")
            playerFragment.arguments = bundle

            playerFragment.show((context as AppCompatActivity).supportFragmentManager, playerFragment.tag)
        }
        binding.root.setOnLongClickListener {
            if(PlayerFragment.musicService!=null) {
              clearNP()
            }
            true
        }


        return view
    }

    fun clearNP() {
        PlayerFragment.nowPlayingId=""
        PlayerFragment.fIndex=-1
        PlayerFragment.musicService!!.audioManager.abandonAudioFocus(PlayerFragment.musicService)
        PlayerFragment.musicService!!.stopForeground(true)
        PlayerFragment.musicService!!.mediaPlayer!!.stop()
        PlayerFragment.musicService = null
        binding.root.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
       initialized()
    }
    //инициализация
    private fun initialized(){
        try {
            if (PlayerFragment.musicService != null) {
                binding.root.visibility = View.VISIBLE
                binding.songNP.isSelected = true
                Glide.with(this@NowPlaying)
                    .load(PlayerFragment.musicListPA[PlayerFragment.songPosition].artURI)
                    .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
                    .into(binding.albumNP)
                binding.songNP.text = PlayerFragment.musicListPA[PlayerFragment.songPosition].title
                binding.artistNP.text=PlayerFragment.musicListPA[PlayerFragment.songPosition].artist

                PlayerFragment.musicService!!.showNotification(R.drawable.baseline_pause_24)

                if (PlayerFragment.isPlaying) {
                    binding.playPauseBTNNP.setImageResource(R.drawable.baseline_pause_24)
                } else {
                    binding.playPauseBTNNP.setImageResource(R.drawable.baseline_play_arrow_24)
                }
            }
        }
        catch (ex:Exception){
            PlayerFragment.binding.songTITLE.text=ex.toString()
            binding.songNP.text="1"
        }
    }
    //воспроизведение
    fun playMusic(){
        PlayerFragment.isPlaying = true
        PlayerFragment.musicService!!.mediaPlayer!!.start()
        binding.playPauseBTNNP.setImageResource(R.drawable.baseline_pause_24)
        PlayerFragment.musicService!!.showNotification(R.drawable.baseline_pause_24)
    }
    //пауза
    fun pauseMusic(){
        PlayerFragment.isPlaying = false
        PlayerFragment.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBTNNP.setImageResource(R.drawable.baseline_play_arrow_24)
        PlayerFragment.musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
    }
    //след\пред композиция
    private  fun prevNextSong(increment:Boolean,context: Context){
        songPosition(increment=increment)
        PlayerFragment.musicService!!.createMP()
        Glide.with(context)
            .load(PlayerFragment.musicListPA[PlayerFragment.songPosition].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(binding.albumNP)
        binding.songNP.text = PlayerFragment.musicListPA[PlayerFragment.songPosition].title
        binding.artistNP.text=PlayerFragment.musicListPA[PlayerFragment.songPosition].artist
        PlayerFragment.musicService!!.showNotification(R.drawable.baseline_pause_24)
        playMusic()
    }
}



