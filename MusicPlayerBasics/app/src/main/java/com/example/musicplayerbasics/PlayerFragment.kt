package com.example.musicplayerbasics

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlayerFragment : BottomSheetDialogFragment() {
    private lateinit var binding: ActivityPlayerBinding
    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }

        lateinit var musicListPA:ArrayList<Music>
        private var songPosition:Int=0
        var mediaPlayer:MediaPlayer?=null
        var isPlaying:Boolean=false
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet: FrameLayout = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        // Height of the view
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        // Behavior of the bottom sheet
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels // Pop-up height
            state = BottomSheetBehavior.STATE_EXPANDED // Expanded state

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
       songInitialization()

        binding.btnPAUSEPLAY.setOnClickListener {
            if (isPlaying){
                pauseMusic()
            }
            else{
                playMusic()
            }
        }

    }
    private fun songInitialization(){
    songPosition= arguments?.getInt("index",0)!!
    val classType = arguments?.getString("class")

    if (songPosition != null && classType != null) {
        when (classType) {
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
                createMP()
            }
        }
    }
}
    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(binding.albumIMG)

        binding.songTITLE.text= musicListPA[songPosition].title +"\n"+musicListPA[songPosition].artist

    }
    private fun createMP(){
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            isPlaying=true
            binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
        }
        catch (ex:Exception){
            return
        }
    }
    private fun playMusic(){
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_pause_24)
        isPlaying=true
        mediaPlayer!!.start()
    }
    private fun pauseMusic(){
        binding.btnPAUSEPLAY.setIconResource(R.drawable.baseline_play_arrow_24)
        isPlaying=false
        mediaPlayer!!.pause()
    }
    override fun onDestroy() {
        super.onDestroy()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}
