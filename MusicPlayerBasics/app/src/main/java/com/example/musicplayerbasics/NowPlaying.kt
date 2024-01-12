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

        val  view=inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding=FragmentNowPlayingBinding.bind(view)
        binding.songNP.isSelected=true
        binding.root.visibility=View.INVISIBLE
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
        binding.root.setOnClickListener {
            val playerFragment = PlayerFragment.newInstance()

            val bundle = Bundle()
            bundle.putInt("index", PlayerFragment.songPosition)
            bundle.putString("class", "NowPlaying")
            playerFragment.arguments = bundle

            playerFragment.show((context as AppCompatActivity).supportFragmentManager, playerFragment.tag)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
       initialized()
    }
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

                if (PlayerFragment.isPlaying) {
                    binding.playPauseBTNNP.setIconResource(R.drawable.baseline_pause_24)
                } else {
                    binding.playPauseBTNNP.setIconResource(R.drawable.baseline_play_arrow_24)
                }
            }
        }
        catch (ex:Exception){
            binding.songNP.text="1"
        }
    }
    //воспроизведение
    private fun playMusic() {
        PlayerFragment.musicService!!.mediaPlayer!!.start()
        binding.playPauseBTNNP.setIconResource(R.drawable.baseline_pause_24)
        PlayerFragment.musicService!!.showNotification(R.drawable.baseline_pause_24)
        PlayerFragment.binding.btnNEXT.setIconResource(R.drawable.baseline_pause_24)
        PlayerFragment.isPlaying=true
    }

    //пауза
    private fun pauseMusic() {
        PlayerFragment.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBTNNP.setIconResource(R.drawable.baseline_play_arrow_24)
        PlayerFragment.musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        PlayerFragment.binding.btnNEXT.setIconResource(R.drawable.baseline_play_arrow_24)
        PlayerFragment.isPlaying=false
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